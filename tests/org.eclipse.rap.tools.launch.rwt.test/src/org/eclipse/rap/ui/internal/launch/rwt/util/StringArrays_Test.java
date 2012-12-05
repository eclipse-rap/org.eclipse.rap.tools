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

import junit.framework.TestCase;


public class StringArrays_Test extends TestCase {

  public void testAppendWithEmptyArray() {
    String[] strings = StringArrays.append( new String[ 0 ], "x" );
    assertEquals( 1, strings.length );
    assertEquals( "x", strings[ 0 ] );
  }

  public void testAppendWithNonEmptyArray() {
    String[] strings = StringArrays.append( new String[] { "x" }, "y" );
    assertEquals( 2, strings.length );
    assertEquals( "x", strings[ 0 ] );
    assertEquals( "y", strings[ 1 ] );
  }
  
  public void testContainsWithNonExistingString() {
    String[] strings = { "a", "b" };
    boolean contains = StringArrays.contains( strings, "c" );
    assertFalse( contains );
  }

  public void testContainsWithExistingString() {
    String[] strings = { "a", "b" };
    boolean contains = StringArrays.contains( strings, "b" );
    assertTrue( contains );
  }
  
  public void testContainsWithEmptyStrings() {
    boolean contains = StringArrays.contains( new String[ 0 ], "c" );
    assertFalse( contains );
  }
}
