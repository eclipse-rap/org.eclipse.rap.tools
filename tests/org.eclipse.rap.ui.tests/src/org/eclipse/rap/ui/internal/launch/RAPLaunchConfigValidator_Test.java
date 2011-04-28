/*******************************************************************************
 * Copyright (c) 2007, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 *     IBM Corporation - original code for feature project creation, from PDE test cases
 *     Cole Markham - feature-based validation testing
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.ui.wizards.feature.CreateFeatureProjectOperation;
import org.eclipse.pde.internal.ui.wizards.feature.FeatureData;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.rap.ui.tests.Fixture;
import org.eclipse.rap.ui.tests.TestPluginProject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;


public class RAPLaunchConfigValidator_Test extends TestCase {

  private static final String FEATURE_PROJECT_NAME = "com.junitTest.feature";
  
  private static final class MyLevel extends Level {
    private static final long serialVersionUID = 1L;
    
    protected MyLevel( String name, int value ) {
      super( name, value );
    }
  }
  
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
  
  public void testValidateServletName() {
    // servlet name has a default value, clear this
    rapConfig.setServletName( "" );
    
    IStatus[] states = validator.validate();
    
    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_SERVLET_NAME ) );
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

  public void testValidateServletNameWithBrandingNotInBundles()
    throws CoreException
  {
    String servletName = "servletTest";
    rapConfig.setServletName( servletName );
    createBrandingExtensionProject( "test.project", "test.id", servletName );
    
    IStatus[] states = validator.validate();
    
    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_SERVLET_BUNDLE ) );
  }
  
  public void testValidateServletNameWithBrandingInBundles()
    throws CoreException
  {
    String servletName = "servletTest";
    rapConfig.setServletName( servletName );
    createBrandingExtensionProject( "test.project", "test.id", servletName );
    config.setAttribute( "workspace_bundles", "test.project@default:default" );
    
    IStatus[] states = validator.validate();
    
    assertTrue( isOk( states ) );
  }
  
  public void testValidateServletNameSameServletNames() throws CoreException {
    String servletName = "servletTest";
    rapConfig.setServletName( servletName );
    createBrandingExtensionProject( "test.project", "test.id", servletName );
    createBrandingExtensionProject( "test.project2", "test.id2", servletName );
    config.setAttribute( "workspace_bundles", "test.project2@default:default" );
    
    IStatus[] states = validator.validate();
    
    assertTrue( isOk( states ) );
  }
  
  public void testValidateServletNameRap() throws CoreException {
    String servletName = "servletTest";
    createBrandingExtensionProject( "test.project", "test.id", servletName );
    config.setAttribute( "workspace_bundles", "test.project@default:default" );

    IStatus[] states = validator.validate();
    
    assertTrue( isOk( states ) );
  }
  
  public void testValidateServletNameWithBrandingNotInFeatures() throws Exception {
    String servletName = "servletTest";
    rapConfig.setServletName( servletName );
    createFeature( null, new IPluginBase[] {} );
    config.setAttribute(  IPDELauncherConstants.USE_CUSTOM_FEATURES, "true" );
    Set featureSet = Collections.singleton( FEATURE_PROJECT_NAME );
    config.setAttribute( IPDELauncherConstants.SELECTED_FEATURES, featureSet );

    IStatus[] states = validator.validate();
    
    assertTrue( isOk( states ) );
  }

  public void testValidateServletNameWithBrandingInFeatures() throws Exception {
    String servletName = "servletTest";
    rapConfig.setServletName( servletName );
    createBrandingExtensionProject( "test.project", "test.id", servletName );
    IProject project = getProject( "test.project" );
    IPluginModelBase pluginModel = PluginRegistry.findModel( project );
    createFeature( null, new IPluginBase[] { pluginModel.getPluginBase() } );
    config.setAttribute( IPDELauncherConstants.USE_CUSTOM_FEATURES, "true" );
    Set featureSet = Collections.singleton( FEATURE_PROJECT_NAME );
    config.setAttribute( IPDELauncherConstants.SELECTED_FEATURES, featureSet );

    IStatus[] states = validator.validate();
    
    assertTrue( isOk( states ) );
  }
  
  public void testValidateEntryPointProjectNotInSelectedBundles()
    throws CoreException
  {
    String id = "entrypoint.id.1";
    String param = "param1";
    createEntryPointExtensionProject( "test.project", id, param );
    rapConfig.setEntryPoint( param );
    
    IStatus[] states = validator.validate();
    
    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_ENTRY_POINT ) );
  }
  
  public void testValidateEntryPointNotExisting() {
    rapConfig.setEntryPoint( "test.Entrypoint" );
    
    IStatus[] states = validator.validate();
    
    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_ENTRY_POINT ) );
  }
  
  public void testValidateEntryPointEmptyWorkspaceBundles() {
    config.setAttribute( "workspace_bundles", "" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( "test.Entrypoint" );
    
    IStatus[] states = validator.validate();
    
    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_ENTRY_POINT ) );
  }
  
  public void testValidateEntryPointProjectInSelectedBundles()
    throws CoreException
  {
    String id = "entrypoint.id.1";
    String param = "param1";
    createEntryPointExtensionProject( "test.project", id, param );
    config.setAttribute( "workspace_bundles", "test.project@default:default" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( param );

    IStatus[] states = validator.validate();
    
    assertTrue( isOk( states ) );
  }
  
  public void testValidateEntryPointSameEntryPoints() throws CoreException {
    String param = "param1";
    createEntryPointExtensionProject( "test.project", "entrypoint.id.1", param );
    createEntryPointExtensionProject( "test.project2", "entrypoint.id.2", param );
    config.setAttribute( "workspace_bundles", "test.project@default:default" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( param );
    
    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }
  
  public void testValidateEntryPointMultipleProjectsInWorkspace()
    throws CoreException
  {
    String id = "entrypoint.id.1";
    String param = "param1";
    createEntryPointExtensionProject( "test.project", id, param );
    createEntryPointExtensionProject( "test.project.test.2", id + "2", param + "2" );
    config.setAttribute( "workspace_bundles", "test.project.test.2@default:default" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( param );
    
    IStatus[] states = validator.validate();
    
    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_ENTRY_POINT ) );
  }
  
  public void testValidateMultipleEntryPointProjectsInWorkspace()
    throws CoreException
  {
    String id = "entrypoint.id.1";
    String param = "param1";
    createEntryPointExtensionProject( "test.project", id, param );
    createEntryPointExtensionProject( "test.project.test.2", id + "2", param + "2" );
    config.setAttribute( "workspace_bundles",
                         "test.project.test.2@default:default," + "test.project@default:default" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( param );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }
  
  public void testValidateEntryPointWithApplication() throws CoreException {
    createApplicationExtensionProject( "test.project", "id" );
    config.setAttribute( "workspace_bundles", "test.project@default:default" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( "test.project." + "id" );

    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }
  
  public void testEntryPointEmpty() {
    IStatus[] states = validator.validate();

    assertTrue( isOk( states ) );
  }

  public void testServletName() throws Exception {
    rapConfig.setServletName( "/foo" );

    IStatus[] states = validator.validate();

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
  
  public void testLogLevel() {
    rapConfig.setLogLevel( new MyLevel( "custom", 1234 ) );
    
    IStatus[] states = rapConfig.getValidator().validate();
    
    assertTrue( hasStatusCode( states, RAPLaunchConfigValidator.ERR_LOG_LEVEL ) );
  }
  
  //////////////////
  // helping methods
  private void deleteOnTearDown( TestPluginProject project ) {
    projectsToDelete.add( project );
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
  
  private void createEntryPointExtensionProject( String projectName, String id, String parameter )
    throws CoreException
  {
    TestPluginProject project = new TestPluginProject( projectName );
    deleteOnTearDown( project );
    Map attributes = new HashMap();
    attributes.put( "id", id );
    attributes.put( "class", "class1" );
    attributes.put( "parameter", parameter );
    project.createExtension( "org.eclipse.rap.ui.entrypoint", "entrypoint", attributes );
  }
  
  private void createApplicationExtensionProject( String projectName, String id )
    throws CoreException
  {
    TestPluginProject project = new TestPluginProject( projectName );
    deleteOnTearDown( project );
    Map attributes = new HashMap();
    attributes.put( "cardinality", "singleton-global" );
    attributes.put( "thread", "main" );
    attributes.put( "visible", "true" );
    String extensionId = "org.eclipse.core.runtime.applications";
    project.createExtensionWithExtensionId( extensionId, "application", attributes, id );
  }
  
  private void createBrandingExtensionProject( String projectName, String id, String servletName )
    throws CoreException
  {
    TestPluginProject project = new TestPluginProject( projectName );
    deleteOnTearDown( project );
    Map attributes = new HashMap();
    attributes.put( "id", id );
    attributes.put( "servletName", servletName );
    project.createExtension( "org.eclipse.rap.ui.branding", "branding", attributes );
  }

  private static FeatureData createDefaultFeatureData() {
    FeatureData fd = new FeatureData();
    fd.id = FEATURE_PROJECT_NAME;
    fd.name = FEATURE_PROJECT_NAME;
    fd.version = "1.0.0";
    return fd;
  }

  private static void createFeature( FeatureData fd, IPluginBase[] plugins ) throws Exception {
    FeatureData featureData = fd;
    if( fd == null ) {
      featureData = createDefaultFeatureData();
    }
    IProject project = getProject( FEATURE_PROJECT_NAME );
    IPath path = Platform.getLocation();
    Shell activeShell = Display.getCurrent().getActiveShell();
    IRunnableWithProgress op
      = new CreateFeatureProjectOperation( project, path, featureData, plugins, activeShell );
    IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
    progressService.runInUI( progressService, op, null );
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
