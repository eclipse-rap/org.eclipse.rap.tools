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
  static final int ERR_SERVLET_PATH_INVALID = 8031;

  ServletPathValidator( RWTLaunchConfig config, ValidationResult validationResult ) {
    super( config, validationResult );
  }

  void validate() {
    if( config.getOpenBrowser() ) {
      validateNotEmpty();
      vaidateCharacters();
    }
  }

  private void validateNotEmpty() {
    if( config.getServletPath().length() == 0 ) {
      addError( "The servlet path must not be empty.", ERR_SERVLET_PATH_EMPTY );
    }
  }

  private void vaidateCharacters() {
    String servletPath = config.getServletPath();
    if( containsChars( servletPath, new char[] { '*', '?', '/', '\\' } ) ) {
      addError( "The servlet path contains invalid characters.", ERR_SERVLET_PATH_INVALID );
    }
  }

  private static boolean containsChars( String string, char[] chars ) {
    boolean hasInvalidChar = false;
    String pattern = new String( chars );
    for( int i = 0; !hasInvalidChar && i < string.length(); i++ ) {
      if( pattern.indexOf( string.charAt( i ) ) != -1 ) {
        hasInvalidChar = true;
      }
    }
    return hasInvalidChar;
  }
}
