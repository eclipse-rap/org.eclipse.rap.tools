/*******************************************************************************
 * Copyright (c) 2009 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.tests;

import java.io.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.internal.core.bundle.BundlePluginBase;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundlePluginModel;
import org.eclipse.pde.internal.core.ibundle.IBundle;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModelBase;

public final class TestPluginProject {

  private static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";

  private static final String MANIFEST_MF = "META-INF/MANIFEST.MF";
  private static final String PLUGIN_XML = "plugin.xml";
  private static final String DEFAULT_SOURCE_FOLDER = "src";
  private static final String DEFAULT_OUTPUT_FOLDER = "bin";

  private static final String PLUGIN_NATURE = "org.eclipse.pde.PluginNature";

  private static int uniqueId = 0;

  private final List extensions;
  private final IProject project;
  private final IJavaProject javaProject;

  public TestPluginProject() throws CoreException {
    this( "test.project." + uniqueId );
    uniqueId++;
  }

  public TestPluginProject( final String name ) throws CoreException {
    extensions = new ArrayList();
    project = createProject( name );
    project.open( new NullProgressMonitor() );
    addNature( JavaCore.NATURE_ID );
    javaProject = createJavaProjectLayout();
    addNature( PLUGIN_NATURE );
    createManifest();
    waitForAutoBuild();
  }

  public String getName() {
    return project.getName();
  }
  
  public IJavaProject getJavaProject() {
    return javaProject;
  }

  public void createExtension( final String point,
                               final String element,
                               final Map attributes )
    throws CoreException
  {
    extensions.add( new Extension( point, element, attributes ) );
    String contents = createPluginXmlContents();
    savePluginXml( contents.toString() );
    ensureSingletonBundle();
  }

  public void delete() throws CoreException {
    project.delete( true, true, new NullProgressMonitor() );
  }

  //////////////////////////
  // Project creation helper

  private IJavaProject createJavaProjectLayout() throws CoreException {
    project.open( new NullProgressMonitor() );
    IJavaProject result = JavaCore.create( project );
    IFolder srcFolder = project.getFolder( DEFAULT_SOURCE_FOLDER );
    if( !srcFolder.exists() ) {
      srcFolder.create( false, true, new NullProgressMonitor() );
    }
    IPath srcPath = srcFolder.getFullPath();
    IClasspathEntry srcEntry = JavaCore.newSourceEntry( srcPath );
    IPath jrePath = JavaRuntime.getDefaultJREContainerEntry().getPath();
    IClasspathEntry jreEntry = JavaCore.newContainerEntry( jrePath );
    IPath binPath = project.getFullPath().append( DEFAULT_OUTPUT_FOLDER );
    IClasspathEntry[] cpes = new IClasspathEntry[]{ srcEntry, jreEntry };
    result.setRawClasspath( cpes, binPath, new NullProgressMonitor() );
    return result;
  }

  private void createManifest() {
    IFile pluginFile = project.getFile( PLUGIN_XML );
    IFile bundleFile = project.getFile( MANIFEST_MF );
    IBundlePluginModelBase model
      = new WorkspaceBundlePluginModel( bundleFile, pluginFile );
    IPluginBase plugin = model.getPluginBase();
    IBundle bundle = ( ( BundlePluginBase )plugin ).getBundle();
    bundle.setHeader( BUNDLE_SYMBOLIC_NAME, project.getName() ); //$NON-NLS-1$
    bundle.setHeader( "Bundle-Version", "1.0.0" ); //$NON-NLS-1$ //$NON-NLS-2$
    bundle.setHeader( "Bundle-ManifestVersion", "2" ); //$NON-NLS-1$ //$NON-NLS-2$
    bundle.setHeader( "Bundle-ActivationPolicy", "lazy" ); //$NON-NLS-1$ //$NON-NLS-2$
    bundle.setHeader( "Bundle-RequiredExecutionEnvironment", "J2SE-1.4" ); //$NON-NLS-1$ //$NON-NLS-2$
    model.save();
  }

  private void addNature( final String nature ) throws CoreException {
    IProjectDescription description = project.getDescription();
    String[] natures = description.getNatureIds();
    String[] newNatures = new String[ natures.length + 1 ];
    System.arraycopy( natures, 0, newNatures, 0, natures.length );
    newNatures[ natures.length ] = nature;
    description.setNatureIds( newNatures );
    project.setDescription( description, new NullProgressMonitor() );
  }

  private static IProject createProject( final String name )
    throws CoreException
  {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IProject result = root.getProject( name );
    result.create( new NullProgressMonitor() );
    return result;
  }

  ///////////////////////////////
  // plugin.xml generation helper
  
  private String createPluginXmlContents() {
    StringBuffer result = new StringBuffer();
    result.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
    result.append( "<?eclipse version=\"3.4\"?>\n" );
    result.append( "<plugin>\n" );
    Iterator iter = extensions.iterator();
    while( iter.hasNext() ) {
      Extension extension = ( Extension )iter.next();
      result.append( extension.toXml() );
    }
    result.append( "</plugin>" );
    return result.toString();
  }

  private void savePluginXml( final String contents ) throws CoreException {
    IFile pluginXml = project.getFile( PLUGIN_XML );
    InputStream stream = toUtf8Stream( contents );
    if( !pluginXml.exists() ) {
      pluginXml.create( stream, true, new NullProgressMonitor() );
    } else {
      pluginXml.setContents( stream, false, false, new NullProgressMonitor() );
    }
    waitForAutoBuild();
  }

  private static ByteArrayInputStream toUtf8Stream( final String string ) {
    try {
      return new ByteArrayInputStream( string.getBytes( "UTF-8" ) ); //$NON-NLS-1$
    } catch( UnsupportedEncodingException e ) {
      throw new RuntimeException( "Failed to encode string to UTF-8.", e ); //$NON-NLS-1$
    }
  }

  private void ensureSingletonBundle() {
    IFile pluginFile = project.getFile( PLUGIN_XML );
    IFile bundleFile = project.getFile( MANIFEST_MF );
    IBundlePluginModelBase model
      = new WorkspaceBundlePluginModel( bundleFile, pluginFile );
    IPluginBase plugin = model.getPluginBase();
    IBundle bundle = ( ( BundlePluginBase )plugin ).getBundle();
    String symbolicName = bundle.getHeader( BUNDLE_SYMBOLIC_NAME );
    if( !symbolicName.endsWith( "singleton:=true" ) ) {
      symbolicName += "; singleton:=true";
      bundle.setHeader( BUNDLE_SYMBOLIC_NAME, symbolicName );
      model.save();
    }
  }

  //////////////////
  // helping methods

  private static void waitForAutoBuild() throws CoreException {
    try {
      NullProgressMonitor monitor = new NullProgressMonitor();
      Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, monitor );
    } catch( OperationCanceledException e ) {
      handleException( "waitForAutoBuild failed", e );
    } catch( InterruptedException e ) {
      handleException( "waitForAutoBuild failed", e );
    }
  }
  private static void handleException( final String msg,
                                       final Throwable throwable )
    throws CoreException
  {
    String pluginId = Fixture.PLUGIN_ID;
    IStatus status = new Status( IStatus.ERROR, pluginId, msg, throwable );
    throw new CoreException( status );
  }

  /////////////////
  // helper classes
  
  private static final class Extension {

    private final String point;
    private final String element;
    private final Map attributes;

    private Extension( final String point, 
                       final String element, 
                       final Map attributes ) 
    {
      this.point = point;
      this.element = element;
      this.attributes = new HashMap();
      if( attributes != null ) {
        this.attributes.putAll( attributes );
      }
    }
    
    private String toXml() {
      StringBuffer result = new StringBuffer();
      result.append( "<extension point=\"" );
      result.append( point );
      result.append( "\">\n" );
      result.append( "  <");
      result.append( element );
      result.append( "\n" );
      Iterator iter = attributes.keySet().iterator();
      while( iter.hasNext() ) {
        String name = ( String )iter.next();
        result.append( "    " );
        result.append( name );
        result.append( "=\"" );
        result.append( attributes.get( name ) );
        result.append( "\"" );
        if( iter.hasNext() ) {
          result.append( "\n" );
        }
      }
      result.append( ">\n");
      result.append( "  </");
      result.append( element );
      result.append( ">\n");
      result.append( "</extension>\n" );
      return result.toString();
    }
  }
}
