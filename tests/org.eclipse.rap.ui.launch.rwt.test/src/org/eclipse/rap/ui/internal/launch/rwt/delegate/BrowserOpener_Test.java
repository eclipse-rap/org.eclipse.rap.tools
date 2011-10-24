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

import java.net.ServerSocket;

import junit.framework.TestCase;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.rap.ui.internal.launch.rwt.config.BrowserMode;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestLaunch;
import org.eclipse.ui.browser.IWebBrowser;


public class BrowserOpener_Test extends TestCase {

  private RWTLaunchConfig launchConfig;
  private ILaunch launch;
  
  public void testConstructorWithInvalidUrl() {
    try {
      new BrowserOpener( launch );
    } catch( RuntimeException expected ) {
    }
  }

  public void testComputeBrowserUrl() {
    launchConfig.setServletPath( "servletpath" );
    setLaunchPort( 1234 );
    BrowserOpener browserOpener = new BrowserOpener( launch );

    String url = browserOpener.computeBrowserUrl();

    assertEquals( "http://127.0.0.1:1234/servletpath", url );
  }
  
  public void testGetBrowserAssignedDifferentIdsForInternalAndExternal() throws Exception {
    launchConfig.setBrowserMode( BrowserMode.EXTERNAL );
    setLaunchPort( 1234 );
    BrowserOpener externalBrowserOpener = new BrowserOpener( launch );
    IWebBrowser externalBrowser = externalBrowserOpener.getBrowser();
    
    launchConfig.setBrowserMode( BrowserMode.INTERNAL );
    BrowserOpener internalBrowserOpener = new BrowserOpener( launch );
    IWebBrowser internalBrowser = internalBrowserOpener.getBrowser();
    
    assertFalse( internalBrowser.getId().equals( externalBrowser.getId() ) );
  }
  
  public void testCanConnectToUrlWhenAvailable() throws Exception {
    ServerSocket socket = new ServerSocket( 0 );
    setLaunchPort( socket.getLocalPort() );
    BrowserOpener browserOpener = new BrowserOpener( launch );

    boolean canConnectToUrl = browserOpener.canConnectToUrl();
    socket.close();

    assertTrue( canConnectToUrl );
  }

  public void testCanConnectToUrlWhenUnvailable() throws Exception {
    ServerSocket socket = new ServerSocket( 0 );
    setLaunchPort( socket.getLocalPort() );
    socket.close();
    BrowserOpener browserOpener = new BrowserOpener( launch );
    
    boolean canConnectToUrl = browserOpener.canConnectToUrl();
    
    assertFalse( canConnectToUrl );
  }

  protected void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    launch = new TestLaunch( launchConfig.getUnderlyingLaunchConfig() );
  }
  
  protected void tearDown() throws Exception {
    Fixture.deleteAllRWTLaunchConfigs();
  }

  private void setLaunchPort( int port ) {
    new RWTLaunch( launch ).setPort( port );
  }
}
