/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.launch.internal.util;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig;
import org.eclipse.rap.ui.internal.launch.URLBuilder;
import org.eclipse.rap.ui.tests.Fixture;


public class URLBuilder_Test extends TestCase {
  
  public void testFromLaunchConfig() throws Exception {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    // servlet name starting with a slash, default entry point
    rapConfig.setServletName( "/startsWithSlash" );
    URL url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/startsWithSlash", url.toString() );
    // servlet name starting without a slash, default entry point
    rapConfig.setServletName( "startsWithoutSlash" );
    url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/startsWithoutSlash", url.toString() );
    // servlet name starting without a slash, distinct entry point
    rapConfig.setServletName( "servletName" );
    rapConfig.setEntryPoint( "entryPoint" );
    url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/servletName?startup=entryPoint", 
                  url.toString() );
  }
  
  public void testValidContextPath() throws CoreException, MalformedURLException {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    rapConfig.setUseManualContextPath( true );
    rapConfig.setContextPath( "/contextPath" );
    URL url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/contextPath/rap", url.toString() );
  }
  
  public void testContextPathWithMissingStartSlash() throws CoreException, MalformedURLException {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    rapConfig.setUseManualContextPath( true );
    rapConfig.setContextPath( "contextPath" );
    URL url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/contextPath/rap", url.toString() );
  }
  
  public void testContextPathWithTrailingSlash() throws CoreException, MalformedURLException {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    rapConfig.setUseManualContextPath( true );
    rapConfig.setContextPath( "/contextPath/" );
    URL url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/contextPath/rap", url.toString() );
  }
  
}
