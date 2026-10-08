/*******************************************************************************
 * Copyright (c) 2011, 2014 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.config;

import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;


class ApplicationClassValidator extends Validator {

  static final int ERR_APPLICATION_CLASS_EMPTY = 8021;

  ApplicationClassValidator( RWTLaunchConfig config, ValidationResult validationResult ) {
    super( config, validationResult );
  }

  void validate() {
    if( LaunchTarget.ENTRY_POINT.equals( config.getLaunchTarget() ) ) {
      if( config.getEntryPoint().length() == 0 ) {
        addError( "The entry point is empty.", ERR_APPLICATION_CLASS_EMPTY );
      }
    } else if( LaunchTarget.APP_CONFIG.equals( config.getLaunchTarget() ) ) {
      if( config.getAppConfig().length() == 0 ) {
        addError( "The application configuration is empty.", ERR_APPLICATION_CLASS_EMPTY );
      }
    }
  }

}
