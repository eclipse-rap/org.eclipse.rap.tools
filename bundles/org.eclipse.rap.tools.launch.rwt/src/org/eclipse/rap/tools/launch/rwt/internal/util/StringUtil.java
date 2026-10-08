/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.util;


public class StringUtil {

  private static final String SLASH = "/";

  public static String stripTrailingSlash( String input ) {
    String result = input;
    if( result.endsWith( SLASH ) ) {
      result = result.substring( 0, result.length() - 1 );
    }
    return result;
  }

  private StringUtil() {
    // prevent instantiation
  }
}
