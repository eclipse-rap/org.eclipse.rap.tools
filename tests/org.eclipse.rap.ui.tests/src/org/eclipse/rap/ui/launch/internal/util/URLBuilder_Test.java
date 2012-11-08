/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.launch.internal.util;

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
    rapConfig.setServletPath( "/startsWithSlash" );
    String url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/startsWithSlash", url );
    // servlet name starting without a slash, default entry point
    rapConfig.setServletPath( "startsWithoutSlash" );
    String url2 = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/startsWithoutSlash", url2 );
    // servlet name starting without a slash, query string
    rapConfig.setServletPath( "servletName?testentrypointpath=/testentry" );
    String url3 = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/servletName?testentrypointpath=/testentry", url3 );
  }

  public void testValidContextPath() throws CoreException {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    rapConfig.setUseManualContextPath( true );
    rapConfig.setContextPath( "/contextPath" );
    String url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/contextPath/rap", url );
  }

  public void testContextPathWithMissingStartSlash() throws CoreException {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    rapConfig.setUseManualContextPath( true );
    rapConfig.setContextPath( "contextPath" );
    String url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/contextPath/rap", url );
  }

  public void testContextPathWithTrailingSlash() throws CoreException {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    rapConfig.setUseManualContextPath( true );
    rapConfig.setContextPath( "/contextPath/" );
    String url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );
    assertEquals( "http://127.0.0.1:80/contextPath/rap", url );
  }

}
