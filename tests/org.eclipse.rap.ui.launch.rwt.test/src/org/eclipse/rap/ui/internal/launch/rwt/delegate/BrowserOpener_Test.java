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
import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.rap.ui.internal.launch.rwt.config.BrowserMode;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestLaunch;
import org.eclipse.ui.browser.IWebBrowser;


public class BrowserOpener_Test extends TestCase {

  private static final String VALID_URL = "http://host/path";
  
  private RWTLaunchConfig launchConfig;
  private ILaunch launch;
  
  public void testConstructorWithInvalidUrl() {
    try {
      new BrowserOpener( launch, "bad" );
    } catch( RuntimeException expected ) {
    }
  }

  public void testGetBrowserAssignedDifferentIdsForInternalAndExternal() throws Exception {
    launchConfig.setBrowserMode( BrowserMode.EXTERNAL );
    BrowserOpener externalBrowserOpener = new BrowserOpener( launch, VALID_URL );
    IWebBrowser externalBrowser = externalBrowserOpener.getBrowser();
    
    launchConfig.setBrowserMode( BrowserMode.INTERNAL );
    BrowserOpener internalBrowserOpener = new BrowserOpener( launch, VALID_URL );
    IWebBrowser internalBrowser = internalBrowserOpener.getBrowser();
    
    assertFalse( internalBrowser.getId().equals( externalBrowser.getId() ) );
  }
  
  public void testCanConnectToUrlWhenAvailable() throws Exception {
    ServerSocket socket = new ServerSocket( 0 );
    Object[] args = new Object[] { String.valueOf( socket.getLocalPort() ) };
    String url = MessageFormat.format( "http://127.0.0.1:{0}/path", args );
    BrowserOpener browserOpener = new BrowserOpener( launch, url );

    boolean canConnectToUrl = browserOpener.canConnectToUrl();
    socket.close();

    assertTrue( canConnectToUrl );
  }

  public void testCanConnectToUrlWhenUnvailable() throws Exception {
    ServerSocket socket = new ServerSocket( 0 );
    Object[] args = new Object[] { String.valueOf( socket.getLocalPort() ) };
    String url = MessageFormat.format( "http://127.0.0.1:{0}/path", args );
    socket.close();
    BrowserOpener browserOpener = new BrowserOpener( launch, url );
    
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
}
