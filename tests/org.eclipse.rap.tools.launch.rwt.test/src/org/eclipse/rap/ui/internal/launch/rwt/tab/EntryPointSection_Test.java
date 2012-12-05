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

public class EntryPointSection_Test extends TestCase {

  private Shell shell;
  private EntryPointSection entryPointSection;

  public void testCreateControlCallsSetControl() {
    entryPointSection.createControl( shell );
    assertTrue( entryPointSection.getControl() != null );
  }

  protected void setUp() throws Exception {
    entryPointSection = new EntryPointSection();
    shell = new Shell();
  }

  protected void tearDown() throws Exception {
    shell.dispose();
  }
}
