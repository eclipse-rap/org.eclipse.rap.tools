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

import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;

import junit.framework.TestCase;


public class WebXmlLocationValidator_Test extends TestCase {
  
  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  public void testValidateWebXmlLocationWhenEmpty() {
    launchConfig.setUseWebXml( true );
    launchConfig.setWebXmlLocation( "" );
    
    validator.validate();
    
    assertTrue( validationResult.contains( WebXmlLocationValidator.ERR_WEB_XML_LOCATION_EMPTY ) );
  }
  
  public void testValidateWebXmlLocationWhenNotFound() {
    launchConfig.setUseWebXml( true );
    launchConfig.setWebXmlLocation( "does/not/exist/web.xml" );
    
    validator.validate();
    
    assertFalse( validationResult.contains( WebXmlLocationValidator.ERR_WEB_XML_LOCATION_EMPTY ) );
    assertTrue( validationResult.contains( WebXmlLocationValidator.ERR_WEB_XML_LOCATION_NOT_FOUND ) );
  }
  
  public void testValidateWebXmlLocationWhenUnused() {
    launchConfig.setUseWebXml( false );
    launchConfig.setWebXmlLocation( "does.not.exist" );
    
    validator.validate();
    
    assertFalse( validationResult.contains( WebXmlLocationValidator.ERR_WEB_XML_LOCATION_EMPTY ) );
    assertFalse( validationResult.contains( WebXmlLocationValidator.ERR_WEB_XML_LOCATION_NOT_FOUND ) );
  }
  
  protected void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new WebXmlLocationValidator( launchConfig, validationResult );
  }
  
  protected void tearDown() throws Exception {
    launchConfig.getUnderlyingLaunchConfig().delete();
    TestProject.deleteAll();
  }
}
