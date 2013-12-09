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
 *    IBM Corporation - original code for feature project creation, from PDE test cases
 *    Cole Markham - feature-based validation testing
 ******************************************************************************/
package org.eclipse.rap.tools.launch.internal;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.tools.internal.tests.Fixture;
import org.eclipse.rap.tools.internal.tests.TestPluginProject;
import org.eclipse.rap.tools.launch.internal.RAPLaunchConfig;
import org.eclipse.rap.tools.launch.internal.RAPLaunchConfigValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RAPLaunchConfigValidator_Test {

  private static final String FEATURE_PROJECT_NAME = "com.junitTest.feature";

  private ILaunchConfigurationWorkingCopy config;
  private RAPLaunchConfig rapConfig;
  private List projectsToDelete;
  private RAPLaunchConfigValidator validator;

  @Before
  public void setUp() throws Exception {
    config = Fixture.createRAPLaunchConfig();
    rapConfig = new RAPLaunchConfig( config );
    validator = new RAPLaunchConfigValidator( rapConfig );
    projectsToDelete = new ArrayList();
  }

  @After
  public void tearDown() throws Exception {
    config.delete();
    deleteProjects( projectsToDelete );
    deleteFeature();
  }

  @Test
  public void testValidate_emptyDataLocation() {
    rapConfig.setDataLocation( "" );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_DATA_LOCATION ) );
  }

  @Test
  public void testValidate_validServletPath() throws Exception {
    rapConfig.setOpenBrowser( true );
    rapConfig.setServletPath( "/foo" );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  @Test
  public void testValidate_emptyServletPath() {
    rapConfig.setOpenBrowser( true );
    // servlet name has a default value, clear this
    rapConfig.setServletPath( "" );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_SERVLET_PATH ) );
  }

  @Test
  public void testValidate_servletPathWithInvalidCharacters() {
    rapConfig.setOpenBrowser( true );
    rapConfig.setServletPath( "/rap*" );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_SERVLET_PATH_INVALID ) );
  }

  @Test
  public void testValidate_servletPathWithoutLeadingSlash() {
    rapConfig.setOpenBrowser( true );
    rapConfig.setServletPath( "rap" );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_SERVLET_PATH_LEADING_SLASH ) );
  }

  @Test
  public void testValidate_servletPathWithoutLeadingSlash_withoutOpenBrowser() {
    rapConfig.setOpenBrowser( false );
    rapConfig.setServletPath( "rap" );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  @Test
  public void testValidate_invalidButDisabledContextPath() {
    rapConfig.setContextPath( "contains space" );
    rapConfig.setUseManualContextPath( false );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  @Test
  public void testValidate_contextPathWithLeadingSlash() {
    rapConfig.setContextPath( "/ok" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  @Test
  public void testValidate_contextPathWithTrailingSlash() {
    rapConfig.setContextPath( "/foo/" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  @Test
  public void testValidate_contextPathWithoutLeadingSlash() {
    rapConfig.setContextPath( "noleadingslash" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_CONTEXT_PATH_LEADING_SLASH ) );
  }

  @Test
  public void testValidate_invalidContextPath() {
    rapConfig.setContextPath( "/invalid\\character" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_CONTEXT_PATH ) );
  }

  @Test
  public void testValidate_contextPathWithSpaces() {
    rapConfig.setContextPath( "/contains space" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_CONTEXT_PATH ) );
  }

  @Test
  public void testValidate_contextPathWithDoubleSlash() {
    rapConfig.setContextPath( "/double//slash" );
    rapConfig.setUseManualContextPath( true );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_CONTEXT_PATH ) );
  }

  @Test
  public void testValidate_invalidPort() {
    rapConfig.setUseManualPort( true );
    rapConfig.setPort( -1 );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_PORT ) );
  }

  @Test
  public void testValidate_validPortAfterRetry() {
    rapConfig.setUseManualPort( true );
    rapConfig.setPort( -1 );
    // Fix validation error and retry
    rapConfig.setPort( 8080 );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  @Test
  public void testValidate_invalidTimeout() {
    rapConfig.setUseSessionTimeout( true );
    rapConfig.setSessionTimeout( -1 );

    IStatus[] states = validator.validate();

    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_TIMEOUT ) );
  }

  @Test
  public void testValidate_zeroTimeout() {
    rapConfig.setUseSessionTimeout( true );
    rapConfig.setSessionTimeout( 0 );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  @Test
  public void testValidate_positiveTimeout() {
    rapConfig.setUseSessionTimeout( true );
    rapConfig.setSessionTimeout( Integer.MAX_VALUE );

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
