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
import static org.junit.Assert.assertTrue;

import java.net.ServerSocket;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.rap.ui.internal.launch.rwt.config.BrowserMode;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestLaunch;
import org.eclipse.ui.browser.IWebBrowser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BrowserOpener_Test {

  private RWTLaunchConfig launchConfig;
  private ILaunch launch;

  @Before
  public void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    launch = new TestLaunch( launchConfig.getUnderlyingLaunchConfig() );
  }

  @After
  public void tearDown() throws Exception {
    Fixture.deleteAllRWTLaunchConfigs();
  }

  @Test
  public void testConstructorWithInvalidUrl() {
    try {
      new BrowserOpener( launch );
    } catch( RuntimeException expected ) {
    }
  }

  @Test
  public void testComputeBrowserUrl() {
    launchConfig.setServletPath( "/servletpath" );
    setLaunchPort( 1234 );
    BrowserOpener browserOpener = new BrowserOpener( launch );

    String url = browserOpener.computeBrowserUrl();

    assertEquals( "http://127.0.0.1:1234/servletpath", url );
  }

  @Test
  public void testComputeBrowserUrl_withContextPath() {
    launchConfig.setServletPath( "/servletpath" );
    launchConfig.setUseManualContextPath( true );
    launchConfig.setContextPath( "/contextpath" );
    setLaunchPort( 1234 );
    BrowserOpener browserOpener = new BrowserOpener( launch );

    String url = browserOpener.computeBrowserUrl();

    assertEquals( "http://127.0.0.1:1234/contextpath/servletpath", url );
  }


  @Test
  public void testComputeBrowserUrl_withContextPathAndTrailingSlash() {
    launchConfig.setServletPath( "/servletpath" );
    launchConfig.setUseManualContextPath( true );
    launchConfig.setContextPath( "/contextpath/" );
    setLaunchPort( 1234 );
    BrowserOpener browserOpener = new BrowserOpener( launch );

    String url = browserOpener.computeBrowserUrl();

    assertEquals( "http://127.0.0.1:1234/contextpath/servletpath", url );
  }

  @Test
  public void testGetBrowser_assignesDifferentIdsForInternalAndExternal() throws Exception {
    launchConfig.setBrowserMode( BrowserMode.EXTERNAL );
    setLaunchPort( 1234 );
    BrowserOpener externalBrowserOpener = new BrowserOpener( launch );
    IWebBrowser externalBrowser = externalBrowserOpener.getBrowser();

    launchConfig.setBrowserMode( BrowserMode.INTERNAL );
    BrowserOpener internalBrowserOpener = new BrowserOpener( launch );
    IWebBrowser internalBrowser = internalBrowserOpener.getBrowser();

    assertFalse( internalBrowser.getId().equals( externalBrowser.getId() ) );
  }

  @Test
  public void testCanConnectToUrl_whenAvailable() throws Exception {
    ServerSocket socket = new ServerSocket( 0 );
    setLaunchPort( socket.getLocalPort() );
    BrowserOpener browserOpener = new BrowserOpener( launch );

    boolean canConnectToUrl = browserOpener.canConnectToUrl();
    socket.close();

    assertTrue( canConnectToUrl );
  }

  @Test
  public void testCanConnectToUrl_whenUnvailable() throws Exception {
    ServerSocket socket = new ServerSocket( 0 );
    setLaunchPort( socket.getLocalPort() );
    socket.close();
    BrowserOpener browserOpener = new BrowserOpener( launch );

    boolean canConnectToUrl = browserOpener.canConnectToUrl();

    assertFalse( canConnectToUrl );
  }

  private void setLaunchPort( int port ) {
    new RWTLaunch( launch ).setPort( port );
  }

}
