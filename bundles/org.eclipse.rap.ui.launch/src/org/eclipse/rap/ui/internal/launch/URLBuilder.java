/*******************************************************************************
 * Copyright (c) 2007, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;

public final class URLBuilder {

  private static final String EMPTY = ""; //$NON-NLS-1$
  static final String SLASH = "/"; //$NON-NLS-1$

  private static final String PROTOCOL = "http"; //$NON-NLS-1$
  private static final String HOST = "127.0.0.1"; //$NON-NLS-1$
  private static final String QUERY_STARTUP = "?startup="; //$NON-NLS-1$
  private static final String QUERY_STARTUP_TEST
    = "?startup=rapjunit&testentrypoint="; //$NON-NLS-1$

  public static String getHost() {
    return HOST;
  }

  public static URL fromLaunchConfig( RAPLaunchConfig config, int port, boolean testMode )
    throws CoreException, MalformedURLException
  {
    String path = getUrlPath( config );
    String query = getUrlQuery( config, testMode );
    return new URL( PROTOCOL, HOST, port, path + query );
  }

  private static String getUrlPath( RAPLaunchConfig config ) throws CoreException {
    String contextPath = getContextPath( config );
    String servletName = getServletName( config );
    StringBuffer buffer = new StringBuffer();
    buffer.append( SLASH );
    if( !EMPTY.equals( contextPath ) ) {
      buffer.append( contextPath );
      buffer.append( SLASH );
    }
    buffer.append( servletName );
    return buffer.toString();
  }

  private static String getUrlQuery( RAPLaunchConfig config, boolean testMode )
    throws CoreException
  {
    String entryPoint = config.getEntryPoint();
    String query = EMPTY;
    if( testMode ) {
      query = QUERY_STARTUP_TEST + entryPoint;
    } else if( !EMPTY.equals( entryPoint ) ) {
      query = QUERY_STARTUP + entryPoint;
    }
    return query;
  }

  private static String getServletName( RAPLaunchConfig config ) throws CoreException {
    String servletName = config.getServletName();
    return stripLeadingAndTrailingSlashes( servletName );
  }

  private static String getContextPath( RAPLaunchConfig config ) throws CoreException {
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
