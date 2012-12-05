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
package org.eclipse.rap.ui.internal.launch.rwt.config;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class ProjectValidator_Test extends TestCase {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  public void testValidateProjectNameWhenValid() throws CoreException {
    TestProject testProject = new TestProject();
    testProject.createJavaProject();
    launchConfig.setProjectName( testProject.getName() );
    
    validator.validate();
    
    assertFalse( validationResult.contains( ProjectValidator.ERR_PROJECT_NOT_JAVA ) );
  }

  public void testValidateProjectNameWhenEmpty() {
    validator.validate();
    
    assertFalse( validationResult.contains( ProjectValidator.ERR_PROJECT_NOT_JAVA ) );
  }
  
  public void testValidateProjectNameWhenNonJavaProject() {
    TestProject testProject = new TestProject();
    launchConfig.setProjectName( testProject.getName() );

    validator.validate();
    
    assertTrue( validationResult.contains( ProjectValidator.ERR_PROJECT_NOT_JAVA ) );
  }
  
  public void testValidateProjectNameWhenNameIsInvalid() {
    launchConfig.setProjectName( "this/is/an/invalid/project-name" );
    
    try {
      validator.validate();
    } catch( RuntimeException re ) {
      fail( "validate() must handle invalid project names gracefully" );
    }
  }

  protected void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new ProjectValidator( launchConfig, validationResult );
  }
  
  protected void tearDown() throws Exception {
    launchConfig.getUnderlyingLaunchConfig().delete();
    TestProject.deleteAll();
  }
}
