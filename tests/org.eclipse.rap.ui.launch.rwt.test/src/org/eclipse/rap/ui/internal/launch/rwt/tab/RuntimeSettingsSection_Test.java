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

import org.eclipse.swt.widgets.Shell;

import junit.framework.TestCase;

public class RuntimeSettingsSection_Test extends TestCase {

  private Shell shell;
  private RuntimeSettingsSection runtimeSettingsSection;

  public void testGetName() {
    String name = runtimeSettingsSection.getName();
    assertNotNull( name );
    assertTrue( name.length() > 0 );
  }
  
  public void testCreateControlCallsSetControl() {
    runtimeSettingsSection.createControl( shell );
    assertNotNull( runtimeSettingsSection.getControl() );
  }

  protected void setUp() throws Exception {
    runtimeSettingsSection = new RuntimeSettingsSection();
    shell = new Shell();
  }

  protected void tearDown() throws Exception {
    shell.dispose();
  }
}
