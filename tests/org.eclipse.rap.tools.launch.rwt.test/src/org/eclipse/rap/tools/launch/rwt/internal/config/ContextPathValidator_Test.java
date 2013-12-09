/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.tools.launch.rwt.internal.config.ContextPathValidator;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.ValidationResult;
import org.eclipse.rap.tools.launch.rwt.internal.config.Validator;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ContextPathValidator_Test {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  @Before
  public void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new ContextPathValidator( launchConfig, validationResult );
  }

  @After
  public void tearDown() throws Exception {
    Fixture.deleteAllRWTLaunchConfigs();
  }

  @Test
  public void testValidateContextPath() {
    launchConfig.setUseManualContextPath( true );
    launchConfig.setContextPath( "/foo/bar" );

    validator.validate();

    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_INVALID ) );
    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

  @Test
  public void testValidateContextPath_withDisabledUseManualContextPath() {
    launchConfig.setUseManualContextPath( false );
    launchConfig.setContextPath( "foo*bar" );

    validator.validate();

    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_INVALID ) );
    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

  @Test
  public void testValidateContextPath_withMissingLeadingSlash() {
    launchConfig.setUseManualContextPath( true );
    launchConfig.setContextPath( "foo/bar" );

    validator.validate();

    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_INVALID ) );
    assertTrue( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

  @Test
  public void testValidateContextPath_withAsterisk() {
    launchConfig.setUseManualContextPath( true );
    launchConfig.setContextPath( "/foo*bar" );

    validator.validate();

    assertTrue( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_INVALID ) );
    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

  @Test
  public void testValidateContextPath_withDoubleSlashes() {
    launchConfig.setUseManualContextPath( true );
    launchConfig.setContextPath( "/foo//bar" );

    validator.validate();

    assertTrue( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_INVALID ) );
    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

}
