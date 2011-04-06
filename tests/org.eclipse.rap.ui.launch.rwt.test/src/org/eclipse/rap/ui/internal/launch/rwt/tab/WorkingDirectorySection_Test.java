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

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;
import org.eclipse.swt.widgets.Shell;


public class WorkingDirectorySection_Test extends TestCase {
  
  private static class TestableWorkingDirectorySection extends WorkingDirectorySection {
  }
  
  private ILaunchConfigurationWorkingCopy launchConfig;
  private TestableWorkingDirectorySection workingDirectorySection;

  public void testGetName() {
    String name = workingDirectorySection.getName();
    assertNotNull( name );
    assertTrue( name.length() > 0 );
  }

  public void testCreateControlCallsSetControl() {
    workingDirectorySection.createControl( new Shell() );
    assertNotNull( workingDirectorySection.getControl() );
  }

  public void testGetProjectWhenProjectIsEmpty() throws CoreException {
    setLaunchConfigurationProject( "" );

    IProject project = workingDirectorySection.getProject( launchConfig );
    
    assertNull( project );
  }

  public void testGetProjectWhenProjectIsExisting() throws CoreException {
    TestProject testProject = new TestProject();
    setLaunchConfigurationProject( testProject.getName() );
    
    IProject returnedProject = workingDirectorySection.getProject( launchConfig );
    
    assertEquals( testProject.getProject(), returnedProject );
  }

  public void testGetProjectWhenProjectIsClosed() throws CoreException {
    TestProject testProject = new TestProject();
    testProject.getProject().close( new NullProgressMonitor() );
    setLaunchConfigurationProject( testProject.getName() );
    
    IProject returnedProject = workingDirectorySection.getProject( launchConfig );
    
    assertNull( returnedProject );
  }
  
  public void testGetProjectWhenProjectIsNonExisting() throws CoreException {
    setLaunchConfigurationProject( "does.not.exist" );
    
    IProject project = workingDirectorySection.getProject( launchConfig );
    
    assertNull( project );
  }
  
  public void testGetProjectWhenProjectNameIsInvalid() throws CoreException {
    setLaunchConfigurationProject( "this/is/an/invalid/projectName" );
    
    IProject project = workingDirectorySection.getProject( launchConfig );
    
    assertNull( project );
  }
  
  public void testLog() {
    IStatus status = new Status( IStatus.ERROR, "pluginId", "message" );
    CoreException exception = new CoreException( status );
    
    workingDirectorySection.log( exception );
    
    assertEquals( "message", workingDirectorySection.getErrorMessage() );
  }
  
  protected void setUp() throws Exception {
    launchConfig = Fixture.createRWTLaunchConfig();
    workingDirectorySection = new TestableWorkingDirectorySection();
  }
  
  protected void tearDown() throws Exception {
    launchConfig.delete();
    TestProject.deleteAll();
  }

  private void setLaunchConfigurationProject( String projectName ) {
    new RWTLaunchConfig( launchConfig ).setProjectName( projectName );
  }
}
