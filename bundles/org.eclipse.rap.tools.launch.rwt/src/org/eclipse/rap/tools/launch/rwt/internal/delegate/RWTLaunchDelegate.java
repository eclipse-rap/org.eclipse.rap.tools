/*******************************************************************************
 * Copyright (c) 2011, 2024 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.delegate;

import java.text.MessageFormat;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.util.BundleFileLocator;
import org.eclipse.rap.tools.launch.rwt.internal.util.StringUtil;


public class RWTLaunchDelegate extends JavaLaunchDelegate {

  private static final String VMARG_JETTY_HOME = " -Djetty.home="; //$NON-NLS-1$
  private static final String VMARG_DEVELOPMENT_MODE
    = " -Dorg.eclipse.rap.rwt.developmentMode="; //$NON-NLS-1$

  private RWTLaunch launch;

  @Override
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
      BrowserOpener browserOpener = new BrowserOpener( launch );
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

  @Override
  public String getMainTypeName( ILaunchConfiguration configuration ) {
    return "org.eclipse.rap.tools.launch.rwt.internal.jetty.JettyLauncher"; //$NON-NLS-1$
  }

  @Override
  public String[] getClasspath( ILaunchConfiguration configuration ) throws CoreException {
    List<String> list = new ArrayList<String>( Arrays.asList( super.getClasspath( configuration ) ) );
    list.add( BundleFileLocator.locate( "org.eclipse.rap.tools.launch.rwt" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "jakarta.servlet-api" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.ee10.servlet" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.ee10.webapp" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.server" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.security" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.http" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.io" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.servlet-api" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.session" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.logging" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.util" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.util.ajax" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.xml" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "org.eclipse.jetty.ee" ) ); //$NON-NLS-1$
    list.add( BundleFileLocator.locate( "slf4j.api" ) ); //$NON-NLS-1$
    return list.toArray( new String[0] );
  }

  @Override
  public String getProgramArguments( ILaunchConfiguration configuration ) {
    // don't call super, program arguments are not configurable via the UI
    String port = String.valueOf( launch.getPort() );
    String contextPath = getContextPath();
    String webAppDirectory = launch.getWebAppPath().getAbsolutePath();
    Object[] arguments = new Object[] { port, contextPath, webAppDirectory };
    return MessageFormat.format( "{0} {1} \"{2}\"", arguments ); //$NON-NLS-1$
  }

  @Override
  public String getVMArguments( ILaunchConfiguration configuration ) throws CoreException {
    StringBuilder result = new StringBuilder();
    result.append( super.getVMArguments( configuration ) );
    result.append( VMARG_JETTY_HOME )
          .append( '"' )
          .append( launch.getJettyHomePath() )
          .append( '"' );
    result.append( VMARG_DEVELOPMENT_MODE )
          .append( launch.getLaunchConfig().getDevelopmentMode() );
    return result.toString();
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

  private String getContextPath() {
    RWTLaunchConfig config = launch.getLaunchConfig();
    String result = "/";
    if( config.getUseManualContextPath() && !config.getContextPath().equals( "/" ) ) {
      result = StringUtil.stripTrailingSlash( config.getContextPath() );
    }
    return result;
  }
}
