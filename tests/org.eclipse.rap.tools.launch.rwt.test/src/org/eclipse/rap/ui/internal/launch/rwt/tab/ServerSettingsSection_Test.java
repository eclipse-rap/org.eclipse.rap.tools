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
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerSettingsSection_Test {

  private Shell shell;
  private ServerSettingsSection runtimeSettingsSection;

  @Before
  public void setUp() throws Exception {
    runtimeSettingsSection = new ServerSettingsSection();
    shell = new Shell();
  }

  @After
  public void tearDown() throws Exception {
    shell.dispose();
  }

  @Test
  public void testGetName() {
    String name = runtimeSettingsSection.getName();

    assertNotNull( name );
    assertTrue( name.length() > 0 );
  }

  @Test
  public void testCreateControl_callsSetControl() {
    runtimeSettingsSection.createControl( shell );

    assertNotNull( runtimeSettingsSection.getControl() );
  }

}
