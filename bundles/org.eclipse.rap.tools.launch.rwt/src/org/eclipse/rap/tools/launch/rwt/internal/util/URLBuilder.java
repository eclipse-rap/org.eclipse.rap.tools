/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.util;

import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;


public final class URLBuilder {

  private static final String EMPTY = ""; //$NON-NLS-1$
  private static final String SLASH = "/"; //$NON-NLS-1$

  private static final String PROTOCOL = "http"; //$NON-NLS-1$
  private static final String HOST = "127.0.0.1"; //$NON-NLS-1$

  public static String fromLaunchConfig( RWTLaunchConfig config ) {
    String port = config.getUseManualPort() ? Integer.toString( config.getPort() ) : "<PORT>";
    return PROTOCOL + "://" + HOST + ":" + port + getUrlPath( config );
  }

  private static String getUrlPath( RWTLaunchConfig config ) {
    String contextPath = getContextPath( config );
    String servletPath = getServletPath( config );
    StringBuffer buffer = new StringBuffer();
    buffer.append( SLASH );
    if( !EMPTY.equals( contextPath ) ) {
      buffer.append( contextPath );
      buffer.append( SLASH );
    }
    buffer.append( servletPath );
    return buffer.toString();
  }

  private static String getServletPath( RWTLaunchConfig config ) {
    String servletPath = config.getServletPath();
    return stripLeadingAndTrailingSlashes( servletPath );
  }

  private static String getContextPath( RWTLaunchConfig config ) {
    String contextPath = "";
    if( config.getUseManualContextPath() ) {
      contextPath = config.getContextPath();
    }
    return stripLeadingAndTrailingSlashes( contextPath );
  }

  private static String stripLeadingAndTrailingSlashes( String input ) {
    String result = input;
    if( result.startsWith( SLASH ) ) {
      result = result.substring( 1 );
    }
    if( result.endsWith( SLASH ) ) {
      result = result.substring( 0, result.length() - 1 );
    }
    return result;
  }

  private URLBuilder() {
    // prevent instantiation
  }

}
