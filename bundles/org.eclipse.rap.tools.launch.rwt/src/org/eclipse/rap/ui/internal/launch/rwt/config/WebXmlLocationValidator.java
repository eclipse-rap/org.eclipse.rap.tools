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
import org.eclipse.core.runtime.Path;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig.LaunchTarget;


class WebXmlLocationValidator extends Validator {

  static final int ERR_WEB_XML_LOCATION_EMPTY = 8010;
  static final int ERR_WEB_XML_LOCATION_NOT_FOUND = 8011;
  
  WebXmlLocationValidator( RWTLaunchConfig config, ValidationResult validationResult ) {
    super( config, validationResult );
  }

  void validate() {
    if( LaunchTarget.WEB_XML.equals( config.getLaunchTarget() ) ) {
      String webXmlLocation = config.getWebXmlLocation();
      if( webXmlLocation.length() == 0 ) { 
        addError( "The location for the web.xml is empty.", ERR_WEB_XML_LOCATION_EMPTY );
      } else {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        if( !root.exists( Path.fromPortableString( webXmlLocation ) ) ) {
          String msg = MessageFormat.format( "File {0} does not exist.", webXmlLocation );
          addError( msg, ERR_WEB_XML_LOCATION_NOT_FOUND );
        }
      }
    }
  }
}
