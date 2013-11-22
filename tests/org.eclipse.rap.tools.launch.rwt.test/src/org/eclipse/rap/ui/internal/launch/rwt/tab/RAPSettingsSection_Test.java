/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RAPSettingsSection_Test {

  private Shell shell;
  private RAPSettingsSection rapSettingsSection;

  @Before
  public void setUp() throws Exception {
    rapSettingsSection = new RAPSettingsSection();
    shell = new Shell();
  }

  @After
  public void tearDown() throws Exception {
    shell.dispose();
  }

  @Test
  public void testGetName() {
    String name = rapSettingsSection.getName();

    assertNotNull( name );
    assertTrue( name.length() > 0 );
  }

  @Test
  public void testCreateControl_callsSetControl() {
    rapSettingsSection.createControl( shell );

    assertNotNull( rapSettingsSection.getControl() );
  }

}
