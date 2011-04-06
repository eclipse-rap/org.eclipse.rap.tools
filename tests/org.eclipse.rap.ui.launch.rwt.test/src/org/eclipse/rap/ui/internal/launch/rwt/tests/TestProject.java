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
package org.eclipse.rap.ui.internal.launch.rwt.tests;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

public final class TestProject {
  private static final String PLUGIN_ID = "org.eclipse.rap.ui.launch.rwt.tests";

  private static final String DEFAULT_SOURCE_FOLDER = "src";
  private static final String DEFAULT_OUTPUT_FOLDER = "bin";

  
  private static int uniqueId = 0;
  private static List projects = new LinkedList();
  
  public static void deleteAll() throws CoreException {
    while( projects.size() > 0 ) {
      TestProject project = ( TestProject )projects.get( 0 );
      project.delete();
      projects.remove( 0 );
    }
  }
  
  private final String projectName;
  private IProject project;
  private IJavaProject javaProject;

  public TestProject() {
    projectName = "test.project." + uniqueId;
    uniqueId++;
  }

  public String getName() {
    initializeProject();
    return project.getName();
  }
  
  public IProject getProject() {
    initializeProject();
    return project;
  }
  
  public IJavaProject getJavaProject() throws CoreException {
    initializeJavaProject();
    return javaProject;
  }
  
  public void createJavaProject() throws CoreException {
    initializeJavaProject();
  }
  
  public IFolder createFolder( String name ) throws CoreException {
    IFolder result = project.getFolder( name );
    if( !result.exists() ) {
      result.create( true, true, newProgressMonitor() );
    }
    return result;
  }
  
  public IFile createFile( IContainer parent, String fileName, String content ) throws CoreException 
  {
    IFile result = parent.getFile( new Path( fileName ) );
    InputStream stream = Fixture.toUtf8Stream( content );
    if( !result.exists() ) {
      result.create( stream, true, newProgressMonitor() );
    } else {
      result.setContents( stream, false, false, newProgressMonitor() );
    }
    return result;
  }
  
  public ICompilationUnit createJavaClass( String packageName, String className, String content ) 
    throws CoreException 
  {
    initializeJavaProject();
    IProgressMonitor monitor = newProgressMonitor();
    IFile srcFolder = project.getFile( DEFAULT_SOURCE_FOLDER );
    IPackageFragmentRoot packageRoot
      = javaProject.findPackageFragmentRoot( srcFolder.getFullPath() );
    IPackageFragment pkg = packageRoot.getPackageFragment( packageName );
    if( !pkg.exists() ) {
      packageRoot.createPackageFragment( packageName, true, monitor );
    }
    String cuName = className + ".java";
    ICompilationUnit result = pkg.createCompilationUnit( cuName, content, true, monitor );
    waitForAutoBuild();
    return result;
  }
  
  public void delete() throws CoreException {
    if( isProjectCreated() ) {
      project.delete( true, true, newProgressMonitor() );
    }
  }

  //////////////////////////
  // Project creation helper

  private boolean isProjectCreated() {
    return project != null;
  }

  private boolean isJavaProjectCreated() {
    return javaProject != null;
  }

  private void initializeProject() {
    if( !isProjectCreated() ) {
      try {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        project = root.getProject( projectName );
        project.create( newProgressMonitor() );
        project.open( newProgressMonitor() );
        waitForAutoBuild();
      } catch( CoreException ce ) {
        throw new RuntimeException( ce );
      }
      projects.add( this );
    }
  }

  private void initializeJavaProject() throws CoreException {
    initializeProject();
    if( !isJavaProjectCreated() ) {
      addNature( JavaCore.NATURE_ID );
      javaProject = JavaCore.create( project );

      IFolder binFolder = createFolder( DEFAULT_OUTPUT_FOLDER );
      javaProject.setOutputLocation( binFolder.getFullPath(), newProgressMonitor() );
      
      IPath jrePath = JavaRuntime.getDefaultJREContainerEntry().getPath();
      IClasspathEntry jreEntry = JavaCore.newContainerEntry( jrePath );
      javaProject.setRawClasspath( new IClasspathEntry[] { jreEntry }, newProgressMonitor() );
      
      IFolder sourceFolder = createFolder( DEFAULT_SOURCE_FOLDER );
      IPackageFragmentRoot packageRoot = javaProject.getPackageFragmentRoot( sourceFolder );
      IClasspathEntry entry = JavaCore.newSourceEntry( packageRoot.getPath() );
      addClasspathEntry( entry );

      waitForAutoBuild();
    }
  }

  private void addNature( String nature ) throws CoreException {
    IProjectDescription description = project.getDescription();
    String[] natures = description.getNatureIds();
    String[] newNatures = new String[ natures.length + 1 ];
    System.arraycopy( natures, 0, newNatures, 0, natures.length );
    newNatures[ natures.length ] = nature;
    description.setNatureIds( newNatures );
    project.setDescription( description, newProgressMonitor() );
  }

  private void addClasspathEntry( IClasspathEntry entry ) throws JavaModelException {
    IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
    IClasspathEntry[] newEntries = new IClasspathEntry[ oldEntries.length + 1 ];
    System.arraycopy( oldEntries, 0, newEntries, 0, oldEntries.length );
    newEntries[ oldEntries.length ] = entry;
    javaProject.setRawClasspath( newEntries, newProgressMonitor() );
  }

  private static void waitForAutoBuild() throws CoreException {
    try {
      Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, newProgressMonitor() );
    } catch( OperationCanceledException e ) {
      handleException( "waitForAutoBuild failed", e );
    } catch( InterruptedException e ) {
      handleException( "waitForAutoBuild failed", e );
    }
  }
  
  private static IProgressMonitor newProgressMonitor() {
    return new NullProgressMonitor();
  }

  private static void handleException( String msg, Throwable throwable ) throws CoreException {
    IStatus status = new Status( IStatus.ERROR, PLUGIN_ID, msg, throwable );
    throw new CoreException( status );
  }
}
