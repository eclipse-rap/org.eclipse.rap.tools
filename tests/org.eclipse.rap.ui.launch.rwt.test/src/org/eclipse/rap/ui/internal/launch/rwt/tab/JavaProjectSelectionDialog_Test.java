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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.rap.ui.internal.launch.rwt.tab.JavaProjectSelectionDialog;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;

import junit.framework.TestCase;


public class JavaProjectSelectionDialog_Test extends TestCase {
  
  private JavaProjectSelectionDialog dialog;

  public void testInitialSelectionWithNull() {
    dialog.setInitialSelection( null );
    assertNull( dialog.getInitialSelection() );
  }

  public void testInitialSelectionWithJavaProject() throws CoreException {
    TestProject testProject = new TestProject();
    IJavaProject javaProject = testProject.getJavaProject();
    
    dialog.setInitialSelection( javaProject );
    
    assertEquals( javaProject, dialog.getInitialSelection() );
  }
  
  protected void setUp() throws Exception {
    dialog = new JavaProjectSelectionDialog( null );
  }
  
  protected void tearDown() throws Exception {
    TestProject.deleteAll();
  }
}
