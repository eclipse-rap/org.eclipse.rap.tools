/*******************************************************************************
 * Copyright (c) 2011, 2014 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.config;

import static org.eclipse.rap.tools.launch.rwt.internal.config.ApplicationClassValidator.ERR_APPLICATION_CLASS_EMPTY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ApplicationClassValidator_Test {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  @Before
  public void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new ApplicationClassValidator( launchConfig, validationResult );
  }

  @After
  public void tearDown() throws Exception {
    launchConfig.getUnderlyingLaunchConfig().delete();
    TestProject.deleteAll();
  }

  @Test
  public void testValidate_entryPoint_whenEmpty() {
    launchConfig.setLaunchTarget( LaunchTarget.ENTRY_POINT );
    launchConfig.setEntryPoint( "" );

    validator.validate();

    assertTrue( validationResult.contains( ERR_APPLICATION_CLASS_EMPTY ) );
  }

  @Test
  public void testValidate_entryPoint_whenUnused() {
    launchConfig.setLaunchTarget( LaunchTarget.WEB_XML );
    launchConfig.setEntryPoint( "" );

    validator.validate();

    assertFalse( validationResult.contains( ERR_APPLICATION_CLASS_EMPTY ) );
  }

  @Test
  public void testValidate_appConfig_whenEmpty() {
    launchConfig.setLaunchTarget( LaunchTarget.APP_CONFIG );
    launchConfig.setAppConfig( "" );

    validator.validate();

    assertTrue( validationResult.contains( ERR_APPLICATION_CLASS_EMPTY ) );
  }

  @Test
  public void testValidate_appConfig_whenUnused() {
    launchConfig.setLaunchTarget( LaunchTarget.WEB_XML );
    launchConfig.setAppConfig( "" );

    validator.validate();

    assertFalse( validationResult.contains( ERR_APPLICATION_CLASS_EMPTY ) );
  }

}
