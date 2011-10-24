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

import org.eclipse.core.resources.IFolder;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class WebAppLocationValidator_Test extends TestCase {

  private RWTLaunchConfig launchConfig;
  private Validator validator;
  private ValidationResult validationResult;

  public void testValidateWebAppLocationWhenRunningFromEntryPoint() {
    launchConfig.setLaunchTarget( LaunchTarget.ENTRY_POINT );
    launchConfig.setWebAppLocation( "does/not/exist" );
    
    validator.validate();
    
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_EMPTY );
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_NOT_FOUND );
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_INVALID );
  }

  public void testValidateWebAppLocationWhenEmpty() {
    launchConfig.setLaunchTarget( LaunchTarget.WEB_APP_FOLDER );
    launchConfig.setWebAppLocation( "" );
    
    validator.validate();
    
    assertContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_EMPTY );
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_NOT_FOUND );
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_INVALID );
  }

  public void testValidateWebAppLocationWhenNotExisting() {
    launchConfig.setLaunchTarget( LaunchTarget.WEB_APP_FOLDER );
    launchConfig.setWebAppLocation( "foo/bar" );
    
    validator.validate();

    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_EMPTY );
    assertContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_NOT_FOUND );
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_INVALID );
  }
  
  public void testValidateWebAppLocationWhenExistingAndInvalid() throws Exception {
    TestProject project = new TestProject();
    IFolder folder = project.createFolder( "web-app" );
    launchConfig.setLaunchTarget( LaunchTarget.WEB_APP_FOLDER );
    launchConfig.setWebAppLocation( folder.getFullPath().toPortableString() );
    
    validator.validate();
    
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_EMPTY );
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_NOT_FOUND );
    assertContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_INVALID );
  }
  
  public void testValidateWebAppLocationWhenExistingAndValid() throws Exception {
    TestProject project = new TestProject();
    IFolder folder = project.createFolder( "WEB-INF" );
    project.createFile( folder, "web.xml", "<web-app />" );
    launchConfig.setLaunchTarget( LaunchTarget.WEB_APP_FOLDER );
    launchConfig.setWebAppLocation( project.getProject().getFullPath().toPortableString() );
    
    validator.validate();
    
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_EMPTY );
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_NOT_FOUND );
    assertNotContains( WebAppLocationValidator.ERR_WEB_APP_LOCATION_INVALID );
  }
  
  protected void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    validationResult = new ValidationResult();
    validator = new WebAppLocationValidator( launchConfig, validationResult );
  }
  
  protected void tearDown() throws Exception {
    launchConfig.getUnderlyingLaunchConfig().delete();
    TestProject.deleteAll();
  }

  private void assertContains( int code ) {
    assertTrue( validationResult.contains( code ) );
  }

  private void assertNotContains( int code ) {
    assertFalse( validationResult.contains( code ) );
  }
}
