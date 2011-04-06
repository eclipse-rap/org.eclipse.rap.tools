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

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;


public class ListUtil_Test extends TestCase {
  
  private List list;

  public void testAddWithEmptyArray() {
    ListUtil.add( list, new Object[ 0 ] );
    
    assertEquals( 0, list.size() );
  }
  
  public void testWithNonEmptyArray() {
    Object[] array = new Object[] { new Object() };
    ListUtil.add( list, array );
    
    assertEquals( 1, list.size() );
    assertSame( array[ 0 ], list.get( 0 ) );
  }
  
  public void testWithNonEmptyList() {
    list.add( new Object() );
    Object[] array = new Object[] { new Object() };
    ListUtil.add( list, array );
    
    assertEquals( 2, list.size() );
    assertSame( array[ 0 ], list.get( 1 ) );
  }
  
  protected void setUp() throws Exception {
    list = new LinkedList();
  }
}
