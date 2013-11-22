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
package org.eclipse.rap.ui.internal.launch.rwt.delegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestLaunch;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RWTLaunch_Test {

  private RWTLaunch rwtLaunch;
  private RWTLaunchConfig launchConfig;

  @Before
  public void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    ILaunch launch = new TestLaunch( launchConfig.getUnderlyingLaunchConfig() );
    rwtLaunch = new RWTLaunch( launch );
  }

  @After
  public void tearDown() throws Exception {
    Fixture.deleteAllRWTLaunchConfigs();
    TestProject.deleteAll();
  }

  @Test
  public void testSetPort() {
    rwtLaunch.setPort( 1234 );

    assertEquals( 1234, rwtLaunch.getPort() );
  }

  @Test
  public void testGetPort_minusOneWhenUndefined() {
    assertEquals( -1, rwtLaunch.getPort() );
  }

  @Test
  public void testGetLaunchConfig() {
    RWTLaunchConfig launchConfig = rwtLaunch.getLaunchConfig();

    assertNotNull( launchConfig );
  }

  @Test
  public void testGetLaunchConfig_returnsSameInstance() {
    RWTLaunchConfig launchConfig1 = rwtLaunch.getLaunchConfig();
    RWTLaunchConfig launchConfig2 = rwtLaunch.getLaunchConfig();

    assertSame( launchConfig1, launchConfig2 );
  }

  @Test
  public void testGetWebAppPath_differsFromJettyHome() throws IOException {
    File webApp = rwtLaunch.getWebAppPath();
    File jettyHome = rwtLaunch.getJettyHomePath();

    String webAppPath = webApp.getCanonicalPath();
    String jettyHomePath = jettyHome.getCanonicalPath();
    assertFalse( webAppPath.equals( jettyHomePath ) );
  }

  @Test
  public void testGetWebXmlPath_isBelowWebAppPath() {
    String webApp = rwtLaunch.getWebAppPath().getAbsolutePath();
    String webXml = rwtLaunch.getWebXmlPath().getAbsolutePath();

    assertTrue( webXml.startsWith( webApp ) );
  }

}
