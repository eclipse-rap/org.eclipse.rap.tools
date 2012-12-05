/*******************************************************************************
 * Copyright (c) 2007, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.pde.internal.launching.launcher.LaunchArgumentsHelper;


public final class RAPLaunchConfigValidator {

  // FIXME remove this when Platfrom.WS_RAP defined
  public static final String WS_RAP = "rap"; //$NON-NLS-1$
  public static final int ERR_SERVLET_PATH = 6001;
  public static final int ERR_PORT = 6004;
  public static final int ERR_URL = 6005;
  public static final int ERR_TIMEOUT = 6007;
  public static final int ERR_ENTRY_POINT = 6008;
  public static final int ERR_SERVLET_BUNDLE = 6009;
  public static final int ERR_DATA_LOCATION = 6010;
  public static final int ERR_CONTEXT_PATH = 6011;
  public static final int WARN_OSGI_FRAMEWORK = 7002;
  public static final int WARN_WS_WRONG = 7003;
  public static final int WARN_SERVLET_PATH = 7004;
  private static final String RAP_LAUNCH_CONFIG_TYPE = "org.eclipse.rap.ui.launch.RAPLauncher"; //$NON-NLS-1$
  private static final String EMPTY = ""; //$NON-NLS-1$
  private static final String PARAM_WS = "-ws";//$NON-NLS-1$

  private final RAPLaunchConfig config;

  public RAPLaunchConfigValidator( final RAPLaunchConfig config ) {
    this.config = config;
  }

  public IStatus[] validate() {
    List states = new ArrayList();
    try {
      addNonOKState( states, validateServletPath() );
      addNonOKState( states, validatePort() );
      addNonOKState( states, validateUniquePort() );
      addNonOKState( states, validateContextPath() );
      addNonOKState( states, validateURL() );
      addNonOKState( states, validateSessionTimeout() );
      addNonOKState( states, validateDataLocation() );
// TODO [rst] Re-enable when bug 338544 is fixed
//      addNonOKState( states, validateWS() );
    } catch( final CoreException e ) {
      String text
        = LaunchMessages.RAPLaunchConfigValidator_ErrorWhileValidating;
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

  private IStatus validateDataLocation() throws CoreException {
    IStatus result = Status.OK_STATUS;
    String dataLocation = config.getDataLocation();
    if( dataLocation == null || dataLocation.length() == 0 ) {
      String msg = LaunchMessages.RAPLaunchConfigValidator_DataLocationErrorMsg;
      result = createError( msg, ERR_DATA_LOCATION, null );
    }
    return result;
  }

  private IStatus validateServletPath() throws CoreException {
    IStatus result = Status.OK_STATUS;
    String servletPath = config.getServletPath();
    if( servletPath == null || EMPTY.equals( servletPath ) ) {
      String msg = LaunchMessages.RAPLaunchConfigValidator_ServletPathEmpty;
      result = createError( msg, ERR_SERVLET_PATH, null );
    } else if( !servletPath.startsWith( "/" ) ) {
      String msg = LaunchMessages.RAPLaunchConfigValidator_ServletPathLeadingSlash;
      result = createWarning( msg, WARN_SERVLET_PATH, null );
    }
    return result;
  }

  private IStatus validateContextPath() throws CoreException {
    IStatus result = Status.OK_STATUS;
    boolean useManualContextPath = config.getUseManualContextPath();
    if( useManualContextPath ) {
      String contextPath = config.getContextPath();
      boolean isValid = isValidContextPath( contextPath );
      if( !isValid ) {
        String unformatedMsg = LaunchMessages.RAPLaunchConfigValidator_InvalidContextPath;
        String msg = MessageFormat.format( unformatedMsg, new Object[] { contextPath } );
        result = createError( msg, ERR_CONTEXT_PATH, null );
      }
    }
    return result;
  }

  private boolean isValidContextPath( String contextPath ) {
    boolean result = true;
    int length = contextPath.length();
    if( contextPath.indexOf( "//" ) != -1 ) {
      result = false;
    }
    for( int i = 0; i < length && result; i++ ) {
      char ch = contextPath.charAt( i );
      boolean isLetterOrDigit = Character.isLetterOrDigit( ch );
      boolean isValidSpecialChar = "/_-.".indexOf( ch ) != -1;
      if( !isLetterOrDigit && !isValidSpecialChar ) {
        result = false;
      }
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
        String text = LaunchMessages.RAPLaunchConfigValidator_PortNumberInvalid;
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
        String text = LaunchMessages.RAPLaunchConfigValidator_PortInUse;
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
      String url = URLBuilder.fromLaunchConfig( config, 80, false );
      new URI( url );
    } catch( URISyntaxException exception ) {
      String text = LaunchMessages.RAPLaunchConfigValidator_MalformedUrl;
      result = createError( text, ERR_URL, exception );
    }
    return result;
  }

  private IStatus validateSessionTimeout() throws CoreException {
    IStatus result = Status.OK_STATUS;
    boolean isValid
      = config.getSessionTimeout() >= RAPLaunchConfig.MIN_SESSION_TIMEOUT;
    if( !isValid ) {
      String msg = LaunchMessages.RAPLaunchConfigValidator_TimeoutInvalid;
      result = createError( msg, ERR_TIMEOUT, null );
    }
    return result;
  }

  private IStatus validateWS() throws CoreException {
    IStatus result = Status.OK_STATUS;
    final ILaunchConfiguration launchConfig = config.getUnderlyingLaunchConfig();
    String[] programArguments = LaunchArgumentsHelper.getUserProgramArgumentArray( launchConfig );
    String wsValue = extractParameterValue( PARAM_WS, programArguments );
    String warnMessagePattern = null;
    if( wsValue == null || "".equals( wsValue ) ) {
      warnMessagePattern = LaunchMessages.RAPLaunchConfigValidator_WsEmpty;
    } else if ( !WS_RAP.equals( wsValue ) ) {
      warnMessagePattern = LaunchMessages.RAPLaunchConfigValidator_WsWrong;
    }
    if( warnMessagePattern != null ) {
      final Object[] args = new Object[]{ PARAM_WS, WS_RAP };
      String warnMessage = MessageFormat.format( warnMessagePattern, args );
      result = createWarning( warnMessage, WARN_WS_WRONG, null );
    }
    return result;
  }

  private String extractParameterValue( String name, String[] parameters ) {
    String result = null;
    for( int i = 0; i < parameters.length; i++ ) {
      String key = parameters[ i ];
      if( name.equals( key ) && i < parameters.length - 1 ) {
        result = parameters[ i + 1 ];
      }
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

}
