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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JavaProjectSelectionDialog_Test {

  private JavaProjectSelectionDialog dialog;

  @Test
  public void testInitialSelectionWithNull() {
    dialog.setInitialSelection( null );

    assertNull( dialog.getInitialSelection() );
  }

  @Before
  public void setUp() throws Exception {
    dialog = new JavaProjectSelectionDialog( null );
  }

  @After
  public void tearDown() throws Exception {
    TestProject.deleteAll();
  }

  @Test
  public void testInitialSelection_withJavaProject() throws CoreException {
    TestProject testProject = new TestProject();
    IJavaProject javaProject = testProject.getJavaProject();

    dialog.setInitialSelection( javaProject );

    assertEquals( javaProject, dialog.getInitialSelection() );
  }

}
