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
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Shell;


public class RWTMainTab_Test extends TestCase {
  
  private RWTMainTab mainTab;
  
  public void testGetImage() {
    assertNotNull( mainTab.getImage() );
  }

  public void testGetName() {
    String name = mainTab.getName();
    
    assertNotNull( name );
    assertTrue( name.length() > 0 );
  }
  
  public void testCreateControlCallsSetControl() {
    mainTab.createControl( new Shell() );
    
    assertNotNull( mainTab.getControl() );
  }

  protected void setUp() throws Exception {
    mainTab = new RWTMainTab();
  }
  
  protected void tearDown() throws Exception {
    mainTab.dispose();
  }
}
