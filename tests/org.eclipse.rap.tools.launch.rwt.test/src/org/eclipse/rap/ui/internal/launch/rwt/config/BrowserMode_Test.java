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

import org.eclipse.rap.ui.internal.launch.rwt.config.BrowserMode;

import junit.framework.TestCase;


public class BrowserMode_Test extends TestCase {

  public void testParseWithUnknownName() {
    try {
      BrowserMode.parse( "foo" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testParseWithINTERNAL() {
    BrowserMode mode = BrowserMode.parse( BrowserMode.INTERNAL.toString() );
    assertSame( BrowserMode.INTERNAL, mode );
  }

  public void testParseWithEXTERNAL() {
    BrowserMode mode = BrowserMode.parse( BrowserMode.EXTERNAL.toString() );
    assertSame( BrowserMode.EXTERNAL, mode );
  }

  public void testParseIgnoresCase() {
    String lowerCaseMode = BrowserMode.EXTERNAL.toString().toLowerCase();
    BrowserMode mode = BrowserMode.parse( lowerCaseMode );
    assertSame( BrowserMode.EXTERNAL, mode );
  }
  
  public void testToStringParseCompatibilityWithINTERNAL() {
    String string = BrowserMode.INTERNAL.toString();
    assertSame( BrowserMode.INTERNAL, BrowserMode.parse( string ) );
  }

  public void testToStringParseCompatibilityWithEXTERNAL() {
    String string = BrowserMode.EXTERNAL.toString();
    assertSame( BrowserMode.EXTERNAL, BrowserMode.parse( string ) );
  }
}
