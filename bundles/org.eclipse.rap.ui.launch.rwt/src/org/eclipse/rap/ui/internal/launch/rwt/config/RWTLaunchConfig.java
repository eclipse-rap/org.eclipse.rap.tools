/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.config;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.*;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;


public final class RWTLaunchConfig {
  
  static final String LAUNCH_CONFIG_TYPE 
    = "org.eclipse.rap.ui.internal.launch.rwt.RWTLaunchConfigType"; //$NON-NLS-1$

  public static final int MIN_PORT_NUMBER = 0;
  public static final int MAX_PORT_NUMBER = 65535;

  // Attribute names from JDT's Java Launcher
  private static final String PROJECT_NAME
    = IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
  public static final String WORKING_DIRECTORY
    = IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY;
  private static final String VM_ARGUMENTS 
    = IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;

  // Attribute names specifiec to the RWT launcher
  private static final String PREFIX = "org.eclipse.rap.launch.rwt."; //$NON-NLS-1$
  private static final String ENTRY_POINT = PREFIX + "entryPoint"; //$NON-NLS-1$
  private static final String USE_WEB_XML = PREFIX + "useWebXml"; //$NON-NLS-1$
  private static final String WEB_XML_LOCATION = PREFIX + "webXmlLocation"; //$NON-NLS-1$
  private static final String SERVLET_PATH = PREFIX + "servletPath"; //$NON-NLS-1$
  private static final String USE_MANUAL_PORT = PREFIX + "useManualPort"; //$NON-NLS-1$
  private static final String PORT = PREFIX + "port"; //$NON-NLS-1$
  private static final String OPEN_BROWSER = PREFIX + "openBrowser"; //$NON-NLS-1$
  private static final String BROWSER_MODE = PREFIX + "browserMode"; //$NON-NLS-1$

  // Default values for attributes
  private static final String DEFAULT_ENTRY_POINT = ""; //$NON-NLS-1$
  private static final String DEFAULT_PROJECT_NAME = ""; //$NON-NLS-1$
  private static final String DEFAULT_VM_ARGUMENTS = ""; //$NON-NLS-1$
  private static final String DEFAULT_WEB_XML_LOCATION = ""; //$NON-NLS-1$
  private static final String DEFAULT_SERVLET_PATH = ""; //$NON-NLS-1$
  private static final boolean DEFAULT_USE_WEB_XML = false;
  private static final String DEFAULT_WORKING_DIRECTORY = null;
  private static final boolean DEFAULT_USE_MANUAL_PORT = false;
  private static final int DEFAULT_PORT = 8080;
  private static final boolean DEFAULT_OPEN_BROWSER = true;
  private static final String DEFAULT_BROWSER_MODE = BrowserMode.INTERNAL.toString();


  
  public static ILaunchConfigurationType getType() {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    return launchManager.getLaunchConfigurationType( LAUNCH_CONFIG_TYPE );
  }

  public static void setDefaults( final ILaunchConfigurationWorkingCopy config ) {
    config.setAttribute( USE_WEB_XML, DEFAULT_USE_WEB_XML );
    config.setAttribute( WEB_XML_LOCATION, DEFAULT_WEB_XML_LOCATION );
    config.setAttribute( SERVLET_PATH, DEFAULT_SERVLET_PATH );
    config.setAttribute( ENTRY_POINT, DEFAULT_ENTRY_POINT );
    config.setAttribute( PROJECT_NAME, DEFAULT_PROJECT_NAME );
    config.setAttribute( VM_ARGUMENTS, DEFAULT_VM_ARGUMENTS );
    config.setAttribute( WORKING_DIRECTORY, DEFAULT_WORKING_DIRECTORY );
    config.setAttribute( USE_MANUAL_PORT, DEFAULT_USE_MANUAL_PORT );
    config.setAttribute( PORT, DEFAULT_PORT );
    config.setAttribute( OPEN_BROWSER, DEFAULT_OPEN_BROWSER );
    config.setAttribute( BROWSER_MODE, DEFAULT_BROWSER_MODE );
  }

  private final ILaunchConfiguration config;
  private final ILaunchConfigurationWorkingCopy workingCopy;

  public RWTLaunchConfig( final ILaunchConfiguration config ) {
    checkNotNull( config, "config" ); //$NON-NLS-1$
    this.config = config;
    if( config instanceof ILaunchConfigurationWorkingCopy ) {
      this.workingCopy = ( ILaunchConfigurationWorkingCopy )config;
    } else {
      this.workingCopy = null;
    }
  }

  public String getName() {
    return config.getName();
  }
  
  public ILaunchConfiguration getUnderlyingLaunchConfig() {
    return config;
  }

  //////////////////////////////////////////////////////////
  // Accessor and mutator methods for wrapped launch config

