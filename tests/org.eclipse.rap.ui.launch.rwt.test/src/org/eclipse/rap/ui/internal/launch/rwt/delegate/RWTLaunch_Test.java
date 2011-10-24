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
package org.eclipse.rap.ui.internal.launch.rwt.delegate;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestLaunch;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class RWTLaunch_Test extends TestCase {
  
  private RWTLaunch rwtLaunch;
  private RWTLaunchConfig launchConfig;

  public void testPort() {
    int port = 1234;
    rwtLaunch.setPort( port );
    assertEquals( port, rwtLaunch.getPort() );
  }
  
  public void testGetPortWhenUndefined() {
    assertEquals( -1, rwtLaunch.getPort() );
  }
  
  public void testGetLaunchConfig() {
    RWTLaunchConfig launchConfig = rwtLaunch.getLaunchConfig();
    assertNotNull( launchConfig );
  }
  
  public void testGetLaunchConfigReturnsSameInstance() {
    RWTLaunchConfig launchConfig1 = rwtLaunch.getLaunchConfig();
    RWTLaunchConfig launchConfig2 = rwtLaunch.getLaunchConfig();
    assertSame( launchConfig1, launchConfig2 );
  }
  
  public void testWebAppDiffersFromJettyHome() throws IOException {
    File webApp = rwtLaunch.getWebAppPath();
    File jettyHome = rwtLaunch.getJettyHomePath();

    String webAppPath = webApp.getCanonicalPath();
    String jettyHomePath = jettyHome.getCanonicalPath();
    assertFalse( webAppPath.equals( jettyHomePath ) );
  }
  
  public void testGetWebXmlPathIsBelowWebAppPath() {
    String webApp = rwtLaunch.getWebAppPath().getAbsolutePath();
    String webXml = rwtLaunch.getWebXmlPath().getAbsolutePath();
    
    assertEquals( webApp, webXml.substring( 0, webApp.length() ) );
  }
  
  public void testGetWebAppPathForWebAppLaunchTarget() throws Exception {
    TestProject project = new TestProject();
    launchConfig.setLaunchTarget( LaunchTarget.WEB_APP_FOLDER );
    launchConfig.setWebAppLocation( project.getProject().getFullPath().toPortableString() );
    
    File webAppPath = rwtLaunch.getWebAppPath();
    
    assertEquals( webAppPath, project.getProject().getLocation().toFile().getCanonicalFile() );
  }

  protected void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    ILaunch launch = new TestLaunch( launchConfig.getUnderlyingLaunchConfig() );
    rwtLaunch = new RWTLaunch( launch );
  }
  
  protected void tearDown() throws Exception {
    Fixture.deleteAllRWTLaunchConfigs();
    TestProject.deleteAll();
  }
}
