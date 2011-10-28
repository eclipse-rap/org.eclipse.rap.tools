/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.config;

import junit.framework.TestCase;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;


public class RWTLaunchConfig_Test extends TestCase {
  private ILaunchConfigurationWorkingCopy config;
  private RWTLaunchConfig rwtConfig;

  public void testGetType() {
    ILaunchConfigurationType launchConfigurationType = RWTLaunchConfig.getType();
    assertEquals( RWTLaunchConfig.LAUNCH_CONFIG_TYPE, launchConfigurationType.getIdentifier() );
  }
  
  public void testSetDefaults() {
    RWTLaunchConfig.setDefaults( config );
    assertEquals( "", rwtConfig.getProjectName() );
    assertEquals( LaunchTarget.ENTRY_POINT, rwtConfig.getLaunchTarget() );
    assertEquals( "rap", rwtConfig.getServletPath() );
    assertEquals( "", rwtConfig.getEntryPoint() );
    assertEquals( "", rwtConfig.getWebXmlLocation() );
    assertEquals( "", rwtConfig.getWebAppLocation() );
    assertEquals( "", rwtConfig.getVMArguments() );
    assertFalse( rwtConfig.getUseManualPort() );
    assertEquals( 8080, rwtConfig.getPort() );
    assertTrue( rwtConfig.getOpenBrowser() );
    assertSame( BrowserMode.INTERNAL, rwtConfig.getBrowserMode() );
  }
  
  public void testConstructorWithNullArgument() {
    try {
      new RWTLaunchConfig( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetUnderlyingLaunchConfig() {
    assertSame( config, rwtConfig.getUnderlyingLaunchConfig() );
  }
  
  public void testGetName() {
    assertEquals( config.getName(), rwtConfig.getName() );
  }
  
  public void testProjectName() {
    String projectName = "projectName";
    rwtConfig.setProjectName( projectName );
    assertEquals( projectName, rwtConfig.getProjectName() );
  }
  
  public void testSetProjectNameWithNullArgument() {
    try {
      rwtConfig.setProjectName( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testLaunchTarget() {
    rwtConfig.setLaunchTarget( LaunchTarget.WEB_APP_FOLDER );
    assertEquals( LaunchTarget.WEB_APP_FOLDER, rwtConfig.getLaunchTarget() );
  }
  
  public void testLaunchTargetWithNullArgument() {
    try {
      rwtConfig.setLaunchTarget( null );
    } catch( NullPointerException expected ) {
      
    }
  }
  
  public void testWebXmlLocation() {
    String webXmlLocation = "/location/of/web.xml";
    rwtConfig.setWebXmlLocation( webXmlLocation );
    assertEquals( webXmlLocation, rwtConfig.getWebXmlLocation() );
  }
  
  public void testWebXmlLocationWithNullArgument() {
    try {
      rwtConfig.setWebXmlLocation( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testWebAppFolderLocation() {
    String webAppLocation = "foo";
    rwtConfig.setWebAppLocation( webAppLocation );
    assertEquals( webAppLocation, rwtConfig.getWebAppLocation() );
  }
  
  public void testWebAppLocationWithNullArgument() {
    try {
      rwtConfig.setWebAppLocation( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testServletPath() {
    String servletName = "servletPath";
    rwtConfig.setServletPath( servletName );
    assertEquals( servletName, rwtConfig.getServletPath() );
  }
  
  public void testServletPathWithNullArgument() {
    try {
      rwtConfig.setServletPath( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testEntryPoint() {
    String entryPoint = "ep";
    rwtConfig.setEntryPoint( entryPoint );
    assertEquals( entryPoint, rwtConfig.getEntryPoint() );
  }
  
  public void testSetEntryPointWithNullArgument() {
    try {
      rwtConfig.setEntryPoint( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testSetVMArgumentsWithNullArgument() {
    try {
      rwtConfig.setVMArguments( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testVMArguments() {
    String vmArguments = "vmArguments";
    rwtConfig.setVMArguments( vmArguments );
    assertEquals( vmArguments, rwtConfig.getVMArguments() );
  }
  
  public void testUseManualPort() {
    rwtConfig.setUseManualPort( true );
    assertTrue( rwtConfig.getUseManualPort() );
  }
  
  public void testPort() {
    rwtConfig.setPort( 1234 );
    assertEquals( 1234, rwtConfig.getPort() );
  }
  
  public void testOpenBrowser() {
    rwtConfig.setOpenBrowser( false );
    assertFalse( rwtConfig.getOpenBrowser() );
  }
  
  public void testBrowserMode() {
    rwtConfig.setBrowserMode( BrowserMode.EXTERNAL );
    assertSame( BrowserMode.EXTERNAL, rwtConfig.getBrowserMode() );
  }
  
  public void testSetBrowserModeWithNullArgument() {
    try {
      rwtConfig.setBrowserMode( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    config = Fixture.createRWTLaunchConfig();
    rwtConfig = new RWTLaunchConfig( config );
  }
  
  protected void tearDown() throws Exception {
    config.delete();
  }
}
