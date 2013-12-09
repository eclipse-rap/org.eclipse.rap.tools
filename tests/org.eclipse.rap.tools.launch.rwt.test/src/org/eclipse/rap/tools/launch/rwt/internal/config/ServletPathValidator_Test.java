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

import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.ServletPathValidator;
import org.eclipse.rap.tools.launch.rwt.internal.config.ValidationResult;
import org.eclipse.rap.tools.launch.rwt.internal.config.Validator;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServletPathValidator_Test {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  @Before
  public void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new ServletPathValidator( launchConfig, validationResult );
  }

  @After
  public void tearDown() throws Exception {
    Fixture.deleteAllRWTLaunchConfigs();
  }

  @Test
  public void testValidate_whenServletPathEmptyAndOpenBrowserRequested() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setServletPath( "" );

    validator.validate();

    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  @Test
  public void testValidate_whenServletPathEmptyAndNoBrowserRequested() {
    launchConfig.setOpenBrowser( false );
    launchConfig.setServletPath( "" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  @Test
  public void testValidate_whenServletPathMissingLeadingSlashAndOpenBrowserRequested() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setServletPath( "foo" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  @Test
  public void testValidate_whenServletPathMissingLeadingSlashAndNoBrowserRequested() {
    launchConfig.setOpenBrowser( false );
    launchConfig.setServletPath( "foo" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  @Test
  public void testValidate_whenServletPathSpecifiedAndOpenBrowserRequested() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setServletPath( "/foo" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  @Test
  public void testValidate_whenServletPathContainsAsterisk() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setServletPath( "/foo*" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  @Test
  public void testValidate_whenServletPathContainsSlash() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setServletPath( "/foo/bar" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

}
