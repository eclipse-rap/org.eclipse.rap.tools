/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.config;

import static org.junit.Assert.assertSame;

import org.eclipse.rap.tools.launch.rwt.internal.config.BrowserMode;
import org.junit.Test;


public class BrowserMode_Test {

  @Test( expected = IllegalArgumentException.class )
  public void testParse_withUnknownName() {
    BrowserMode.parse( "foo" );
  }

  @Test
  public void testParse_withINTERNAL() {
    BrowserMode mode = BrowserMode.parse( BrowserMode.INTERNAL.toString() );

    assertSame( BrowserMode.INTERNAL, mode );
  }

  @Test
  public void testParse_withEXTERNAL() {
    BrowserMode mode = BrowserMode.parse( BrowserMode.EXTERNAL.toString() );

    assertSame( BrowserMode.EXTERNAL, mode );
  }

  @Test
  public void testParse_ignoresCase() {
    String lowerCaseMode = BrowserMode.EXTERNAL.toString().toLowerCase();

    BrowserMode mode = BrowserMode.parse( lowerCaseMode );

    assertSame( BrowserMode.EXTERNAL, mode );
  }

  @Test
  public void testToStringParse_compatibilityWithINTERNAL() {
    String string = BrowserMode.INTERNAL.toString();

    assertSame( BrowserMode.INTERNAL, BrowserMode.parse( string ) );
  }

  @Test
  public void testToStringParse_compatibilityWithEXTERNAL() {
    String string = BrowserMode.EXTERNAL.toString();

    assertSame( BrowserMode.EXTERNAL, BrowserMode.parse( string ) );
  }

}
