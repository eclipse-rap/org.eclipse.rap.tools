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
package org.eclipse.rap.ui.internal.launch.rwt.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class StringArrays_Test {

  @Test
  public void testAppend_withEmptyArray() {
    String[] strings = StringArrays.append( new String[ 0 ], "x" );
    assertEquals( 1, strings.length );
    assertEquals( "x", strings[ 0 ] );
  }

  @Test
  public void testAppend_withNonEmptyArray() {
    String[] strings = StringArrays.append( new String[] { "x" }, "y" );
    assertEquals( 2, strings.length );
    assertEquals( "x", strings[ 0 ] );
    assertEquals( "y", strings[ 1 ] );
  }

  @Test
  public void testContains_withNonExistingString() {
    String[] strings = { "a", "b" };
    boolean contains = StringArrays.contains( strings, "c" );
    assertFalse( contains );
  }

  @Test
  public void testContains_withExistingString() {
    String[] strings = { "a", "b" };
    boolean contains = StringArrays.contains( strings, "b" );
    assertTrue( contains );
  }

  @Test
  public void testContains_withEmptyStrings() {
    boolean contains = StringArrays.contains( new String[ 0 ], "c" );
    assertFalse( contains );
  }

}
