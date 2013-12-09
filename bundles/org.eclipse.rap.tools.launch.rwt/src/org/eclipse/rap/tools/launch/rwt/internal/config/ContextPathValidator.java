/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.config;


public class ContextPathValidator extends Validator {

  static final int ERR_CONTEXT_PATH_INVALID = 8041;
  static final int ERR_CONTEXT_PATH_LEADING_SLASH = 8042;

  ContextPathValidator( RWTLaunchConfig config, ValidationResult validationResult ) {
    super( config, validationResult );
  }

  @Override
  void validate() {
    if( config.getUseManualContextPath() ) {
      validateLeadingSlash();
      vaidateCharacters();
    }
  }

  private void validateLeadingSlash() {
    if( !config.getContextPath().startsWith( "/" ) ) {
      addError( "The context path must start with a slash.", ERR_CONTEXT_PATH_LEADING_SLASH );
    }
  }

  private void vaidateCharacters() {
    String contextPath = config.getContextPath();
    if( contextPath.startsWith( "/" ) ) {
      contextPath = contextPath.substring( 1 );
    }
    boolean invalid = false;
    if( contextPath.indexOf( "//" ) != -1 ) {
      invalid = true;
    }
    for( int i = 0; i < contextPath.length() && !invalid; i++ ) {
      char ch = contextPath.charAt( i );
      boolean isLetterOrDigit = Character.isLetterOrDigit( ch );
      boolean isValidSpecialChar = "/_-.".indexOf( ch ) != -1;
      if( !isLetterOrDigit && !isValidSpecialChar ) {
        invalid = true;
      }
    }
    if( invalid ) {
      addError( "The context path contains invalid characters.", ERR_CONTEXT_PATH_INVALID );
    }
  }
}