  public String getProjectName() {
    return getAttribute( PROJECT_NAME, DEFAULT_PROJECT_NAME );
  }
  
  public void setProjectName( String projectName ) {
    checkNotNull( projectName, "projectName" ); //$NON-NLS-1$
    checkWorkingCopy();
    workingCopy.setAttribute( PROJECT_NAME, projectName );
  }

  public String getVMArguments() {
    return getAttribute( VM_ARGUMENTS, DEFAULT_VM_ARGUMENTS );
  }

  public void setVMArguments( String vmArguments ) {
    checkNotNull( vmArguments, "vmArguments" ); //$NON-NLS-1$
    checkWorkingCopy();
    workingCopy.setAttribute( VM_ARGUMENTS, vmArguments );
  }
  
  public boolean getUseWebXml() {
    return getAttribute( USE_WEB_XML, DEFAULT_USE_WEB_XML );
  }
  
  public void setUseWebXml( boolean useWebXml ) {
    checkWorkingCopy();
    workingCopy.setAttribute( USE_WEB_XML, useWebXml );
  }
  
  public String getWebXmlLocation() {
    return getAttribute( WEB_XML_LOCATION, DEFAULT_WEB_XML_LOCATION );
  }
  
  public void setWebXmlLocation( String webXmlLocation ) {
    checkNotNull( webXmlLocation, "webXmlLocation" ); //$NON-NLS-1$
    checkWorkingCopy();
    workingCopy.setAttribute( WEB_XML_LOCATION, webXmlLocation );
  }

  public String getServletPath() {
    return getAttribute( SERVLET_PATH, DEFAULT_SERVLET_PATH );
  }

  public void setServletPath( String servletPath ) {
    checkNotNull( servletPath, "servletPath" ); //$NON-NLS-1$
    checkWorkingCopy();
    workingCopy.setAttribute( SERVLET_PATH, servletPath );
  }

  public String getEntryPoint() {
    return getAttribute( ENTRY_POINT, DEFAULT_ENTRY_POINT );
  }

  public void setEntryPoint( final String entryPoint ) {
    checkNotNull( entryPoint, "entryPoint" ); //$NON-NLS-1$
    checkWorkingCopy();
    workingCopy.setAttribute( ENTRY_POINT, entryPoint );
  }

  /////////////////
  // Helping method

  public boolean getUseManualPort() {
    return getAttribute( USE_MANUAL_PORT, DEFAULT_USE_MANUAL_PORT );
  }

  public void setUseManualPort( boolean useManualPort ) {
    checkWorkingCopy();
    workingCopy.setAttribute( USE_MANUAL_PORT, useManualPort );
  }

  public int getPort() {
    return getAttribute( PORT, DEFAULT_PORT );
  }

  public void setPort( int port ) {
    checkWorkingCopy();
    workingCopy.setAttribute( PORT, port );
  }

  public boolean getOpenBrowser() {
    return getAttribute( OPEN_BROWSER, DEFAULT_OPEN_BROWSER );
  }

  public void setOpenBrowser( boolean openBrowser ) {
    checkWorkingCopy();
    workingCopy.setAttribute( OPEN_BROWSER, openBrowser );
  }

  public BrowserMode getBrowserMode() {
    String browserMode = getAttribute( BROWSER_MODE, DEFAULT_BROWSER_MODE );
    return BrowserMode.parse( browserMode );
  }

  public void setBrowserMode( BrowserMode browserMode ) {
    checkNotNull( browserMode, "browserMode" ); //$NON-NLS-1$
    checkWorkingCopy();
    workingCopy.setAttribute( BROWSER_MODE, browserMode.toString() );
  }

  private static void checkNotNull( Object argument, String argumentName ) {
    if( argument == null ) {
      throw new NullPointerException( argumentName );
    }
  }

  private void checkWorkingCopy() {
    if( workingCopy == null ) {
      String msg
        = "Launch configuration cannot be modified, no working copy available"; //$NON-NLS-1$
      throw new IllegalStateException( msg );
    }
  }

  private String getAttribute( String name, String defaultValue ) {
    String result = null;
    try {
      result = config.getAttribute( name, defaultValue );
    } catch( CoreException e ) {
      handleException( name, e );
    }
    return result;
  }

  private boolean getAttribute( String name, boolean defaultValue ) {
    boolean result = false;
    try {
      result = config.getAttribute( name, defaultValue );
    } catch( CoreException e ) {
      handleException( name, e );
    }
    return result;
  }

  private int getAttribute( String name, int defaultValue ) {
    int result = 0;
    try {
      result = config.getAttribute( name, defaultValue );
    } catch( CoreException e ) {
      handleException( name, e );
    }
    return result;
  }
  
  private void handleException( String name, CoreException e ) {
    String msg = "Failed to read launch configuration attribute: " + name; //$NON-NLS-1$
    throw new RuntimeException( msg, e );
  }
}
