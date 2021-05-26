/*******************************************************************************
 * Copyright (c) 2011, 2020 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.delegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestLaunch;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RWTLaunchDelegate_Test {

  private ILaunchConfigurationWorkingCopy launchConfig;
  private RWTLaunchConfig rwtLaunchConfig;
  private RWTLaunchDelegate launchDelegate;

  @Before
  public void setUp() throws Exception {
    launchConfig = Fixture.createRWTLaunchConfig();
    rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    launchDelegate = new RWTLaunchDelegate();
    launchDelegate.initializeLaunch( new TestLaunch( launchConfig ) );
  }

  @After
  public void tearDown() throws Exception {
    TestProject.deleteAll();
    launchConfig.delete();
  }

  @Test
  public void testGetProgramArguments() {
    setProgramArguments( "program-argument" );

    String programArgs = launchDelegate.getProgramArguments( launchConfig );

    assertFalse( programArgs.contains( "program-argument" ) );
  }

  @Test
  public void testGetMainTypeName() {
    String mainTypeName = launchDelegate.getMainTypeName( launchConfig );

    ClassLoader loader = RWTLaunchDelegate.class.getClassLoader();
    try {
      Class jettyLauncher = loader.loadClass( mainTypeName );
      jettyLauncher.getDeclaredMethod( "main", String[].class );
    } catch( ClassNotFoundException exception ) {
      fail( "Unable to load Jetty launcher class: " + mainTypeName );
    } catch( NoSuchMethodException exception ) {
      fail( "Unable to find main method in Jetty launcher class: " + mainTypeName );
    } catch( SecurityException shouldNotHappen ) {
    }
  }

  @Test
  public void testGetVMArguments() throws CoreException {
    String vmArguments = launchDelegate.getVMArguments( launchConfig );

    assertTrue( vmArguments.contains( "-Djetty.home=" ) );
    assertTrue( vmArguments.contains( "-Dorg.eclipse.rap.rwt.developmentMode=" ) );
  }

  @Test
  public void testGetVMArgumentsWithUserDefinedArgument() throws CoreException {
    String vmArgument = "-Dfoo=bar";
    setVMArgument( vmArgument );

    String vmArguments = launchDelegate.getVMArguments( launchConfig );

    assertTrue( vmArguments.indexOf( vmArgument ) >= 0 );
  }

  @Test
  public void testGetJavaProject() throws CoreException {
    TestProject testProject = new TestProject();
    IJavaProject javaProject = testProject.getJavaProject();
    rwtLaunchConfig.setProjectName( javaProject.getElementName() );

    IJavaProject returnedJavaProject = launchDelegate.getJavaProject( launchConfig );

    assertEquals( javaProject, returnedJavaProject );
  }

  @Test
  public void testGetClasspath() throws CoreException {
    String[] classpath = launchDelegate.getClasspath( launchConfig );
    String[] bundles = {
      "org.eclipse.rap.tools.launch.rwt",
      "org.eclipse.jetty.http",
      "org.eclipse.jetty.io",
      "org.eclipse.jetty.security",
      "org.eclipse.jetty.server",
      "org.eclipse.jetty.servlet",
      "org.eclipse.jetty.util",
      "org.eclipse.jetty.util.ajax",
      "org.eclipse.jetty.webapp",
      "org.eclipse.jetty.xml",
      "slf4j.api",
    };
    for( String bundle : bundles ) {
      boolean found = false;
      for( String path : classpath ) {
        if( path != null && path.indexOf( bundle ) != -1 ) {
          found = true;
        }
      }
      assertTrue( "Bundle " + bundle + " not found in classpath." , found );
    }
  }

  @Test
  public void testDeterminePortWhenManualPortConfigured() {
    int configuredPort = 1234;
    rwtLaunchConfig.setUseManualPort( true );
    rwtLaunchConfig.setPort( configuredPort );

    int port = launchDelegate.determinePort();

    assertEquals( configuredPort, port );
  }

  @Test
  public void testDeterminePortWhenAutomaticPortConfigured() throws IOException {
    rwtLaunchConfig.setUseManualPort( false );

    int port = launchDelegate.determinePort();

    assertTrue( isPortFree( port ) );
  }

  private void setVMArgument( String vmArgument ) {
    String attrVmArgs = IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;
    launchConfig.setAttribute( attrVmArgs, vmArgument );
  }

  private void setProgramArguments( String value ) {
    String attrProgramArgs = IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS;
    launchConfig.setAttribute( attrProgramArgs, value );
  }

  private static boolean isPortFree( int port ) throws IOException {
    boolean result = false;
    try {
      Socket socket = new Socket( "127.0.0.1", port );
      socket.close();
    } catch( ConnectException expected ) {
      result = true;
    }
    return result;
  }

}
