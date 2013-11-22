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

import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WebXmlLocationValidator_Test {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  @Before
  public void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new WebXmlLocationValidator( launchConfig, validationResult );
  }

  @After
  public void tearDown() throws Exception {
    launchConfig.getUnderlyingLaunchConfig().delete();
    TestProject.deleteAll();
  }

  @Test
  public void testValidate_withEmptyWebXmlLocation() {
    launchConfig.setLaunchTarget( LaunchTarget.WEB_XML );
    launchConfig.setWebXmlLocation( "" );

    validator.validate();

    assertTrue( validationResult.contains( WebXmlLocationValidator.ERR_WEB_XML_LOCATION_EMPTY ) );
  }

  @Test
  public void testValidate_withNonExistentWebXmlLocation() {
    launchConfig.setLaunchTarget( LaunchTarget.WEB_XML );
    launchConfig.setWebXmlLocation( "does/not/exist/web.xml" );

    validator.validate();

    assertFalse( validationResult.contains( WebXmlLocationValidator.ERR_WEB_XML_LOCATION_EMPTY ) );
    assertTrue( validationResult.contains( WebXmlLocationValidator.ERR_WEB_XML_LOCATION_NOT_FOUND ) );
  }

  @Test
  public void testValidate_withUnusedWebXml() {
    launchConfig.setLaunchTarget( LaunchTarget.ENTRY_POINT );
    launchConfig.setWebXmlLocation( "does.not.exist" );

    validator.validate();

    assertFalse( validationResult.contains( WebXmlLocationValidator.ERR_WEB_XML_LOCATION_EMPTY ) );
    assertFalse( validationResult.contains( WebXmlLocationValidator.ERR_WEB_XML_LOCATION_NOT_FOUND ) );
  }

}
