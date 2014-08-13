/*******************************************************************************
 * Copyright (c) 2011, 2014 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.config;


public class ServletPathValidator extends Validator {

  static final int ERR_SERVLET_PATH_EMPTY = 8030;
  static final int ERR_SERVLET_PATH_INVALID = 8031;
  static final int ERR_SERVLET_PATH_LEADING_SLASH = 8032;

  private String servletPath;

  public ServletPathValidator( RWTLaunchConfig config, ValidationResult validationResult ) {
    super( config, validationResult );
  }

  public ServletPathValidator( String servletPath, ValidationResult validationResult ) {
    super( null, validationResult );
    this.servletPath = servletPath;
  }

  public void validate() {
    if( config == null || config.getOpenBrowser() ) {
      validateNotEmpty();
      validateLeadingSlash();
      vaidateCharacters();
    }
  }

  private void validateNotEmpty() {
    if( getServletPath().length() == 0 ) {
      addError( "The servlet path must not be empty.", ERR_SERVLET_PATH_EMPTY );
    }
  }

  private void validateLeadingSlash() {
    if( !getServletPath().startsWith( "/" ) ) {
      addError( "The servlet path must start with a slash.", ERR_SERVLET_PATH_LEADING_SLASH );
    }
  }

  private void vaidateCharacters() {
    String servletPath = getServletPath();
    if( servletPath.startsWith( "/" ) ) {
      servletPath = servletPath.substring( 1 );
    }
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

  private String getServletPath() {
    return config != null ? config.getServletPath() : servletPath;
  }

}
