/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.launch.internal.util;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.tools.internal.tests.Fixture;
import org.eclipse.rap.tools.launch.internal.RAPLaunchConfig;
import org.eclipse.rap.tools.launch.internal.URLBuilder;
import org.junit.Before;
import org.junit.Test;


public class URLBuilder_Test {

  private RAPLaunchConfig rapConfig;

  @Before
  public void setUp() throws CoreException {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    rapConfig = new RAPLaunchConfig( config );
  }

  @Test
  public void testFromLaunchConfig() throws Exception {
    rapConfig.setServletPath( "/foo" );

    String url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );

    assertEquals( "http://127.0.0.1:80/foo", url );
  }

  @Test
  public void testFromLaunchConfig_servletPathWithoutTrailingSlash() throws Exception {
    rapConfig.setServletPath( "foo" );

    String url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );

    assertEquals( "http://127.0.0.1:80/foo", url );
  }

  @Test
  public void testFromLaunchConfig_servletPathWithQueryString() throws Exception {
    rapConfig.setServletPath( "foo?id=23&path=/path" );

    String url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );

    assertEquals( "http://127.0.0.1:80/foo?id=23&path=/path", url );
  }

  @Test
  public void testFromLaunchConfig_withContextPath() throws CoreException {
    rapConfig.setUseManualContextPath( true );
    rapConfig.setContextPath( "/contextPath" );

    String url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );

    assertEquals( "http://127.0.0.1:80/contextPath/rap", url );
  }

  @Test
  public void testFromLaunchConfig_withContextPathWithoutLeadingSlash() throws CoreException {
    rapConfig.setUseManualContextPath( true );
    rapConfig.setContextPath( "contextPath" );

    String url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );

    assertEquals( "http://127.0.0.1:80/contextPath/rap", url );
  }

  @Test
  public void testFromLaunchConfig_withContextPathWithTrailingSlash() throws CoreException {
    rapConfig.setUseManualContextPath( true );
    rapConfig.setContextPath( "/contextPath/" );

    String url = URLBuilder.fromLaunchConfig( rapConfig, 80, false );

    assertEquals( "http://127.0.0.1:80/contextPath/rap", url );
  }

}
