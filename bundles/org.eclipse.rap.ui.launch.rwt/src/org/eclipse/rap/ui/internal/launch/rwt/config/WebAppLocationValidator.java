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

import java.text.MessageFormat;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig.LaunchTarget;


class WebAppLocationValidator extends Validator {

  static final int ERR_WEB_APP_LOCATION_EMPTY = 8040;
  static final int ERR_WEB_APP_LOCATION_NOT_FOUND = 8041;
  static final int ERR_WEB_APP_LOCATION_INVALID = 8042;

  private final IWorkspaceRoot workspaceRoot;
  
  WebAppLocationValidator( RWTLaunchConfig config, ValidationResult validationResult ) {
    super( config, validationResult );
    workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
  }

  void validate() {
    if( LaunchTarget.WEB_APP_FOLDER.equals( config.getLaunchTarget() ) ) {
      validateLocation();
      validateStructure();
    }
  }

  private void validateLocation() {
    String webAppLocation = config.getWebAppLocation();
    if( webAppLocation.length() == 0 ) { 
      addError( "The location for the web app folder is empty.", ERR_WEB_APP_LOCATION_EMPTY );
    } else if( !webAppLocationExists() ) {
      String msg = MessageFormat.format( "Folder {0} does not exist.", webAppLocation );
      addError( msg, ERR_WEB_APP_LOCATION_NOT_FOUND );
    }
  }
  
  private void validateStructure() {
    if( webAppLocationExists() && !webXmlFileExists() ) {
      String text = "Folder {0} is not a valid web application root (WEB-INF/web.xml is missing).";
      String msg = MessageFormat.format( text, config.getWebAppLocation() );
      addError( msg, ERR_WEB_APP_LOCATION_INVALID );
    }
  }

  private boolean webAppLocationExists() {
    IPath path = getWebAppPath();
    return !path.isEmpty() && workspaceRoot.exists( path );
  }

  private boolean webXmlFileExists() {
    return workspaceRoot.exists( getWebXmlFile() );
  }

  private IPath getWebAppPath() {
    return Path.fromPortableString( config.getWebAppLocation() );
  }

  private IPath getWebXmlFile() {
    IPath webAppLocation = getWebAppPath();
    IPath webInfFolder = webAppLocation.append( "WEB-INF" );
    return webInfFolder.append( "web.xml" );
  }
}
