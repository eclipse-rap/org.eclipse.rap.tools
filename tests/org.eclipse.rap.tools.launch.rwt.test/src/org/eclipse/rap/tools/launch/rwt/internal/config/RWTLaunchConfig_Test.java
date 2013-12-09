/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.tools.launch.rwt.internal.config.BrowserMode;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RWTLaunchConfig_Test {

  private ILaunchConfigurationWorkingCopy config;
  private RWTLaunchConfig rwtConfig;

  @Before
  public void setUp() throws Exception {
    config = Fixture.createRWTLaunchConfig();
    rwtConfig = new RWTLaunchConfig( config );
  }

  @After
  public void tearDown() throws Exception {
    config.delete();
  }

  @Test
  public void testGetType() {
    ILaunchConfigurationType type = RWTLaunchConfig.getType();

    assertEquals( RWTLaunchConfig.LAUNCH_CONFIG_TYPE, type.getIdentifier() );
  }

  @Test
  public void testSetDefaults() {
    RWTLaunchConfig.setDefaults( config );

    assertEquals( "", rwtConfig.getProjectName() );
    assertEquals( LaunchTarget.ENTRY_POINT, rwtConfig.getLaunchTarget() );
    assertEquals( "/rap", rwtConfig.getServletPath() );
    assertEquals( "", rwtConfig.getEntryPoint() );
    assertEquals( "", rwtConfig.getWebXmlLocation() );
    assertEquals( "", rwtConfig.getWebAppLocation() );
    assertEquals( "", rwtConfig.getVMArguments() );
    assertFalse( rwtConfig.getUseManualPort() );
    assertEquals( 8080, rwtConfig.getPort() );
    assertFalse( rwtConfig.getUseManualContextPath() );
    assertEquals( "/", rwtConfig.getContextPath() );
    assertFalse( rwtConfig.getUseSessionTimeout() );
    assertEquals( 0, rwtConfig.getSessionTimeout() );
    assertTrue( rwtConfig.getOpenBrowser() );
    assertTrue( rwtConfig.getDevelopmentMode() );
    assertSame( BrowserMode.INTERNAL, rwtConfig.getBrowserMode() );
  }

  @Test( expected = NullPointerException.class )
  public void testConstructor_failsWithNullArgument() {
    new RWTLaunchConfig( null );
  }

  @Test
  public void testGetUnderlyingLaunchConfig() {
    assertSame( config, rwtConfig.getUnderlyingLaunchConfig() );
  }

  @Test
  public void testGetName() {
    assertEquals( config.getName(), rwtConfig.getName() );
  }

  @Test
  public void testSetProjectName() {
    String projectName = "projectName";

    rwtConfig.setProjectName( projectName );

    assertEquals( projectName, rwtConfig.getProjectName() );
  }

  @Test( expected = NullPointerException.class )
  public void testSetProjectName_failsWithNullArgument() {
    rwtConfig.setProjectName( null );
  }

  @Test( expected = NullPointerException.class )
  public void testSetLaunchTarget_failsWithNullArgument() {
    rwtConfig.setLaunchTarget( null );
  }

  @Test
  public void testSetWebXmlLocation() {
    String location = "/location/of/web.xml";

    rwtConfig.setWebXmlLocation( location );

    assertEquals( location, rwtConfig.getWebXmlLocation() );
  }

  @Test( expected = NullPointerException.class )
  public void testSetWebXmlLocation_failsWithNullArgument() {
    rwtConfig.setWebXmlLocation( null );
  }

  @Test
  public void testSetWebAppLocation() {
    String location = "foo";

    rwtConfig.setWebAppLocation( location );

    assertEquals( location, rwtConfig.getWebAppLocation() );
  }

  @Test( expected = NullPointerException.class )
  public void testWebAppLocation_failsWithNullArgument() {
    rwtConfig.setWebAppLocation( null );
  }

  @Test
  public void testSetServletPath() {
    String servletPath = "/servletPath";

    rwtConfig.setServletPath( servletPath );

    assertEquals( servletPath, rwtConfig.getServletPath() );
  }

  @Test( expected = NullPointerException.class )
  public void testSetServletPath_failsWithNullArgument() {
    rwtConfig.setServletPath( null );
  }

  @Test
  public void testSetContextPath() {
    String contextPath = "/contextPath";

    rwtConfig.setContextPath( contextPath );

    assertEquals( contextPath, rwtConfig.getContextPath() );
  }

  @Test( expected = NullPointerException.class )
  public void testSetContextPath_failsWithNullArgument() {
    rwtConfig.setContextPath( null );
  }

  @Test
  public void testSetEntryPoint() {
    String entryPoint = "ep";

    rwtConfig.setEntryPoint( entryPoint );

    assertEquals( entryPoint, rwtConfig.getEntryPoint() );
  }

  @Test( expected = NullPointerException.class )
  public void testSetEntryPointWithNullArgument() {
    rwtConfig.setEntryPoint( null );
  }

  @Test( expected = NullPointerException.class )
  public void testSetVMArguments_failsWithNullArgument() {
    rwtConfig.setVMArguments( null );
  }

  @Test
  public void testSetVMArguments() {
    String vmArguments = "vmArguments";

    rwtConfig.setVMArguments( vmArguments );

    assertEquals( vmArguments, rwtConfig.getVMArguments() );
  }

  @Test
  public void testSetUseManualPort() {
    rwtConfig.setUseManualPort( true );

    assertTrue( rwtConfig.getUseManualPort() );
  }

  @Test
  public void testSetPort() {
    rwtConfig.setPort( 1234 );

    assertEquals( 1234, rwtConfig.getPort() );
  }

  @Test
  public void testSetSessionTimeout() {
    rwtConfig.setSessionTimeout( 1234 );

    assertEquals( 1234, rwtConfig.getSessionTimeout() );
  }

  @Test
  public void testSetOpenBrowser() {
    rwtConfig.setOpenBrowser( false );

    assertFalse( rwtConfig.getOpenBrowser() );
  }

  @Test
  public void testSetDevelopmentMode() {
    rwtConfig.setDevelopmentMode( false );

    assertFalse( rwtConfig.getDevelopmentMode() );
  }

  @Test
  public void testSetBrowserMode() {
    rwtConfig.setBrowserMode( BrowserMode.EXTERNAL );

    assertSame( BrowserMode.EXTERNAL, rwtConfig.getBrowserMode() );
  }

  @Test( expected = NullPointerException.class )
  public void testSetBrowserMode_failsWithNullArgument() {
    rwtConfig.setBrowserMode( null );
  }

}
