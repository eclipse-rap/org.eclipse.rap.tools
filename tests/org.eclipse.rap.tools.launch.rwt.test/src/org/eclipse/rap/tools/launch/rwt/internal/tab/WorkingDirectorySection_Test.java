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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.tab.WorkingDirectorySection;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestProject;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WorkingDirectorySection_Test {

  private ILaunchConfigurationWorkingCopy launchConfig;
  private TestableWorkingDirectorySection workingDirectorySection;

  @Before
  public void setUp() throws Exception {
    launchConfig = Fixture.createRWTLaunchConfig();
    workingDirectorySection = new TestableWorkingDirectorySection();
  }

  @After
  public void tearDown() throws Exception {
    launchConfig.delete();
    TestProject.deleteAll();
  }

  @Test
  public void testGetName() {
    String name = workingDirectorySection.getName();
    assertNotNull( name );
    assertTrue( name.length() > 0 );
  }

  @Test
  public void testCreateControl_callsSetControl() {
    workingDirectorySection.createControl( new Shell() );
    assertNotNull( workingDirectorySection.getControl() );
  }

  @Test
  public void testGetProject_whenProjectIsEmpty() throws CoreException {
    setLaunchConfigurationProject( "" );

    IProject project = workingDirectorySection.getProject( launchConfig );

    assertNull( project );
  }

  @Test
  public void testGetProject_whenProjectIsExisting() throws CoreException {
    TestProject testProject = new TestProject();
    setLaunchConfigurationProject( testProject.getName() );

    IProject returnedProject = workingDirectorySection.getProject( launchConfig );

    assertEquals( testProject.getProject(), returnedProject );
  }

  @Test
  public void testGetProject_whenProjectIsClosed() throws CoreException {
    TestProject testProject = new TestProject();
    testProject.getProject().close( new NullProgressMonitor() );
    setLaunchConfigurationProject( testProject.getName() );

    IProject returnedProject = workingDirectorySection.getProject( launchConfig );

    assertNull( returnedProject );
  }

  @Test
  public void testGetProject_whenProjectIsNonExisting() throws CoreException {
    setLaunchConfigurationProject( "does.not.exist" );

    IProject project = workingDirectorySection.getProject( launchConfig );

    assertNull( project );
  }

  @Test
  public void testGetProject_whenProjectNameIsInvalid() throws CoreException {
    setLaunchConfigurationProject( "this/is/an/invalid/projectName" );

    IProject project = workingDirectorySection.getProject( launchConfig );

    assertNull( project );
  }

  @Test
  public void testLog() {
    IStatus status = new Status( IStatus.ERROR, "pluginId", "message" );
    CoreException exception = new CoreException( status );

    workingDirectorySection.log( exception );

    assertEquals( "message", workingDirectorySection.getErrorMessage() );
  }

  private void setLaunchConfigurationProject( String projectName ) {
    new RWTLaunchConfig( launchConfig ).setProjectName( projectName );
  }

  private static class TestableWorkingDirectorySection extends WorkingDirectorySection {
  }

}
