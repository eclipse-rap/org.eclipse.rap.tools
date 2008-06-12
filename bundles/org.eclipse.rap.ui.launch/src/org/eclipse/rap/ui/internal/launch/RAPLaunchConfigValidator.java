/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;


public final class RAPLaunchConfigValidator {
  
  private static final String RAP_LAUNCH_CONFIG_TYPE 
    = "org.eclipse.rap.ui.launch.RAPLauncher"; //$NON-NLS-1$

  private static final String EMPTY = ""; //$NON-NLS-1$
  
  private final RAPLaunchConfig config;

  public RAPLaunchConfigValidator( final RAPLaunchConfig config ) {
    this.config = config;
  }

  public IStatus[] validate() {
    List states = new ArrayList();
    try {
      states.add( validateServletName() );
      states.add( validateEntryPoint() );
      states.add( validatePort() );
      states.add( validateUniquePort() );
      states.add( validateURL() );
    } catch( CoreException e ) {
      String text 
        = "An error occured while validating the launch configuration: {0}";
      Object[] args = new Object[] { e.getLocalizedMessage() };
      String msg = MessageFormat.format( text, args );
      states.add( createError( msg, e ) ); 
    }
    IStatus[] result = new IStatus[ states.size() ];
    states.toArray( result );
    return result;
  }
  
  private IStatus validateServletName() throws CoreException {
    IStatus result = Status.OK_STATUS;
    if( EMPTY.equals( config.getServletName() ) ) {
      result = createError( "The servlet name must not be empty", null );
    }
    return result;
  }

  private IStatus validateEntryPoint() throws CoreException {
    IStatus result = Status.OK_STATUS;
    if( EMPTY.equals( config.getEntryPoint() ) ) { 
      result = createError( "The entry point must not be empty", null );
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
        result = createError( msg, null );
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
        result = createWarning( msg, null );
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
      result = createWarning( text, e );
    }
    return result;
  }

  /////////////////////////
  // Status creation helper
  
  private IStatus createWarning( final String msg, final Throwable thr ) {
    return new Status( IStatus.WARNING, Activator.getPluginId(), msg, thr );
  }

  private IStatus createError( final String msg, final Throwable thr ) {
    return new Status( IStatus.ERROR, Activator.getPluginId(), msg, thr );
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
