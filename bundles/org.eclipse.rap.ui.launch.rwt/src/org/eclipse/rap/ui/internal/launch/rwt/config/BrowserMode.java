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


public final class BrowserMode {

  public static final BrowserMode INTERNAL
    = new BrowserMode( "INTERNAL" ); //$NON-NLS-1$
  public static final BrowserMode EXTERNAL 
    = new BrowserMode( "EXTERNAL" ); //$NON-NLS-1$

  public static BrowserMode[] values() {
    return new BrowserMode[]{ INTERNAL, EXTERNAL };
  }

  public static BrowserMode parse( final String name ) {
    BrowserMode result = null;
    BrowserMode[] knownValues = values();
    for( int i = 0; result == null && i < knownValues.length; i++ ) {
      if( knownValues[ i ].getName().equalsIgnoreCase( name ) ) {
        result = knownValues[ i ];
      }
    }
    if( result == null ) {
      String msg = "Unknown BrowserMode: " + name; //$NON-NLS-1$
      throw new IllegalArgumentException( msg );
    }
    return result;
  }
  private final String name;

  private BrowserMode( final String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return name;
  }
}
