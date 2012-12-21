/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.config;

import junit.framework.TestCase;

import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;


public class ContextPathValidator_Test extends TestCase {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  public void testValidateContextPath() {
    launchConfig.setUseManualContextPath( true );
    launchConfig.setContextPath( "/foo/bar" );

    validator.validate();

    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_INVALID ) );
    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

  public void testValidateContextPath_WithDisabledUseManualContextPath() {
    launchConfig.setUseManualContextPath( false );
    launchConfig.setContextPath( "foo*bar" );

    validator.validate();

    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_INVALID ) );
    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

  public void testValidateContextPath_MissingLeadingSlash() {
    launchConfig.setUseManualContextPath( true );
    launchConfig.setContextPath( "foo/bar" );

    validator.validate();

    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_INVALID ) );
    assertTrue( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

  public void testValidateContextPath_WithAsterisk() {
    launchConfig.setUseManualContextPath( true );
    launchConfig.setContextPath( "/foo*bar" );

    validator.validate();

    assertTrue( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_INVALID ) );
    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

  public void testValidateContextPath_WithDoubleSlashes() {
    launchConfig.setUseManualContextPath( true );
    launchConfig.setContextPath( "/foo//bar" );

    validator.validate();

    assertTrue( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_INVALID ) );
    assertFalse( validationResult.contains( ContextPathValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

  protected void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new ContextPathValidator( launchConfig, validationResult );
  }

  protected void tearDown() throws Exception {
    Fixture.deleteAllRWTLaunchConfigs();
  }
}
