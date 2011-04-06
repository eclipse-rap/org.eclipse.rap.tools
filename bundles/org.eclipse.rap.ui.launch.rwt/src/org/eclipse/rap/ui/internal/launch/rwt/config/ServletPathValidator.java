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


class ServletPathValidator extends Validator {

  static final int ERR_SERVLET_PATH_EMPTY = 8030;

  ServletPathValidator( RWTLaunchConfig config, ValidationResult validationResult ) {
    super( config, validationResult );
  }

  void validate() {
    if( config.getUseWebXml() && config.getOpenBrowser() ) {
      if( config.getServletPath().length() == 0 ) {
        addError( "The servlet path must not be empty.", ERR_SERVLET_PATH_EMPTY );
      }
    }
  }
}
