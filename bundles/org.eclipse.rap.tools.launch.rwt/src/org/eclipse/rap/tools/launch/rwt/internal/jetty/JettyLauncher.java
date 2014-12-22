/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.WebAppContext;


public class JettyLauncher {

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
      Log.getLogger( JettyLauncher.class.getName() ).warn( Log.EXCEPTION, exception );
    }
  }
}
