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
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import junit.framework.TestCase;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.swt.widgets.Shell;


public class ArgumentsTab_Test extends TestCase {
  
  private ArgumentsTab argumentsTab;
  private RWTLaunchConfig launchConfig;

  public void testCreateControlCallsSetControl() {
    argumentsTab.createControl( new Shell() );
    assertNotNull( argumentsTab.getControl() );
  }

  public void testGetImage() {
    assertNotNull( argumentsTab.getImage() );
  }

  public void testGetName() {
    String name = argumentsTab.getName();
    
    assertNotNull( name );
    assertTrue( name.length() > 0 );
  }
  
  public void testIsValidWithInvalidWorkingDirectory() {
    TestableWorkingDirectorySection workingDirectorySection = setupWorkingDirectorySection( false );
    
    boolean valid = argumentsTab.isValid( launchConfig.getUnderlyingLaunchConfig() );
    
    assertTrue( workingDirectorySection.isValidWasCalled );
    assertFalse( valid );
  }

  public void testIsValidWithValidWorkingDirectory() {
    TestableWorkingDirectorySection workingDirectorySection = setupWorkingDirectorySection( true );
    
    boolean valid = argumentsTab.isValid( launchConfig.getUnderlyingLaunchConfig() );
    
    assertTrue( workingDirectorySection.isValidWasCalled );
    assertTrue( valid );
  }
  
  public void testGetErrorMessage() {
    String returnedErrorMessage = argumentsTab.getErrorMessage();
    
    assertNull( returnedErrorMessage );
  }

  public void testGetErrorMessageWithErrorInWorkingDirectorySection() {
    String errorMessage = "errorMessage";
    TestableWorkingDirectorySection workingDirectorySection = setupWorkingDirectorySection( true );
    workingDirectorySection.setErrorMessage( errorMessage );
    
    String returnedErrorMessage = argumentsTab.getErrorMessage();
    
    assertEquals( returnedErrorMessage, errorMessage );
  }
  
  public void testGetMessage() {
    String returnedMessage = argumentsTab.getMessage();
    
    assertNull( returnedMessage );
  }

  public void testGetMessageWithInfoInWrkingDirectorySection() {
    String message = "message";
    TestableWorkingDirectorySection workingDirectorySection = setupWorkingDirectorySection( true );
    workingDirectorySection.setMessage( message );
    
    String returnedMessage = argumentsTab.getMessage();
    
    assertEquals( returnedMessage, message );
  }
  
  protected void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    argumentsTab = new ArgumentsTab();
  }
  
  protected void tearDown() throws Exception {
    argumentsTab.dispose();
    launchConfig.getUnderlyingLaunchConfig().delete();
  }

  private TestableWorkingDirectorySection setupWorkingDirectorySection( boolean valid ) {
    TestableWorkingDirectorySection result = new TestableWorkingDirectorySection( valid );
    argumentsTab.workingDirectorySection = result;
    return result;
  }

  private static class TestableWorkingDirectorySection extends WorkingDirectorySection {
    boolean isValidWasCalled;
    private final boolean valid;
    
    TestableWorkingDirectorySection( boolean valid ) {
      this.valid = valid;
    }
    
    public boolean isValid( ILaunchConfiguration config ) {
      isValidWasCalled = true;
      return valid;
    }
    
    public void setErrorMessage( String errorMessage ) {
      super.setErrorMessage( errorMessage );
    }
    
    public void setMessage( String message ) {
      super.setMessage( message );
    }
  }
}
