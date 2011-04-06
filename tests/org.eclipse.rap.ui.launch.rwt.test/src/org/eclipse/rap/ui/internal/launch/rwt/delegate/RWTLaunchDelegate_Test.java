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
package org.eclipse.rap.ui.internal.launch.rwt.delegate;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestLaunch;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class RWTLaunchDelegate_Test extends TestCase {
  
  private ILaunchConfigurationWorkingCopy launchConfig;
  private RWTLaunchConfig rwtLaunchConfig;
  private RWTLaunchDelegate launchDelegate;

  public void testGetProgramArguments() {
    String programArgs = "programArgument";
    setProgramArguments( programArgs );
    
    String returnedProgramArgs = launchDelegate.getProgramArguments( launchConfig );
    
    assertTrue( returnedProgramArgs.indexOf( "programArgument" ) == -1 );
  }
  
  public void testGetMainTypeName() {
    String mainTypeName = launchDelegate.getMainTypeName( launchConfig );
    assertEquals( "org.mortbay.jetty.Main", mainTypeName );
  }
  
  public void testGetVMArguments() throws CoreException {
    String vmArguments = launchDelegate.getVMArguments( launchConfig );
    assertTrue( vmArguments.indexOf( "-Djetty.home=" ) >= 0 );
  }

  public void testGetVMArgumentsWithUserDefinedArgument() throws CoreException {
    String vmArgument = "-Dfoo=bar";
    setVMArgument( vmArgument );
    
    String vmArguments = launchDelegate.getVMArguments( launchConfig );
    
    assertTrue( vmArguments.indexOf( vmArgument ) >= 0 );
  }
  
  public void testGetJavaProject() throws CoreException {
    TestProject testProject = new TestProject();
    IJavaProject javaProject = testProject.getJavaProject();
    rwtLaunchConfig.setProjectName( javaProject.getElementName() );
    
    IJavaProject returnedJavaProject = launchDelegate.getJavaProject( launchConfig );
    
    assertEquals( javaProject, returnedJavaProject );
  }
  
  public void testGetClasspath() throws CoreException {
    String[] classpath = launchDelegate.getClasspath( launchConfig );
    assertEquals( 3, classpath.length );
  }
  
  public void testDeterminePortWhenManualPortConfigured() {
    int configuredPort = 1234;
    rwtLaunchConfig.setUseManualPort( true );
    rwtLaunchConfig.setPort( configuredPort );
    
    int port = launchDelegate.determinePort();
    
    assertEquals( configuredPort, port ); 
  }

  public void testDeterminePortWhenAutomaticPortConfigured() throws IOException {
    rwtLaunchConfig.setUseManualPort( false );
    
    int port = launchDelegate.determinePort();
    
    assertTrue( isPortFree( port ) );
  }
  
  protected void setUp() throws Exception {
    launchConfig = Fixture.createRWTLaunchConfig();
    rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    launchDelegate = new RWTLaunchDelegate();
    launchDelegate.initializeLaunch( new TestLaunch( launchConfig ) );
  }

  protected void tearDown() throws Exception {
    TestProject.deleteAll();
    launchConfig.delete();
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
