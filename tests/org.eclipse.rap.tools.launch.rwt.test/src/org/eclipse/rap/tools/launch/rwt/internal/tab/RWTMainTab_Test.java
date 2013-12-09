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
package org.eclipse.rap.tools.launch.rwt.internal.tab;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.tools.launch.rwt.internal.tab.RWTMainTab;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RWTMainTab_Test {

  private RWTMainTab mainTab;

  @Before
  public void setUp() throws Exception {
    mainTab = new RWTMainTab();
  }

  @After
  public void tearDown() throws Exception {
    mainTab.dispose();
  }

  @Test
  public void testGetImage() {
    assertNotNull( mainTab.getImage() );
  }

  @Test
  public void testGetName() {
    String name = mainTab.getName();

    assertNotNull( name );
    assertTrue( name.length() > 0 );
  }

  @Test
  public void testCreateControl_callsSetControl() {
    mainTab.createControl( new Shell() );

    assertNotNull( mainTab.getControl() );
  }

}
