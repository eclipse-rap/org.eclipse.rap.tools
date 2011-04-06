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
import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.rap.ui.internal.launch.rwt.RWTLaunchActivator;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.util.FileUtil;


class RWTLaunch {

  private static final String PREFIX = RWTLaunch.class.getName();
  private static final String PORT = PREFIX + "#port"; //$NON-NLS-1$
  
  private final ILaunch launch;
  private final RWTLaunchConfig config;
  
  RWTLaunch( ILaunch launch ) {
    this.launch = launch;
    this.config = new RWTLaunchConfig( launch.getLaunchConfiguration() );
  }
  
  RWTLaunchConfig getLaunchConfig() {
    return config;
  }

  void setPort( int port ) {
    launch.setAttribute( PORT, String.valueOf( port ) );
  }

  int getPort() {
    return Integer.valueOf( launch.getAttribute( PORT ) ).intValue();
  }
  
  String computeBrowserUrl() {
    String servletName;
    String port = String.valueOf( getPort() );
    if( config.getUseWebXml() ) {
      servletName = config.getServletPath();
    } else {
      servletName = "rap";
    }
    return MessageFormat.format( "http://127.0.0.1:{0}/{1}", new Object[] { port, servletName } );
  }
  
  void cleanUp() {
    FileUtil.delete( getBasePath() );
  }

  File getJettyHomePath() {
    return getPath( "jetty-home" ); //$NON-NLS-1$
  }

  File getWebAppPath() {
    return getPath( "web-app" ); //$NON-NLS-1$
  }

  File getWebXmlPath() {
    return getPath( "web-app/WEB-INF/web.xml" ); //$NON-NLS-1$
  }
  
  private File getPath( String suffix ) {
    File basePath = getBasePath();
    return new File( basePath, suffix );
  }

  private File getBasePath() {
    IPath stateLocation = RWTLaunchActivator.getDefault().getStateLocation();
    IPath basePath = stateLocation.append( config.getName() );
    return basePath.toFile();
  }
}
