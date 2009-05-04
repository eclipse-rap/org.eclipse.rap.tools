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

import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.launcher.OSGiFrameworkManager;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;


public final class RAPLaunchConfigValidator {

  public static final int ERR_SERVLET_NAME = 6001;
  public static final int ERR_ENTRY_POINT_EMPTY = 6002;
  public static final int ERR_PORT = 6004;
  public static final int ERR_URL = 6005;
  public static final int ERR_LOG_LEVEL = 6006;

  public static final int WARN_OSGI_FRAMEWORK = 7002;

  private static final String RAP_LAUNCH_CONFIG_TYPE
    = "org.eclipse.rap.ui.launch.RAPLauncher"; //$NON-NLS-1$
  
  private static final String EQUINOX_FRAMEWORK
    = "org.eclipse.pde.ui.EquinoxFramework"; //$NON-NLS-1$

  private static final String EMPTY = ""; //$NON-NLS-1$


  private final RAPLaunchConfig config;

  public RAPLaunchConfigValidator( final RAPLaunchConfig config ) {
    this.config = config;
  }

  public IStatus[] validate() {
    List states = new ArrayList();
    try {
      addNonOKState( states, validateServletName() );
      addNonOKState( states, validateEntryPoint() );
      addNonOKState( states, validatePort() );
      addNonOKState( states, validateUniquePort() );
      addNonOKState( states, validateURL() );
      addNonOKState( states, validateLogLevel() );
      addNonOKState( states, validateOSGiFramework() );
    } catch( CoreException e ) {
      String text
        = "An error occured while validating the launch configuration: {0}";
      Object[] args = new Object[] { e.getLocalizedMessage() };
      String msg = MessageFormat.format( text, args );
      states.add( createError( msg, 0, e ) );
    }
    IStatus[] result = new IStatus[ states.size() ];
    states.toArray( result );
    return result;
  }

  /////////////////////
  // Validation methods

  private IStatus validateServletName() throws CoreException {
    IStatus result = Status.OK_STATUS;
    if( EMPTY.equals( config.getServletName() ) ) {
      String msg = "The servlet name must not be empty";
      result = createError( msg, ERR_SERVLET_NAME, null );
    }
    return result;
  }

  private IStatus validateEntryPoint() throws CoreException {
    IStatus result = Status.OK_STATUS;
    String entryPoint = config.getEntryPoint();
    if( EMPTY.equals( entryPoint ) ) {
      String msg = "The entry point must not be empty";
      result = createError( msg, ERR_ENTRY_POINT_EMPTY, null );
    }
    return result;
  }

  private IStatus validatePort() throws CoreException {
    IStatus result = Status.OK_STATUS;
    if( config.getUseManualPort() ) {
      int port = config.getPort();
      if(    port < RAPLaunchConfig.MIN_PORT_NUMBER
          || port > RAPLaunchConfig.MAX_PORT_NUMBER )
      {
        String text = "Port number must be between {0} and {1}";
        Object[] args = new Object[] {
          new Integer( RAPLaunchConfig.MIN_PORT_NUMBER ),
          new Integer( RAPLaunchConfig.MAX_PORT_NUMBER )
        };
        String msg = MessageFormat.format( text, args );
        result = createError( msg, ERR_PORT, null );
      }
    }
    return result;
  }

  private IStatus validateUniquePort() throws CoreException {
    IStatus result = Status.OK_STATUS;
    if( config.getUseManualPort() ) {
      RAPLaunchConfig duplicate = null;
      ILaunchConfiguration[] launchConfigs = getLaunchConfigs();
      for( int i = 0; duplicate == null && i < launchConfigs.length; i++ ) {
        RAPLaunchConfig otherConfig = new RAPLaunchConfig( launchConfigs[ i ] );
        if( hasSamePort( otherConfig ) ) {
          duplicate = otherConfig;
        }
      }
      if( duplicate != null ) {
        String text = "The port {0,number,#} is already used by {1}.";
        Object[] args = new Object[] {
          new Integer( config.getPort() ),
          duplicate.getName()
        };
        String msg = MessageFormat.format( text, args );
        result = createWarning( msg, 0, null );
      }
    }
    return result;
  }

  private IStatus validateURL() throws CoreException {
    IStatus result = Status.OK_STATUS;
    try {
      URLBuilder.fromLaunchConfig( config, 80, false );
    } catch( MalformedURLException e ) {
      String text = "Servlet name and/or entry point cause a malformed URL.";
      result = createWarning( text, ERR_URL, e );
    }
    return result;
  }

  private IStatus validateLogLevel() throws CoreException {
    IStatus result = Status.OK_STATUS;
    boolean isValid = false;
    Level logLevel = config.getLogLevel();
    for( int i = 0; !isValid && i < RAPLaunchConfig.LOG_LEVELS.length; i++ ) {
      if( RAPLaunchConfig.LOG_LEVELS[ i ].equals( logLevel ) ) {
        isValid = true;
      }
    }
    if( !isValid ) {
      Object[] args = new Object[] { logLevel.getName() };
      String msg = MessageFormat.format( "Invalid log level: ''{0}''.", args );
      result = createError( msg, ERR_LOG_LEVEL, null );
    }
    return result;
  }
  
  private IStatus validateOSGiFramework() throws CoreException {
    IStatus result = Status.OK_STATUS;
    String frameworkId = getOSGiFrameworkId();
    if( !EQUINOX_FRAMEWORK.equals( frameworkId ) ) {
      String msg = "The RAP launcher only works with the Equinox OSGi Framework";
      result = createWarning( msg, WARN_OSGI_FRAMEWORK, null );
    }
    return result;
  }

  /////////////////////////
  // Status creation helper

  private void addNonOKState( final List states, final IStatus state ) {
    if( state != null && !state.isOK() ) {
      states.add( state );
    }
  }

  private IStatus createWarning( final String msg,
                                 final int code,
                                 final Throwable thr )
  {
    String pluginId = Activator.getPluginId();
    return new Status( IStatus.WARNING, pluginId, code, msg, thr );
  }

  private IStatus createError( final String msg,
                               final int code,
                               final Throwable thr )
  {
    return new Status( IStatus.ERROR, Activator.getPluginId(), code, msg, thr );
  }

  /////////////////////////////////////////
  // Helping methods for validateUniquePort

  private static ILaunchConfiguration[] getLaunchConfigs() throws CoreException
  {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    ILaunchConfigurationType type
      = launchManager.getLaunchConfigurationType( RAP_LAUNCH_CONFIG_TYPE );
    return launchManager.getLaunchConfigurations( type );
  }

  private boolean hasSamePort( final RAPLaunchConfig otherConfig )
    throws CoreException
  {
    return    otherConfig.getUseManualPort()
           && !config.getName().equals( otherConfig.getName() )
           && config.getPort() == otherConfig.getPort();
  }
  
  ////////////////////////////////////////////
  // Helping methods for validateOSGiFramework

  private String getOSGiFrameworkId() throws CoreException {
    ILaunchConfiguration launchConfig = config.getUnderlyingLaunchConfig();
    OSGiFrameworkManager manager
      = PDEPlugin.getDefault().getOSGiFrameworkManager();
    String defaultFrameworkId = manager.getDefaultFramework();
    String attributeName = IPDELauncherConstants.OSGI_FRAMEWORK_ID;
    return launchConfig.getAttribute( attributeName, defaultFrameworkId );
  }
}
