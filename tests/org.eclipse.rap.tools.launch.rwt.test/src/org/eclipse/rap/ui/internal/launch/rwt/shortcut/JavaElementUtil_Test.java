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
package org.eclipse.rap.ui.internal.launch.rwt.shortcut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JavaElementUtil_Test {

  private TestProject project;

  @Before
  public void setUp() throws Exception {
    project = new TestProject();
  }

  @After
  public void tearDown() throws Exception {
    project.delete();
  }

  @Test
  public void testAdapt_withPlainObject() {
    IJavaElement javaElement = JavaElementUtil.adapt( new Object() );

    assertNull( javaElement );
  }

  @Test
  public void testAdapt_withIAdaptable() throws CoreException {
    IJavaProject javaProject = project.getJavaProject();

    IJavaElement javaElement = JavaElementUtil.adapt( javaProject );

    assertSame( javaProject, javaElement );
  }

  @Test
  public void testAdapt_array() throws CoreException {
    IJavaProject javaProject = project.getJavaProject();
    Object[] elements = new Object[] { new Object(), javaProject, new Object() };

    IJavaElement[] javaElements = JavaElementUtil.adapt( elements );

    assertEquals( 1, javaElements.length );
    assertSame( javaProject, javaElements[ 0 ] );
  }

}
