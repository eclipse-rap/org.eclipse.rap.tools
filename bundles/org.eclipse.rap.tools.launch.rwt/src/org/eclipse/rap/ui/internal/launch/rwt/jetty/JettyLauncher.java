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
package org.eclipse.rap.ui.internal.launch.rwt.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.WebAppContext;


public class JettyLauncher {

  public static void main( String[] args ) {
    try {
      Server server = new Server();
      ContextHandlerCollection contexts = new ContextHandlerCollection();
      server.setHandler( contexts );
      SocketConnector connector = new SocketConnector();
      connector.setPort( Integer.parseInt( args[ 0 ] ) );
      server.setConnectors( new Connector[]{ connector } );

      WebAppContext webapp = new WebAppContext();
      webapp.setContextPath( args[ 1 ] );
      webapp.setWar( args[ 2 ] );
      contexts.addHandler(webapp);

      server.start();
      server.join();
    } catch( Exception exception ) {
      Log.getLogger( JettyLauncher.class.getName() ).warn( Log.EXCEPTION, exception );
    }
  }
}
