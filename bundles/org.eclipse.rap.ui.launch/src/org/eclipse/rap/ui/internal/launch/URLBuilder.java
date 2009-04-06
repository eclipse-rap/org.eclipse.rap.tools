/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.*;

public final class URLBuilder {

  private static final String EMPTY = ""; //$NON-NLS-1$
  private static final String SLASH = "/"; //$NON-NLS-1$

  private static final String PROTOCOL = "http"; //$NON-NLS-1$
  private static final String HOST = "127.0.0.1"; //$NON-NLS-1$
  private static final String QUERY_STARTUP = "?startup="; //$NON-NLS-1$
  private static final String QUERY_STARTUP_TEST
    = "?startup=rapjunit&testentrypoint="; //$NON-NLS-1$

  public static String getHost() {
    return HOST;
  }
  
  public static URL fromLaunchConfig( final RAPLaunchConfig config, 
                                      final int port, 
                                      final boolean testMode ) 
    throws CoreException, MalformedURLException 
  {
    String servletName = config.getServletName();
    if( !servletName.startsWith( SLASH ) ) { 
      servletName = SLASH + servletName;
    }
    String entryPoint = config.getEntryPoint();
    String query = EMPTY;
    if( testMode ) {
      query = QUERY_STARTUP_TEST + entryPoint;
    } else if( !EMPTY.equals( entryPoint ) ) {
      query = QUERY_STARTUP + entryPoint;
    }
    return new URL( PROTOCOL, HOST, port, servletName + query );
  }

  private URLBuilder() {
    // prevent instantiation
  }
}
