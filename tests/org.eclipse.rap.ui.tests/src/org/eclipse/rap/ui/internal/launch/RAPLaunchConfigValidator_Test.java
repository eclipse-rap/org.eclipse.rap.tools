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
    
    protected MyLevel( final String name, final int value ) {
      super( name, value );
    }
  }
  
  private ILaunchConfigurationWorkingCopy config;
  private RAPLaunchConfig rapConfig;
  private List projectsToDelete;

  protected void setUp() throws Exception {
    config = Fixture.createRAPLaunchConfig();
    rapConfig = new RAPLaunchConfig( config );
    projectsToDelete = new ArrayList();
  }
  
  protected void tearDown() throws Exception {
    config.delete();
    deleteProjects( projectsToDelete );
    deleteFeature();
  }
  
  public void testValidateDataLocation() {
    rapConfig.setDataLocation( "" );
    RAPLaunchConfigValidator validator = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = validator.validate();
    int code = RAPLaunchConfigValidator.ERR_DATA_LOCATION;
    assertTrue( findStatusCode( states, code ) );
  }
  
  public void testValidateServletName() {
    // servlet name has a default value, clear this
    rapConfig.setServletName( "" );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_SERVLET_NAME;
    assertTrue( findStatusCode( states, code ) );
  }

  public void testValidateWsRAP() throws Exception {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    config.setAttribute( IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "-ws rap" );
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    RAPLaunchConfigValidator validator = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = validator.validate();
    int code = RAPLaunchConfigValidator.WARN_WS_WRONG;
    assertFalse( findStatusCode( states, code ) );
  }

  public void testValidateWsEmpty() throws Exception {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    config.setAttribute( IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "" );
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    RAPLaunchConfigValidator validator = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = validator.validate();
    int code = RAPLaunchConfigValidator.WARN_WS_WRONG;
//    TODO [rst] Change to assertTrue when bug 338544 is fixed
    assertFalse( findStatusCode( states, code ) );
  }

  public void testValidateWsNotRAP() throws Exception {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    config.setAttribute( IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "-ws test" );
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    RAPLaunchConfigValidator validator = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = validator.validate();
    int code = RAPLaunchConfigValidator.WARN_WS_WRONG;
//    TODO [rst] Change to assertTrue when bug 338544 is fixed
    assertFalse( findStatusCode( states, code ) );
  }

  public void testValidateServletNameWithBrandingNotInBundles()
    throws CoreException
  {
    String servletName = "servletTest";
    rapConfig.setServletName( servletName );
    createBrandingExtensionProject( "test.project", "test.id", servletName );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_SERVLET_BUNDLE;
    assertTrue( findStatusCode( states, code ) );
  }
  
  public void testValidateServletNameWithBrandingInBundles()
    throws CoreException
  {
    String servletName = "servletTest";
    rapConfig.setServletName( servletName );
    createBrandingExtensionProject( "test.project", "test.id", servletName );
    config.setAttribute( "workspace_bundles", "test.project@default:default" );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_SERVLET_BUNDLE;
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testValidateServletNameSameServletNames() throws CoreException {
    String servletName = "servletTest";
    rapConfig.setServletName( servletName );
    createBrandingExtensionProject( "test.project", "test.id", servletName );
    createBrandingExtensionProject( "test.project2", "test.id2", servletName );
    config.setAttribute( "workspace_bundles", 
                         "test.project2@default:default" );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_SERVLET_BUNDLE;
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testValidateServletNameRap() throws CoreException {
    String servletName = "servletTest";
    createBrandingExtensionProject( "test.project", "test.id", servletName );
    config.setAttribute( "workspace_bundles", "test.project@default:default" );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_SERVLET_BUNDLE;
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testValidateServletNameWithBrandingNotInFeatures() throws Exception {
    String servletName = "servletTest";
    rapConfig.setServletName( servletName );
    createFeature( null, new IPluginBase[] {} );
    config.setAttribute(  IPDELauncherConstants.USE_CUSTOM_FEATURES, "true" );
    Set featureSet = Collections.singleton( FEATURE_PROJECT_NAME );
    config.setAttribute( IPDELauncherConstants.SELECTED_FEATURES, featureSet );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_SERVLET_BUNDLE;
    assertFalse( findStatusCode( states, code ) );
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
    RAPLaunchConfigValidator validator = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = validator.validate();
    int code = RAPLaunchConfigValidator.ERR_SERVLET_BUNDLE;
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testValidateEntryPointProjectNotInSelectedBundles()
    throws CoreException
  {
    String id = "entrypoint.id.1";
    String param = "param1";
    createEntryPointExtensionProject( "test.project", id, param );
    rapConfig.setEntryPoint( param );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_ENTRY_POINT;
    assertTrue( findStatusCode( states, code ) );
  }
  
  public void testValidateEntryPointNotExisting() {
    rapConfig.setEntryPoint( "test.Entrypoint" );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_ENTRY_POINT;
    assertTrue( findStatusCode( states, code ) );
  }
  
  public void testValidateEntryPointEmptyWorkspaceBundles() {
    config.setAttribute( "workspace_bundles", "" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( "test.Entrypoint" );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_ENTRY_POINT;
    assertTrue( findStatusCode( states, code ) );
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
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_ENTRY_POINT;
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testValidateEntryPointSameEntryPoints() throws CoreException {
    String param = "param1";
    createEntryPointExtensionProject( "test.project", 
                                      "entrypoint.id.1", param );
    createEntryPointExtensionProject( "test.project2", 
                                      "entrypoint.id.2", param );
    config.setAttribute( "workspace_bundles", "test.project@default:default" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( param );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_ENTRY_POINT;
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testValidateEntryPointMultipleProjectsInWorkspace()
    throws CoreException
  {
    String id = "entrypoint.id.1";
    String param = "param1";
    createEntryPointExtensionProject( "test.project", id, param );
    createEntryPointExtensionProject( "test.project.test.2", id + "2", param + "2" );
    config.setAttribute( "workspace_bundles",
                         "test.project.test.2@default:default" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( param );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_ENTRY_POINT;
    assertTrue( findStatusCode( states, code ) );
  }
  
  public void testValidateMultipleEntryPointProjectsInWorkspace()
    throws CoreException
  {
    String id = "entrypoint.id.1";
    String param = "param1";
    createEntryPointExtensionProject( "test.project", id, param );
    createEntryPointExtensionProject( "test.project.test.2", 
                                      id + "2", param + "2" );
    config.setAttribute( "workspace_bundles",
                         "test.project.test.2@default:default,"
                           + "test.project@default:default" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( param );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_ENTRY_POINT;
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testValidateEntryPointWithApplication() throws CoreException {
    String id = "id";
    createApplicationExtensionProject( "test.project", id );
    config.setAttribute( "workspace_bundles", "test.project@default:default" );
    rapConfig = new RAPLaunchConfig( config );
    rapConfig.setEntryPoint( "test.project." + id );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_ENTRY_POINT;
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testEntryPointEmpty() {
    RAPLaunchConfigValidator val = rapConfig.getValidator();
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_ENTRY_POINT;
    assertFalse( findStatusCode( states, code ) );  }
  
  public void testPort() {
    rapConfig.setUseManualPort( true );
    rapConfig.setPort( -1 );
    RAPLaunchConfigValidator val = rapConfig.getValidator();
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_PORT;
    assertTrue( findStatusCode( states, code ) );
    // Fix validation error and retry
    rapConfig.setPort( 8080 );
    states = val.validate();
    assertFalse( findStatusCode( states, code ) );
  }

  public void testTimeout() {
    rapConfig.setUseSessionTimeout( true );
    RAPLaunchConfigValidator val = rapConfig.getValidator();
    int code = RAPLaunchConfigValidator.ERR_TIMEOUT;
    // invalid values
    rapConfig.setSessionTimeout( -1 );
    IStatus[] states = val.validate();
    assertTrue( findStatusCode( states, code ) );
    rapConfig.setSessionTimeout( Integer.MIN_VALUE );
    states = val.validate();
    assertTrue( findStatusCode( states, code ) );
    // valid values
    rapConfig.setSessionTimeout( 0 );
    states = val.validate();
    assertFalse( findStatusCode( states, code ) );
    rapConfig.setSessionTimeout( 1 );
    states = val.validate();
    assertFalse( findStatusCode( states, code ) );
    rapConfig.setSessionTimeout( Integer.MAX_VALUE );
    states = val.validate();
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testUrl() {
    rapConfig.setEntryPoint( "entryPoint" );
    RAPLaunchConfigValidator val = rapConfig.getValidator();
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_PORT;
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testLogLevel() {
    rapConfig.setLogLevel( new MyLevel( "custom", 1234 ) );
    IStatus[] states = rapConfig.getValidator().validate();
    int code = RAPLaunchConfigValidator.ERR_LOG_LEVEL;
    assertTrue( findStatusCode( states, code ) );
  }
  
  //////////////////
  // helping methods
  private void deleteOnTearDown( TestPluginProject project ) {
    projectsToDelete.add( project );
  }

  private static boolean findStatusCode( final IStatus[] states, 
                                         final int code ) 
  {
    boolean result = false;
    for( int i = 0; !result && i < states.length; i++ ) {
      if( states[ i ].getCode() == code ) {
        result = true;
      }
    }
    return result;
  }

  
  private void createEntryPointExtensionProject( final String projectName,
                                                 final String id,
                                                 final String parameter )
    throws CoreException
  {
    TestPluginProject project = new TestPluginProject( projectName );
    deleteOnTearDown( project );
    Map attributes = new HashMap();
    attributes.put( "id", id );
    attributes.put( "class", "class1" );
    attributes.put( "parameter", parameter );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint",
                             attributes );
  }
  
  private void createApplicationExtensionProject( final String projectName,
                                                  final String id )
    throws CoreException
  {
    TestPluginProject project = new TestPluginProject( projectName );
    deleteOnTearDown( project );
    Map attributes = new HashMap();
    attributes.put( "cardinality", "singleton-global" );
    attributes.put( "thread", "main" );
    attributes.put( "visible", "true" );
    String extensionId = "org.eclipse.core.runtime.applications";
    project.createExtensionWithExtensionId( extensionId,
                                            "application",
                                            attributes,
                                            id );
  }
  
  private void createBrandingExtensionProject( final String projectName,
                                               final String id,
                                               final String servletName )
    throws CoreException
  {
    TestPluginProject project = new TestPluginProject( projectName );
    deleteOnTearDown( project );
    Map attributes = new HashMap();
    attributes.put( "id", id );
    attributes.put( "servletName", servletName );
    project.createExtension( "org.eclipse.rap.ui.branding",
                             "branding",
                             attributes );
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
