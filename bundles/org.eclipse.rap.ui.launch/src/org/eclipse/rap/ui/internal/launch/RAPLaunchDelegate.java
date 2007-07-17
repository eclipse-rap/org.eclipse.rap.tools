/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.ui.internal.launch;

import java.net.*;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.pde.ui.launcher.EquinoxLaunchConfiguration;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig.BrowserMode;
import org.eclipse.rap.ui.internal.launch.util.ErrorUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;


public final class RAPLaunchDelegate extends EquinoxLaunchConfiguration {

  private static final String EMPTY = ""; //$NON-NLS-1$
  
  // VM argument contants
  private static final String VMARG_PORT 
    = "-Dorg.osgi.service.http.port="; //$NON-NLS-1$
  private static final String VMARG_LOG_LEVEL 
    = "-Dorg.eclipse.swt.clientLogLevel="; //$NON-NLS-1$
  
  // Constants to construct URL
  private static final String URL_PROTOCOL = "http"; //$NON-NLS-1$
  private static final String URL_HOST = "127.0.0.1"; //$NON-NLS-1$
  private static final String URL_FILE = "/rap"; //$NON-NLS-1$
  private static final String URL_QUERY_STARTUP = "?w4t_startup="; //$NON-NLS-1$

  private static final int CONNECT_TIMEOUT = 20000; // 20 Seconds
  

  public void launch( final ILaunchConfiguration config,
                      final String mode,
                      final ILaunch launch,
                      final IProgressMonitor monitor ) 
    throws CoreException
  {
    SubProgressMonitor subMonitor;
    subMonitor = new SubProgressMonitor( monitor, IProgressMonitor.UNKNOWN );
    terminateIfRunning( launch, subMonitor );
    registerBrowserOpener( launch );
    super.launch( config, mode, launch, subMonitor );
  }

  ///////////////////////////////////////
  // EquinoxLaunchConfiguration overrides
  
  public String[] getVMArguments( final ILaunchConfiguration config )
    throws CoreException
  {
    List list = new ArrayList();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    list.add( VMARG_PORT + rapConfig.getPort() );
    list.add( VMARG_LOG_LEVEL + rapConfig.getLogLevel().getName() );
    list.addAll( Arrays.asList( super.getVMArguments( config ) ) );
    String[] result = new String[ list.size() ];
    list.toArray( result );
    return result;
  }
  
  //////////////////////////////////
  // Helping method to construct URL
  
  private static URL getUrl( final RAPLaunchConfig config ) 
    throws CoreException 
  {
    String entryPoint = config.getEntryPoint(); 
    String query = EMPTY; 
    if( !EMPTY.equals( entryPoint ) ) { 
      query = URL_QUERY_STARTUP + entryPoint;
    }
    int port = config.getPort();
    try {
      URL result = new URL( URL_PROTOCOL, URL_HOST, port, URL_FILE + query );
      return result;
    } catch( MalformedURLException e ) {
      String msg = "Invalid URL."; //$NON-NLS-1$
      String pluginId = Activator.getPluginId();
      Status status = new Status( IStatus.ERROR, pluginId, 0, msg, e );
      throw new CoreException( status );
    }
  }

  ///////////////////////////////////////////////////
  // Helping methods to detect already running launch
  
  private static void terminateIfRunning( final ILaunch launch, 
                                          final IProgressMonitor monitor ) 
    throws CoreException 
  {
    ILaunchConfiguration config = launch.getLaunchConfiguration();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    if( rapConfig.getTerminatePrevious() ) {
      String taskName = "Terminating previous launch";
      monitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
      try {
        final ILaunch runningLaunch = findRunning( launch );
        if( runningLaunch != null ) {
          terminate( runningLaunch );
        }
      } finally {
        monitor.done();
      }
    }
  }

  private static ILaunch findRunning( final ILaunch launch ) {
    ILaunchConfiguration config = launch.getLaunchConfiguration(); 
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    ILaunch[] runningLaunches = launchManager.getLaunches();
    ILaunch result = null;
    for( int i = 0; result == null && i < runningLaunches.length; i++ ) {
      ILaunch runningLaunch = runningLaunches[ i ];
      String runningName = runningLaunch.getLaunchConfiguration().getName();
      if( runningLaunch != launch && runningName.equals( config.getName() ) ) {
        result = runningLaunches[ i ];  
      }
    }
    return result;
  }
  
