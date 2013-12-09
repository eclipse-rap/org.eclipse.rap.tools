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

import static org.junit.Assert.assertTrue;

import org.eclipse.rap.tools.launch.rwt.internal.tab.EntryPointSection;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class EntryPointSection_Test {

  private Shell shell;
  private EntryPointSection entryPointSection;

  @Before
  public void setUp() throws Exception {
    entryPointSection = new EntryPointSection();
    shell = new Shell();
  }

  @After
  public void tearDown() throws Exception {
    shell.dispose();
  }

  @Test
  public void testCreateControlCallsSetControl() {
    entryPointSection.createControl( shell );

    assertTrue( entryPointSection.getControl() != null );
  }

}
