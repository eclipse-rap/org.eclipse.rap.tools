/*******************************************************************************
 * Copyright (c) 2011, 2012 Rüdiger Herrmann and others.
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

import junit.framework.TestCase;

import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;


public class ServletPathValidator_Test extends TestCase {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  public void testValidateServletPathWhenEmptyAndOpenBrowserRequested() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setServletPath( "" );

    validator.validate();

    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  public void testValidateServletPathWhenEmptyAndNoBrowserRequested() {
    launchConfig.setOpenBrowser( false );
    launchConfig.setServletPath( "" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  public void testValidateServletPathWhenMissingLeadingSlashAndOpenBrowserRequested() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setServletPath( "foo" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  public void testValidateServletPathWhenMissingLeadingSlashAndNoBrowserRequested() {
    launchConfig.setOpenBrowser( false );
    launchConfig.setServletPath( "foo" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  public void testValidateServletPathWhenSpecifiedAndOpenBrowserRequested() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setServletPath( "/foo" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  public void testValidateServletPathWithAsterisk() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setServletPath( "/foo*" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  public void testValidateServletPathWithSlash() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setServletPath( "/foo/bar" );

    validator.validate();

    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  protected void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new ServletPathValidator( launchConfig, validationResult );
  }

  protected void tearDown() throws Exception {
    Fixture.deleteAllRWTLaunchConfigs();
  }
}