  private static void terminate( final ILaunch launch )
    throws DebugException
  {
    final Object signal = new Object();
    final boolean[] terminated = { false };
    DebugPlugin debugPlugin = DebugPlugin.getDefault();
    debugPlugin.addDebugEventListener( new IDebugEventSetListener() {
      public void handleDebugEvents( final DebugEvent[] events ) {
        for( int i = 0; i < events.length; i++ ) {
          DebugEvent event = events[ i ];
          if( isTerminateEventFor( event, launch ) ) {
            DebugPlugin.getDefault().removeDebugEventListener( this );
            terminated[ 0 ] = true;
            synchronized( signal ) {
              signal.notifyAll();
            }
          }
        }
      }
    } );
    launch.terminate();
    if( !terminated[ 0 ] ) {
      try {
        synchronized( signal ) {
          signal.wait();
        }
      } catch( InterruptedException e ) {
        // ignore
      }
    }
  }

  ////////////////////////////////////////////
  // Helping methods to evaluate debug events 
  
  private static boolean isCreateEventFor( final DebugEvent event, 
                                           final ILaunch launch ) 
  {
    Object source = event.getSource();
    int kind = event.getKind();
    return    kind == DebugEvent.CREATE 
           && source instanceof RuntimeProcess 
           && ( ( RuntimeProcess ) source ).getLaunch() == launch;
  }
  
  private static boolean isTerminateEventFor( final DebugEvent event, 
                                              final ILaunch launch ) 
  {
    boolean result = false;
    if(    event.getKind() == DebugEvent.TERMINATE 
        && event.getSource() instanceof RuntimeProcess ) 
    {
      RuntimeProcess process = ( RuntimeProcess )event.getSource();
      result = process.getLaunch() == launch;
    }
    return result;
  }
  
  /////////////////////////////////////
  // Helping methods to test connection
  
  private static void waitForHttpService( final ILaunch launch ) {
    ILaunchConfiguration config = launch.getLaunchConfiguration();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config ); 
    long start = System.currentTimeMillis();
    boolean canConnect = false;
    boolean interrupted = false;
    while(    System.currentTimeMillis() - start <= CONNECT_TIMEOUT 
           && !canConnect
           && !interrupted
           && !launch.isTerminated() ) 
    {
      try {
        Socket socket = new Socket( URL_HOST, rapConfig.getPort() );
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
  }
  
  /////////////////////////////////////////////////
  // Helping methods to create browser and open URL
  
  private static void registerBrowserOpener( final ILaunch launch ) {
    DebugPlugin debugPlugin = DebugPlugin.getDefault();
    debugPlugin.addDebugEventListener( new IDebugEventSetListener() {
      public void handleDebugEvents( final DebugEvent[] events ) {
        for( int i = 0; i < events.length; i++ ) {
          DebugEvent event = events[ i ];
          if( isCreateEventFor( event, launch ) ) {
            DebugPlugin.getDefault().removeDebugEventListener( this );
            waitForHttpService( launch );
            openBrowser( launch );
          }
        }
      }
    } );
  }
  
  private static void openBrowser( final ILaunch launch ) {
    ILaunchConfiguration config = launch.getLaunchConfiguration();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    URL url = null;
    try {
      url = getUrl( rapConfig );
      IWebBrowser browser = getBrowser( rapConfig );
      openUrl( browser, url );
    } catch( final CoreException e ) {
      String text = "Failed to open browser for URL ''{0}''.";
      String msg = MessageFormat.format( text, new Object[]{ url } );
      ErrorUtil.show( msg, e );
    }
  }
  
  private static IWebBrowser getBrowser( final RAPLaunchConfig config ) 
    throws CoreException
  {
    final IWebBrowser[] result = { null };
    final CoreException[] exception = { null };
    Display.getDefault().syncExec( new Runnable() {
      public void run() {
        try {
          IWorkbench workbench = PlatformUI.getWorkbench();
          IWorkbenchBrowserSupport support = workbench.getBrowserSupport();
          int style 
            = IWorkbenchBrowserSupport.LOCATION_BAR 
            | IWorkbenchBrowserSupport.NAVIGATION_BAR 
            | IWorkbenchBrowserSupport.STATUS;
          if( BrowserMode.EXTERNAL.equals( config.getBrowserMode() ) ) {
            style |= IWorkbenchBrowserSupport.AS_EXTERNAL;
          } else {
            style |= IWorkbenchBrowserSupport.AS_EDITOR;
          }
          String id = config.getName();
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
  
  private static void openUrl( final IWebBrowser browser, final URL url ) 
    throws PartInitException 
  {
    final PartInitException[] exception = { null };
    Display.getDefault().asyncExec( new Runnable() {
      public void run() {
        try {
          browser.openURL( url );
        } catch( PartInitException e ) {
          String text = "Failed to open URL ''{0}'' in browser.";
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
