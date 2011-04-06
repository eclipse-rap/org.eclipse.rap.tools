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

import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;


public class ServletPathValidator_Test extends TestCase {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  public void testValidateServletPathWithNonWebXmlConfiuration() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setUseWebXml( false );
    launchConfig.setServletPath( "" );
    
    validator.validate();
    
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
  }
  
  public void testValidateServletPathWhenNoBrowserRequested() {
    launchConfig.setOpenBrowser( false );
    launchConfig.setUseWebXml( true );
    launchConfig.setServletPath( "" );
    
    validator.validate();
    
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
  }
  
  public void testValidateServletPathWhenUnnecessarilySpecified() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setUseWebXml( true );
    launchConfig.setServletPath( "foo" );
    
    validator.validate();
    
    assertFalse( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
  }
  
  public void testValidateServletPathWhenEmptyAndBrowserAndWebXmlSpecified() {
    launchConfig.setOpenBrowser( true );
    launchConfig.setUseWebXml( true );
    launchConfig.setServletPath( "" );
    
    validator.validate();
    
    assertTrue( validationResult.contains( ServletPathValidator.ERR_SERVLET_PATH_EMPTY ) );
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
