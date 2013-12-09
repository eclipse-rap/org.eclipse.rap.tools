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
package org.eclipse.rap.tools.launch.rwt.internal.tab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.tab.ArgumentsTab;
import org.eclipse.rap.tools.launch.rwt.internal.tab.WorkingDirectorySection;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ArgumentsTab_Test {

  private ArgumentsTab argumentsTab;
  private RWTLaunchConfig launchConfig;

  @Before
  public void setUp() throws Exception {
    launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    argumentsTab = new ArgumentsTab();
  }

  @After
  public void tearDown() throws Exception {
    argumentsTab.dispose();
    launchConfig.getUnderlyingLaunchConfig().delete();
  }

  @Test
  public void testCreateControl_callsSetControl() {
    argumentsTab.createControl( new Shell() );

    assertNotNull( argumentsTab.getControl() );
  }

  @Test
  public void testGetImage() {
    assertNotNull( argumentsTab.getImage() );
  }

  @Test
  public void testGetName() {
    String name = argumentsTab.getName();

    assertNotNull( name );
    assertTrue( name.length() > 0 );
  }

  @Test
  public void testIsValid_withInvalidWorkingDirectory() {
    TestableWorkingDirectorySection workingDirectorySection = setupWorkingDirectorySection( false );

    boolean valid = argumentsTab.isValid( launchConfig.getUnderlyingLaunchConfig() );

    assertTrue( workingDirectorySection.isValidWasCalled );
    assertFalse( valid );
  }

  @Test
  public void testIsValid_withValidWorkingDirectory() {
    TestableWorkingDirectorySection workingDirectorySection = setupWorkingDirectorySection( true );

    boolean valid = argumentsTab.isValid( launchConfig.getUnderlyingLaunchConfig() );

    assertTrue( workingDirectorySection.isValidWasCalled );
    assertTrue( valid );
  }

  @Test
  public void testGetErrorMessage() {
    String errorMessage = argumentsTab.getErrorMessage();

    assertNull( errorMessage );
  }

  @Test
  public void testGetErrorMessage_withErrorInWorkingDirectorySection() {
    TestableWorkingDirectorySection workingDirectorySection = setupWorkingDirectorySection( true );
    workingDirectorySection.setErrorMessage( "errorMessage" );

    String errorMessage = argumentsTab.getErrorMessage();

    assertEquals( "errorMessage", errorMessage );
  }

  @Test
  public void testGetMessage() {
    String message = argumentsTab.getMessage();

    assertNull( message );
  }

  @Test
  public void testGetMessage_withInfoInWrkingDirectorySection() {
    TestableWorkingDirectorySection workingDirectorySection = setupWorkingDirectorySection( true );
    workingDirectorySection.setMessage( "message" );

    String message = argumentsTab.getMessage();

    assertEquals( "message", message );
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

    @Override
    public boolean isValid( ILaunchConfiguration config ) {
      isValidWasCalled = true;
      return valid;
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
      super.setErrorMessage( errorMessage );
    }

    @Override
    public void setMessage( String message ) {
      super.setMessage( message );
    }
  }

}
