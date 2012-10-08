/*******************************************************************************
 * Copyright (c) 2007, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    IBM Corporation - original code for feature project creation, from PDE test cases
 *    Cole Markham - feature-based validation testing
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.rap.ui.tests.Fixture;
import org.eclipse.rap.ui.tests.TestPluginProject;


public class RAPLaunchConfigValidator_Test extends TestCase {

  private static final String FEATURE_PROJECT_NAME = "com.junitTest.feature";

  private ILaunchConfigurationWorkingCopy config;
  private RAPLaunchConfig rapConfig;
  private List projectsToDelete;
  private RAPLaunchConfigValidator validator;

  protected void setUp() throws Exception {
    config = Fixture.createRAPLaunchConfig();
    rapConfig = new RAPLaunchConfig( config );
    validator = new RAPLaunchConfigValidator( rapConfig );
    projectsToDelete = new ArrayList();
  }

  protected void tearDown() throws Exception {
    config.delete();
    deleteProjects( projectsToDelete );
    deleteFeature();
  }

  public void testValidateDataLocation() {
    rapConfig.setDataLocation( "" );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_DATA_LOCATION ) );
  }

  public void testValidateServletPath() throws Exception {
    rapConfig.setServletPath( "/foo" );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  public void testValidateServletPathEmpty() {
    // servlet name has a default value, clear this
    rapConfig.setServletPath( "" );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_SERVLET_PATH ) );
  }

  public void testValidateServletPathLeadingSlash() {
    rapConfig.setServletPath( "rap" );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.WARN_SERVLET_PATH ) );
  }

  public void testValidateWsRAP() throws Exception {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    config.setAttribute( IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "-ws rap" );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  public void testValidateWsEmpty() throws Exception {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    config.setAttribute( IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "" );

    IStatus[] states = validator.validate();

    // TODO [rst] Change to assertTrue when bug 338544 is fixed
    // assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.WARN_WS_WRONG ) );
    assertTrue( isOk( states ) );
  }

  public void testValidateWsNotRAP() throws Exception {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    config.setAttribute( IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "-ws test" );

    IStatus[] states = validator.validate();

    // TODO [rst] Change to assertTrue when bug 338544 is fixed
    // assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.WARN_WS_WRONG ) );
    assertTrue( isOk( states ) );
  }

  public void testInvalidButDisabledContextPath() {
    rapConfig.setContextPath( "contains space" );
    rapConfig.setUseManualContextPath( false );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  public void testContextPathStartSlash() {
    rapConfig.setContextPath( "/ok" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  public void testContextPathEndSlash() {
    rapConfig.setContextPath( "/foo/" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  public void testInvalidContextPath() {
    rapConfig.setContextPath( "invalid\\character" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_CONTEXT_PATH ) );
  }

  public void testContextPathWithSpaces() {
    rapConfig.setContextPath( "contains space" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_CONTEXT_PATH ) );
  }

  public void testContextPathWithDoubleSlash() {
    rapConfig.setContextPath( "double//slash" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_CONTEXT_PATH ) );
  }

  public void testInvalidPort() {
    rapConfig.setUseManualPort( true );
    rapConfig.setPort( -1 );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_PORT ) );
  }

  public void testPortRetry() {
    rapConfig.setUseManualPort( true );
    rapConfig.setPort( -1 );
    // Fix validation error and retry
    rapConfig.setPort( 8080 );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  public void testInvalidTimeout() {
    rapConfig.setUseSessionTimeout( true );
    rapConfig.setSessionTimeout( -1 );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_TIMEOUT ) );
  }

  public void testZeroTimeout() {
    rapConfig.setUseSessionTimeout( true );
    rapConfig.setSessionTimeout( 0 );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  public void testPositiveTimeout() {
    rapConfig.setUseSessionTimeout( true );
    rapConfig.setSessionTimeout( Integer.MAX_VALUE );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  public void testUrl() {
    rapConfig.setEntryPoint( "entryPoint" );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  private static boolean hasStatusCode( IStatus[] states, int code ) {
    boolean result = false;
    for( int i = 0; !result && i < states.length; i++ ) {
      if( states[ i ].getCode() == code ) {
        result = true;
      }
    }
    return result;
  }

  private boolean isOk( IStatus[] states ) {
    boolean result = true;
    for( int i = 0; !result && i < states.length; i++ ) {
      if( !states[ i ].isOK() ) {
        result = false;
      }
    }
    return result;
  }

  private static void deleteFeature() throws CoreException {
    IProject featureProject = getProject( FEATURE_PROJECT_NAME );
    if( featureProject != null ) {
      featureProject.delete( true, true, new NullProgressMonitor() );
    }
  }

  private static IProject getProject( String projectName ) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return root.getProject( projectName );
  }

  private static void deleteProjects( List projects ) throws CoreException {
    Iterator iter = projects.iterator();
    while( iter.hasNext() ) {
      TestPluginProject project = ( TestPluginProject )iter.next();
      project.delete();
    }
  }
}
