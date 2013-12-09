/*******************************************************************************
 * Copyright (c) 2007, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.rap.tools.internal.tests.Fixture;
import org.eclipse.rap.tools.internal.tests.TargetUtil;
import org.eclipse.rap.tools.launch.internal.RAPLaunchConfig;
import org.eclipse.rap.tools.launch.internal.RAPLaunchDelegate;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class RAPLaunchDelegate_Test {

  private ILaunchConfigurationWorkingCopy config;
  private RAPLaunchConfig rapConfig;
  private RAPLaunchDelegate launchDelegate;

  @BeforeClass
  public static void setUpTarget() throws CoreException {
    TargetUtil.initializeTargetPlatform();
  }

  @Before
  public void setUp() throws CoreException {
    config = Fixture.createRAPLaunchConfig();
    rapConfig = new RAPLaunchConfig( config );
    launchDelegate = new RAPLaunchDelegate();
  }

  @After
  public void tearDown() throws Exception {
    config.delete();
  }

  /*
   * Make sure that user VM arguments are 'overridden' by those added by the
   * launch delegate.
   * Overriding currently is done by ensuring that the VM arguments added by
   * the launcher come *after* the user VM arguments.
   */
  @Test
  public void testGetVMArguments() throws CoreException {
    // prepare launch configuration
    config.setAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
                         "-Dorg.osgi.service.http.port=manually" );
    rapConfig.setUseManualPort( true );
    rapConfig.setPort( 1234 );
    applyConfig( config, launchDelegate );

    String[] arguments = launchDelegate.getVMArguments( config );

    int manualPortIndex = indexOf( arguments, "-Dorg.osgi.service.http.port=manually" );
    int autoPortIndex = indexOf( arguments, "-Dorg.osgi.service.http.port=1234" );
    assertTrue( manualPortIndex > -1 );
    assertTrue( autoPortIndex > -1 );
    assertTrue( autoPortIndex > manualPortIndex );
  }

  @Test
  public void testGetVMArguments_developmentMode() throws CoreException {
    rapConfig.setDevelopmentMode( false );
    applyConfig( config, launchDelegate );

    String[] arguments = launchDelegate.getVMArguments( config );

    assertTrue( indexOf( arguments, "-Dorg.eclipse.rap.rwt.developmentMode=false" ) > -1 );
  }

  /*
   * Make sure that user program arguments contain -data
   */
  @Test
  public void testGetProgramArguments_defaultLocation() throws CoreException {
    applyConfig( config, launchDelegate );

    String[] arguments = launchDelegate.getProgramArguments( config );

    int dataIndex = indexOf( arguments, "-data" );
    assertTrue( dataIndex > -1 );
    String defaultDataLocation = RAPLaunchConfig.getDefaultDataLocation( config.getName() );
    String defaultDataLocationResolved = resolveVariable( defaultDataLocation );
    assertEquals( defaultDataLocationResolved, arguments[ dataIndex + 1 ] );
  }

  @Test
  public void testGetProgramArguments() throws CoreException {
    rapConfig.setDataLocation( "test" );
    applyConfig( config, launchDelegate );

    String[] arguments = launchDelegate.getProgramArguments( config );

    int dataIndex = indexOf( arguments, "-data" );
    assertTrue( dataIndex > -1 );
    assertEquals( "test", arguments[ dataIndex + 1 ] );
  }

  @Test
  public void testGetProgramArguments_emptyDataLocation() throws CoreException {
    rapConfig.setDataLocation( "" );
    applyConfig( config, launchDelegate );

    String[] arguments = launchDelegate.getProgramArguments( config );

    int dataIndex = indexOf( arguments, "-data" );
    assertTrue( dataIndex == -1 );
  }

  @Test
  public void testClear_removesDataLocationWhenDoClearIsTrue() throws CoreException, IOException {
    File dataLocation = createDataLocation();
    RAPLaunchConfig.setDefaults( config );
    rapConfig.setDataLocation( dataLocation.getAbsolutePath() );
    rapConfig.setDoClearDataLocation( true );
    applyConfig( config, launchDelegate );

    launchDelegate.clear( config, new NullProgressMonitor() );

    assertFalse( dataLocation.exists() );
  }

  @Test
  public void testClear_retainsDataLocationWhenDoClearIsFalse() throws CoreException, IOException {
    File dataLocation = createDataLocation();
    RAPLaunchConfig.setDefaults( config );
    rapConfig.setDataLocation( dataLocation.getAbsolutePath() );
    rapConfig.setDoClearDataLocation( false );
    applyConfig( config, launchDelegate );

    launchDelegate.clear( config, new NullProgressMonitor() );

    assertTrue( dataLocation.exists() );
  }

  /*
   * Make sure that the user specified session timeout value is used, when the
   * "use session timeout" checkbox is selected.
   */
  @Test
  public void testGetVMArguments_containsCustomSessionTimeout() throws CoreException {
    rapConfig.setUseSessionTimeout( true );
    rapConfig.setSessionTimeout( 100 );
    applyConfig( config, launchDelegate );

    String[] arguments = launchDelegate.getVMArguments( config );

    String expected = "-Dorg.eclipse.equinox.http.jetty.context.sessioninactiveinterval=100";
    int timeoutIndex = indexOf( arguments, expected );
    assertTrue( timeoutIndex > -1 );
  }

  /*
   * Make sure that the default session timeout value (zero) is used, when the
   * "use session timeout" checkbox is NOT selected.
   */
  @Test
  public void testGetVMArguments_containsDefaultSessionTimeout() throws CoreException {
    rapConfig.setUseSessionTimeout( false );
    rapConfig.setSessionTimeout( 100 );
    applyConfig( config, launchDelegate );

    String[] arguments = launchDelegate.getVMArguments( config );

    String expected = "-Dorg.eclipse.equinox.http.jetty.context.sessioninactiveinterval=0";
    int timeoutIndex = indexOf( arguments, expected );
    assertTrue( timeoutIndex > -1 );
  }

  private static void applyConfig( ILaunchConfigurationWorkingCopy config,
                                   RAPLaunchDelegate launchDelegate )
  {
    try {
      launchDelegate.launch( config, null, null, new NullProgressMonitor() );
    } catch( Throwable thr ) {
      // ignore any exceptions, the only purpose of the above call is to
      // set the 'config' field of the RAPLaunchDelegate
    }
  }

  private static String resolveVariable( String expression ) throws CoreException {
    VariablesPlugin variablePlugin = VariablesPlugin.getDefault();
    IStringVariableManager stringVariableManager = variablePlugin.getStringVariableManager();
    return stringVariableManager.performStringSubstitution( expression );
  }

  private static File createDataLocation() throws IOException {
    File dataLocation = File.createTempFile( "dataLocation", "" );
    dataLocation.delete();
    dataLocation.mkdirs();
    IPath path = new Path( dataLocation.getAbsolutePath() ).append( "file" );
    path.toFile().createNewFile();
    return dataLocation;
  }

  private static int indexOf( Object[] array, Object object ) {
    for( int i = 0; i < array.length; i++ ) {
      if( object.equals( array[ i ] ) ) {
        return i;
      }
    }
    return -1;
  }

}
