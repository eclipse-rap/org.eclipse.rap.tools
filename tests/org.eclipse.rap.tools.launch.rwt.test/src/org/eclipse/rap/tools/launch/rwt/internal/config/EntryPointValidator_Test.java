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
package org.eclipse.rap.tools.launch.rwt.internal.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.tools.launch.rwt.internal.config.EntryPointValidator;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.ValidationResult;
import org.eclipse.rap.tools.launch.rwt.internal.config.Validator;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class EntryPointValidator_Test {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  @Before
  public void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new EntryPointValidator( launchConfig, validationResult );
  }

  @After
  public void tearDown() throws Exception {
    launchConfig.getUnderlyingLaunchConfig().delete();
    TestProject.deleteAll();
  }

  @Test
  public void testValidateWhenEmpty() {
    launchConfig.setLaunchTarget( LaunchTarget.ENTRY_POINT );
    launchConfig.setEntryPoint( "" );

    validator.validate();

    assertTrue( validationResult.contains( EntryPointValidator.ERR_ENTRY_POINT_EMPTY ) );
  }

  @Test
  public void testValidateWhenUnused() {
    launchConfig.setLaunchTarget( LaunchTarget.WEB_XML );
    launchConfig.setEntryPoint( "" );

    validator.validate();

    assertFalse( validationResult.contains( EntryPointValidator.ERR_ENTRY_POINT_EMPTY ) );
  }

}
