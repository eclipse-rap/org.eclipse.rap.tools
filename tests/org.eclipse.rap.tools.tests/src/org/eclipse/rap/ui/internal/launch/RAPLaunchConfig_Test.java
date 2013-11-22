/*******************************************************************************
 * Copyright (c) 2007, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.ui.tests.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RAPLaunchConfig_Test {

  private ILaunchConfigurationWorkingCopy config;
  private RAPLaunchConfig rapConfig;

  @Before
  public void setUp() throws Exception {
    config = Fixture.createRAPLaunchConfig();
    rapConfig = new RAPLaunchConfig( config );
  }

  @After
  public void tearDown() throws Exception {
    config.delete();
  }

  @Test
  public void testInitialValues() throws CoreException {
    assertFalse( rapConfig.getUseManualPort() );
    assertFalse( "".equals( rapConfig.getServletPath() ) );
    assertTrue( rapConfig.getDevelopmentMode() );
    assertEquals( 0, rapConfig.getSessionTimeout() );
    assertFalse( rapConfig.getUseSessionTimeout() );
    assertFalse( rapConfig.getUseManualContextPath() );
    assertTrue( "/".equals( rapConfig.getContextPath() ) );
  }

  @Test
  public void testServletPath() throws CoreException {
    rapConfig.setServletPath( "xyz" );

    assertEquals( "xyz", rapConfig.getServletPath() );
  }

  @Test
  public void testServletPath_failsWithNull() throws CoreException {
    rapConfig.setServletPath( "xyz" );
    try {

      rapConfig.setServletPath( null );

      fail();
    } catch( NullPointerException e ) {
      assertEquals( "xyz", rapConfig.getServletPath() );
    }
  }

  @Test
  public void testSetDataLocation() throws CoreException {
    rapConfig.setDataLocation( "xyz" );
    assertEquals( "xyz", rapConfig.getDataLocation() );
  }

  @Test
  public void testSetDataLocation_failsWithNull() throws CoreException {
    rapConfig.setDataLocation( "xyz" );
    try {

      rapConfig.setDataLocation( null );

      fail();
    } catch( NullPointerException e ) {
      assertEquals( "xyz", rapConfig.getDataLocation() );
    }
  }

  @Test
  public void testSetContextPath_withValidPath() throws CoreException {
    rapConfig.setContextPath( "/xyz" );

    assertEquals( "/xyz", rapConfig.getContextPath() );
  }

  @Test( expected = NullPointerException.class )
  public void testSetContextPath_failsWithNull() {
    rapConfig.setContextPath( null );
  }

  @Test
  public void testSetUseDefaultDataLocation_false() throws CoreException {
    rapConfig.setUseDefaultDataLocation( false );

    assertFalse( rapConfig.getUseDefaultDatatLocation() );
  }

  @Test
  public void testSetUseDefaultDataLocation_true() throws CoreException {
    rapConfig.setUseDefaultDataLocation( true );

    assertTrue( rapConfig.getUseDefaultDatatLocation() );
  }

  @Test
  public void testDoClear() throws CoreException {
    rapConfig.setDoClearDataLocation( false );
    assertFalse( rapConfig.getDoClearDataLocation() );
    rapConfig.setDoClearDataLocation( true );
    assertTrue( rapConfig.getDoClearDataLocation() );
  }

}