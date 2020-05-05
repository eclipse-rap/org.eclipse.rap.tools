/*******************************************************************************
 * Copyright (c) 2007, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.internal;

import static java.util.Arrays.asList;
import static org.eclipse.pde.internal.launching.launcher.LauncherUtils.clearWorkspace;

import java.io.IOException;
import java.net.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.*;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.pde.launching.EquinoxLaunchConfiguration;
import org.eclipse.rap.tools.launch.internal.RAPLaunchConfig.BrowserMode;
import org.eclipse.rap.tools.launch.internal.util.ErrorUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;


public final class RAPLaunchDelegate extends EquinoxLaunchConfiguration {

  // VM argument contants
  private static final String VMARG_PORT
    = "-Dorg.osgi.service.http.port="; //$NON-NLS-1$
  private static final String VMARG_DEVELOPMENT_MODE
    = "-Dorg.eclipse.rap.rwt.developmentMode="; //$NON-NLS-1$
  private static final String VMARG_SESSION_TIMEOUT
    = "-Dorg.eclipse.equinox.http.jetty.context.sessioninactiveinterval="; //$NON-NLS-1$
  private static final String VMARG_CONTEXT_PATH
    = "-Dorg.eclipse.equinox.http.jetty.context.path="; //$NON-NLS-1$

  private static final int CONNECT_TIMEOUT = 20000; // 20 Seconds

  private ILaunch launch;
  private RAPLaunchConfig config;
  private int port;
  private final boolean testMode;

  public RAPLaunchDelegate() {
    this( false );
  }

  public RAPLaunchDelegate( boolean testMode ) {
    this.testMode = testMode;
  }

  @Override
  public void launch( ILaunchConfiguration config,
                      String mode,
                      ILaunch launch,
                      IProgressMonitor monitor )
    throws CoreException
  {
    SubProgressMonitor subMonitor = doPreLaunch( config, launch, monitor );
    super.launch( config, mode, launch, subMonitor );
  }

  public SubProgressMonitor doPreLaunch( ILaunchConfiguration config,
                                         ILaunch launch,
                                         IProgressMonitor monitor )
    throws CoreException
  {
    // As this is the first method that is called after creating an instance
    // of RAPLaunchDelegate, store the config and launch parameters to be
    // accessible from member methods
    // TODO [rh] find a better way
    this.launch = launch;
    this.config = new RAPLaunchConfig( config );
    SubProgressMonitor subMonitor;
    subMonitor = new SubProgressMonitor( monitor, IProgressMonitor.UNKNOWN );
    terminateIfRunning( subMonitor );
    subMonitor = new SubProgressMonitor( monitor, IProgressMonitor.UNKNOWN );
    warnIfPortBusy( subMonitor );
    subMonitor = new SubProgressMonitor( monitor, IProgressMonitor.UNKNOWN );
    port = determinePort( subMonitor );
    if( this.config.getOpenBrowser() ) {
      registerBrowserOpener();
    }
    return subMonitor;
  }

  @Override
  public String[] getVMArguments( ILaunchConfiguration config ) throws CoreException {
    List<String> list = new ArrayList<String>();
    // ORDER IS CRUCIAL HERE:
    // Override VM arguments that are specified manually with the values
    // necessary for the RAP launcher
    list.addAll( asList( super.getVMArguments( config ) ) );
    list.addAll( getRAPVMArguments() );
    return toStringArray( list );
  }

  @Override
  public String[] getProgramArguments( ILaunchConfiguration configuration ) throws CoreException {
    List<String> programArguments = new ArrayList<String>();
    programArguments.addAll( asList( super.getProgramArguments( configuration ) ) );
    String dataLocationResolved = getResolvedDataLoacation();
    if( dataLocationResolved.length() > 0 ) {
      programArguments.addAll( asList( "-data", dataLocationResolved ) );
    }
    return toStringArray( programArguments );
  }

  private String getResolvedDataLoacation() throws CoreException {
    String dataLocation = config.getDataLocation();
    return resolveVariables( dataLocation );
  }

  private String resolveVariables( String dataLocation ) throws CoreException {
    VariablesPlugin variablePlugin = VariablesPlugin.getDefault();
    IStringVariableManager stringVariableManager = variablePlugin.getStringVariableManager();
    return stringVariableManager.performStringSubstitution( dataLocation );
  }

  private List<String> getRAPVMArguments() throws CoreException {
    List<String> arguments = new ArrayList<String>();
    arguments.add( VMARG_PORT + port );
    arguments.add( VMARG_DEVELOPMENT_MODE + config.getDevelopmentMode() );
    if( config.getUseSessionTimeout() ) {
      arguments.add( VMARG_SESSION_TIMEOUT + config.getSessionTimeout() );
    } else {
      arguments.add( VMARG_SESSION_TIMEOUT + RAPLaunchConfig.MIN_SESSION_TIMEOUT );
    }
    if( config.getUseManualContextPath() ) {
      String contextPath = config.getContextPath();
      if( !contextPath.startsWith( URLBuilder.SLASH ) ) {
        contextPath = URLBuilder.SLASH + contextPath;
      }
      if( contextPath.endsWith( URLBuilder.SLASH ) ) {
        contextPath = contextPath.substring( 0, contextPath.length() - 1 );
      }
      arguments.add( VMARG_CONTEXT_PATH + contextPath );
    }
    return arguments;
  }

  private static String[] toStringArray( List<String> list ) {
    String[] result = new String[ list.size() ];
    list.toArray( result );
    return result;
  }

  private void warnIfPortBusy( SubProgressMonitor monitor ) throws CoreException {
    String taskName = LaunchMessages.RAPLaunchDelegate_CheckPortTaskName;
    monitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
    try {
      if( config.getUseManualPort() && isPortBusy( config.getPort() ) ) {
        DebugPlugin debugPlugin = DebugPlugin.getDefault();
        IStatusHandler prompter = debugPlugin.getStatusHandler( promptStatus );
        if( prompter != null ) {
          IStatus status = PortBusyStatusHandler.STATUS;
          Object resolution = prompter.handleStatus( status, config );
          if( Boolean.FALSE.equals( resolution ) ) {
            String text = LaunchMessages.RAPLaunchDelegate_PortInUse;
            Object[] args = new Object[] {
              new Integer( config.getPort() ),
              config.getName()
            };
            String msg = MessageFormat.format( text, args );
            String pluginId = Activator.PLUGIN_ID;
            Status infoStatus = new Status( IStatus.INFO, pluginId, msg );
            throw new CoreException( infoStatus );
          }
        }
      }
    } finally {
      monitor.done();
    }
  }

  private int determinePort( IProgressMonitor monitor ) throws CoreException {
    int result;
    String taskName = LaunchMessages.RAPLaunchDelegate_DeterminePortTaskName;
    monitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
    try {
      if( config.getUseManualPort() ) {
        result = config.getPort();
      } else {
        result = findFreePort();
      }
    } finally {
      monitor.done();
    }
    return result;
  }

  private static int findFreePort() throws CoreException {
    try {
      ServerSocket server = new ServerSocket( 0 );
      try {
        return server.getLocalPort();
      } finally {
        server.close();
      }
    } catch( IOException e ) {
      String msg = "Could not obtain a free port number."; //$NON-NLS-1$
      String pluginId = Activator.getPluginId();
      Status status = new Status( IStatus.ERROR, pluginId, msg, e );
      throw new CoreException( status );
    }
  }

  private static boolean isPortBusy( int port ) {
    ServerSocket server = null;
    try {
      server = new ServerSocket( port );
    } catch( IOException e1 ) {
      // assume that port is occupied when getting here
    }
    if( server != null ) {
      try {
        server.close();
      } catch( IOException e ) {
        // ignore
      }
    }
    return server == null;
  }

  private URL getUrl() throws CoreException {
    try {
      String url = URLBuilder.fromLaunchConfig( config, port, testMode );
      return new URL( url );
    } catch( MalformedURLException e ) {
      String msg = "Invalid URL."; //$NON-NLS-1$
      String pluginId = Activator.getPluginId();
      Status status = new Status( IStatus.ERROR, pluginId, 0, msg, e );
      throw new CoreException( status );
    }
  }

  private void terminateIfRunning( IProgressMonitor monitor ) throws CoreException {
    String taskName = LaunchMessages.RAPLaunchDelegate_TerminatePreviousTaskName;
    monitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
    try {
      ILaunch runningLaunch = findRunning();
      if( runningLaunch != null ) {
        terminate( runningLaunch );
      }
    } finally {
      monitor.done();
    }
  }

  private ILaunch findRunning() {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    ILaunch[] runningLaunches = launchManager.getLaunches();
    ILaunch result = null;
    for( int i = 0; result == null && i < runningLaunches.length; i++ ) {
      ILaunch runningLaunch = runningLaunches[ i ];
      if(    runningLaunch != launch
          && config.getName().equals( getLaunchName( runningLaunch ) )
          && !runningLaunch.isTerminated() )
      {
        result = runningLaunches[ i ];
      }
    }
    return result;
  }

  private static String getLaunchName( ILaunch launch ) {
    ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
    // the launch config might be null (e.g. if deleted) even though there
    // still exists a launch for that config
    return launchConfiguration == null ? null : launchConfiguration.getName();
  }

  private static void terminate( final ILaunch previousLaunch ) throws DebugException {
    final Object signal = new Object();
    final boolean[] terminated = { false };
    DebugPlugin debugPlugin = DebugPlugin.getDefault();
    debugPlugin.addDebugEventListener( new IDebugEventSetListener() {
      @Override
      public void handleDebugEvents( DebugEvent[] events ) {
        for( DebugEvent event : events ) {
          if( isTerminateEventFor( event, previousLaunch ) ) {
            DebugPlugin.getDefault().removeDebugEventListener( this );
            synchronized( signal ) {
              terminated[ 0 ] = true;
              signal.notifyAll();
            }
          }
        }
      }
    } );
    previousLaunch.terminate();
    try {
      synchronized( signal ) {
        if( !terminated[ 0 ] ) {
          signal.wait();
        }
      }
    } catch( InterruptedException e ) {
      // ignore
    }
  }

  private static boolean isCreateEventFor( DebugEvent event, ILaunch launch ) {
    Object source = event.getSource();
    return    event.getKind() == DebugEvent.CREATE
           && source instanceof RuntimeProcess
           && ( ( RuntimeProcess ) source ).getLaunch() == launch;
  }

  private static boolean isTerminateEventFor( DebugEvent event, ILaunch launch ) {
    boolean result = false;
    if(    event.getKind() == DebugEvent.TERMINATE
        && event.getSource() instanceof RuntimeProcess )
    {
      RuntimeProcess process = ( RuntimeProcess )event.getSource();
      result = process.getLaunch() == launch;
    }
    return result;
  }

  private void waitForHttpService( IProgressMonitor monitor ) {
    SubProgressMonitor subMonitor = new SubProgressMonitor( monitor, 1 );
    String taskName = LaunchMessages.RAPLaunchDelegate_WaitForHTTPTaskName;
    subMonitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
    try {
      long start = System.currentTimeMillis();
      boolean canConnect = false;
      boolean interrupted = false;
      while(    System.currentTimeMillis() - start <= CONNECT_TIMEOUT
             && !canConnect
             && !interrupted
             && !monitor.isCanceled()
             && !launch.isTerminated() )
      {
        try {
          Socket socket = new Socket( URLBuilder.getHost(), port );
          socket.close();
          canConnect = true;
        } catch( Exception e ) {
          // http service not yet started - wait a bit
          try {
            Thread.sleep( 200 );
          } catch( InterruptedException ie ) {
            interrupted = true;
          }
        }
      }
    } finally {
      subMonitor.done();
    }
  }

  @Override
  public void clear( ILaunchConfiguration configuration, IProgressMonitor monitor )
    throws CoreException
  {
    clearDataLocation( configuration, monitor );
    super.clear( configuration, monitor );
  }

  private void clearDataLocation( ILaunchConfiguration configuration, IProgressMonitor monitor )
    throws CoreException
  {
    clearWorkspace( configuration, getResolvedDataLoacation(), monitor );
  }

  private void registerBrowserOpener() {
    DebugPlugin debugPlugin = DebugPlugin.getDefault();
    debugPlugin.addDebugEventListener( new IDebugEventSetListener() {
      @Override
      public void handleDebugEvents( DebugEvent[] events ) {
        for( DebugEvent event : events ) {
          if( isCreateEventFor( event, launch ) ) {
            DebugPlugin.getDefault().removeDebugEventListener( this );
            // Start a separate job to wait for the http service and launch the
            // browser. Otherwise we would block the application on whose
            // service we are waiting for
            final String jobTaskName = LaunchMessages.RAPLaunchDelegate_StartClientTaskName;
            Job job = new Job( jobTaskName ) {
              @Override
              protected IStatus run( IProgressMonitor monitor ) {
                String taskName = jobTaskName;
                monitor.beginTask( taskName, 2 );
                try {
                  waitForHttpService( monitor );
                  monitor.worked( 1 );
                  if( !launch.isTerminated() ) {
                    openBrowser( monitor );
                  }
                } finally {
                  monitor.done();
                }
                return Status.OK_STATUS;
              }
            };
            job.schedule();
          }
        }
      }
    } );
  }

  private void openBrowser( IProgressMonitor monitor ) {
    SubProgressMonitor subMonitor = new SubProgressMonitor( monitor, 1 );
    String taskName = LaunchMessages.RAPLaunchDelegate_StartClientTaskName;
    subMonitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
    try {
      URL url = null;
      try {
        url = getUrl();
        IWebBrowser browser = getBrowser();
        openUrl( browser, url );
      } catch( CoreException e ) {
        String text = LaunchMessages.RAPLaunchDelegate_OpenBrowserFailed;
        String msg = MessageFormat.format( text, new Object[]{ url } );
        ErrorUtil.show( msg, e );
      }
    } finally {
      subMonitor.done();
    }
  }

  private IWebBrowser getBrowser() throws CoreException {
    final IWebBrowser[] result = { null };
    final CoreException[] exception = { null };
    Display.getDefault().syncExec( new Runnable() {
      @Override
      public void run() {
        try {
          IWorkbench workbench = PlatformUI.getWorkbench();
          IWorkbenchBrowserSupport support = workbench.getBrowserSupport();
          int style = IWorkbenchBrowserSupport.LOCATION_BAR
                    | IWorkbenchBrowserSupport.NAVIGATION_BAR
                    | IWorkbenchBrowserSupport.STATUS;
          if( BrowserMode.EXTERNAL.equals( config.getBrowserMode() ) ) {
            style |= IWorkbenchBrowserSupport.AS_EXTERNAL;
          } else {
            style |= IWorkbenchBrowserSupport.AS_EDITOR;
          }
          // Starting the same launch first with the external, then with the
          // internal browser without restarting the workbench will still open
          // an external browser.
          // The fix is to append the browserMode to the id
          String id = config.getName() + config.getBrowserMode();
          String name = config.getName();
          String toolTip = config.getName();
          result[ 0 ] = support.createBrowser( style, id, name, toolTip );
        } catch( CoreException e ) {
          exception[ 0 ] = e;
        }
      }
    } );
    if( exception[ 0 ] != null ) {
      throw exception[ 0 ];
    }
    return result[ 0 ];
  }

  private static void openUrl( final IWebBrowser browser, final URL url ) throws PartInitException {
    final PartInitException[] exception = { null };
    Display.getDefault().asyncExec( new Runnable() {
      @Override
      public void run() {
        try {
          browser.openURL( url );
        } catch( PartInitException e ) {
          String text = LaunchMessages.RAPLaunchDelegate_OpenUrlFailed;
          String msg = MessageFormat.format( text, new Object[] { url } );
          String pluginId = Activator.getPluginId();
          Status status = new Status( IStatus.ERROR, pluginId, msg, e );
          exception[ 0 ] = new PartInitException( status );
        }
      }
    } );
    if( exception[ 0 ] != null ) {
      throw exception[ 0 ];
    }
  }

}
