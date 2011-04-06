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

import java.text.MessageFormat;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.util.BundleFileLocator;
import org.eclipse.rap.ui.internal.launch.rwt.util.StringArrays;


public class RWTLaunchDelegate extends JavaLaunchDelegate {
  
  private RWTLaunch launch;

  public void launch( ILaunchConfiguration configuration,
                      String mode,
                      ILaunch launch,
                      IProgressMonitor mon )
    throws CoreException
  {
    IProgressMonitor monitor = mon == null ? new NullProgressMonitor() : mon;
    // Initialize launch field
    initializeLaunch( launch );
    // Register launch listener to delete temp files when launch terminates
    LaunchCleanup.register( launch );
    // If requested, schedule opening of browser. Must occur before actual launch
    RWTLaunchConfig launchConfig = this.launch.getLaunchConfig();
    if( launchConfig.getOpenBrowser() ) {
      BrowserOpener browserOpener = new BrowserOpener( launch, this.launch.computeBrowserUrl() );
      browserOpener.scheduleOpen();
    }
    // Terminate previous
    LaunchTerminator.terminatePrevious( launch, monitor );
    // Provision web.xml
    new WebXmlProvider( this.launch ).provide( monitor );
    // Run actual launch with JDT launcher
    if( !monitor.isCanceled() ) {
      super.launch( configuration, mode, launch, mon );
    }
  }

  public String getMainTypeName( ILaunchConfiguration configuration ) {
    return "org.mortbay.jetty.Main";
  }
  
  public String[] getClasspath( ILaunchConfiguration configuration ) throws CoreException {
    String[] result = super.getClasspath( configuration );
    result = StringArrays.append( result, BundleFileLocator.locate( "org.mortbay.jetty.server" ) ); //$NON-NLS-1$
    result = StringArrays.append( result, BundleFileLocator.locate( "org.mortbay.jetty.util" ) ); //$NON-NLS-1$
    result = StringArrays.append( result, BundleFileLocator.locate( "javax.servlet" ) ); //$NON-NLS-1$
    return result;
  }
  
  public String getProgramArguments( ILaunchConfiguration configuration ) {
    // don't call super, program arguments are not configurable via the UI
    String port = String.valueOf( launch.getPort() );
    String webAppDirectory = launch.getWebAppPath().getAbsolutePath();
    return MessageFormat.format( "{0} -webapp \"{1}\"", new Object[] { port, webAppDirectory } ); //$NON-NLS-1$
  }
  
  public String getVMArguments( ILaunchConfiguration configuration ) throws CoreException {
    String result = super.getVMArguments( configuration );
    result += " -Djetty.home="; //$NON-NLS-1$
    result += "\""; //$NON-NLS-1$
    result += launch.getJettyHomePath();
    result += "\""; //$NON-NLS-1$
    return result;
  }
  
  void initializeLaunch( ILaunch genericLaunch ) {
    launch = new RWTLaunch( genericLaunch );
    launch.setPort( determinePort() );
  }

  int determinePort() {
    RWTLaunchConfig launchConfig = launch.getLaunchConfig();
    int result;
    if( launchConfig.getUseManualPort() ) {
      result = launchConfig.getPort();
    } else {
      result = SocketUtil.findFreePort();
    }
    return result;
  }
}
