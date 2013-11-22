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
import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class ListUtil_Test {

  private List<Object> list;

  @Before
  public void setUp() throws Exception {
    list = new LinkedList<Object>();
  }

  @Test
  public void testAdd_withEmptyArray() {
    ListUtil.add( list, new Object[ 0 ] );

    assertEquals( 0, list.size() );
  }

  @Test
  public void testAdd_withNonEmptyArray() {
    Object[] array = new Object[] { new Object() };

    ListUtil.add( list, array );

    assertEquals( 1, list.size() );
    assertSame( array[ 0 ], list.get( 0 ) );
  }

  @Test
  public void testAdd_withNonEmptyList() {
    list.add( new Object() );
    Object[] array = new Object[] { new Object() };

    ListUtil.add( list, array );

    assertEquals( 2, list.size() );
    assertSame( array[ 0 ], list.get( 1 ) );
  }
}
