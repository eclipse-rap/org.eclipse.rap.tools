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
package org.eclipse.rap.ui.internal.launch.rwt.util;


public class StringArrays {

  public static String[] append( String[] strings, String string ) {
    String[] result = new String[ strings.length + 1 ];
    System.arraycopy( strings, 0, result, 0, strings.length );
    result[ strings.length ] = string;
    return result;
  }
  
  public static boolean contains( String[] strings, String string ) {
    boolean result = false;
    for( int i = 0; !result && i < strings.length; i++ ) {
      if( strings[ i ].equals( string ) ) {
        result = true;
      }
    }
    return result;
  }

  private StringArrays() {
    // prevent instantiation
  }
}
