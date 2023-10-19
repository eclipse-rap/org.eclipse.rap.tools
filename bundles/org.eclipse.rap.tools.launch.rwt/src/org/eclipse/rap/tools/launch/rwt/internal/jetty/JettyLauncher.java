/*******************************************************************************
 * Copyright (c) 2012, 2023 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.jetty;

import org.eclipse.jetty.ee8.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JettyLauncher {

  private static final Logger LOG = LoggerFactory.getLogger(JettyLauncher.class);

  public static void main( String[] args ) {
    try {
      Server server = new Server( Integer.parseInt( args[ 0 ] ) );
      WebAppContext webapp = new WebAppContext();
      webapp.setContextPath( args[ 1 ] );
      webapp.setWar( args[ 2 ] );
      server.setHandler( webapp );
      server.start();
      server.join();
    } catch( Exception exception ) {
      LOG.error( exception.getLocalizedMessage(), exception );
    }
  }
}
