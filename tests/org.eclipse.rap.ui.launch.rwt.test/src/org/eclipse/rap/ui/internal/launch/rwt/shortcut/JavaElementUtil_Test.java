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
package org.eclipse.rap.ui.internal.launch.rwt.shortcut;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class JavaElementUtil_Test extends TestCase {

  private TestProject project;

  public void testAdaptWithPlainObject() {
    IJavaElement javaElement = JavaElementUtil.adapt( new Object() );
    assertNull( javaElement );
  }

  public void testAdaptWithIAdaptable() throws CoreException {
    IJavaProject javaProject = project.getJavaProject();
    IJavaElement javaElement = JavaElementUtil.adapt( javaProject );
    assertSame( javaProject, javaElement );
  }
  
  public void testAdaptArray() throws CoreException {
    IJavaProject javaProject = project.getJavaProject();
    Object[] elements = new Object[] { new Object(), javaProject, new Object() };
    IJavaElement[] javaElements = JavaElementUtil.adapt( elements );
    
    assertEquals( 1, javaElements.length );
    assertSame( javaProject, javaElements[ 0 ] );
  }
  
  protected void setUp() throws Exception {
    project = new TestProject();
  }
  
  protected void tearDown() throws Exception {
    project.delete();
  }

}
