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
package org.eclipse.rap.ui.internal.launch.rwt.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ProjectValidator_Test {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  @Before
  public void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new ProjectValidator( launchConfig, validationResult );
  }

  @After
  public void tearDown() throws Exception {
    launchConfig.getUnderlyingLaunchConfig().delete();
    TestProject.deleteAll();
  }

  @Test
  public void testValidate_validProjectName() throws CoreException {
    TestProject testProject = new TestProject();
    testProject.createJavaProject();
    launchConfig.setProjectName( testProject.getName() );

    validator.validate();

    assertFalse( validationResult.contains( ProjectValidator.ERR_PROJECT_NOT_JAVA ) );
  }

  @Test
  public void testValidate_emptyProjectName() {
    validator.validate();

    assertFalse( validationResult.contains( ProjectValidator.ERR_PROJECT_NOT_JAVA ) );
  }

  @Test
  public void testValidateProjectNameWhenNonJavaProject() {
    TestProject testProject = new TestProject();
    launchConfig.setProjectName( testProject.getName() );

    validator.validate();

    assertTrue( validationResult.contains( ProjectValidator.ERR_PROJECT_NOT_JAVA ) );
  }

  @Test
  public void testValidateProjectNameWhenNameIsInvalid() {
    launchConfig.setProjectName( "this/is/an/invalid/project-name" );

    try {
      validator.validate();
    } catch( RuntimeException re ) {
      fail( "validate() must handle invalid project names gracefully" );
    }
  }

}
