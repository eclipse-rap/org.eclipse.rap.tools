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
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class EntryPointValidator_Test extends TestCase {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  public void testValidateWhenEmpty() {
    launchConfig.setUseWebXml( false );
    launchConfig.setEntryPoint( "" );
    
    validator.validate();
    
    assertTrue( validationResult.contains( EntryPointValidator.ERR_ENTRY_POINT_EMPTY ) );
  }
  
  public void testValidateWhenUnused() {
    launchConfig.setUseWebXml( true );
    launchConfig.setEntryPoint( "" );
    
    validator.validate();
    
    assertFalse( validationResult.contains( EntryPointValidator.ERR_ENTRY_POINT_EMPTY ) );
  }
  
  protected void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new EntryPointValidator( launchConfig, validationResult );
  }
  
  protected void tearDown() throws Exception {
    launchConfig.getUnderlyingLaunchConfig().delete();
    TestProject.deleteAll();
  }
}
