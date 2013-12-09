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

import org.eclipse.rap.tools.launch.rwt.internal.tab.VMArgumentsSection;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VMArgumentsSection_Test {

  private Shell shell;
  private VMArgumentsSection vmArgumentsSection;

  @Before
  public void setUp() throws Exception {
    vmArgumentsSection = new VMArgumentsSection();
    shell = new Shell();
  }

  @After
  public void tearDown() throws Exception {
    shell.dispose();
  }

  @Test
  public void testGetName() {
    String name = vmArgumentsSection.getName();

    assertNotNull( name );
    assertTrue( name.length() > 0 );
  }

  @Test
  public void testCreateControl_callsSetControl() {
    vmArgumentsSection.createControl( shell );

    assertNotNull( vmArgumentsSection.getControl() );
  }

}
