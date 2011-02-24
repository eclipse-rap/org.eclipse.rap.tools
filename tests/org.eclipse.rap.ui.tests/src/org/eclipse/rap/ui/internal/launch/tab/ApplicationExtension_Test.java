/******************************************************************************* 
 * Copyright (c) 2010 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.rap.ui.tests.TestPluginProject;


public class ApplicationExtension_Test extends TestCase {
  
  private List projectsToDelete = new ArrayList();

  public void testFindByIdWithEmptyWorkspace() throws Exception {
    ApplicationExtension findById = ApplicationExtension.findById( "some.id" );
    assertNull( findById );
  }

  public void testFindByIdWithSingleProject() throws Exception {
    String id = "application.id.1";
    // set up plug-in project with entry point extension
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map attributes = new HashMap();    
    attributes.put( "cardinality", "singleton-global" );
    attributes.put( "thread", "main" );
    attributes.put( "visible", "true" );
    project.createExtensionWithExtensionId( 
                             "org.eclipse.core.runtime.applications",
                             "application", 
                             attributes,
                             id );
    // find id and assert result
    ApplicationExtension findById = ApplicationExtension.findById( id );
    assertNotNull( findById );
    assertEquals( id, findById.getId() );    
    assertEquals( project.getName(), findById.getProject() );
  }

  public void testFindByIdWithDuplicateIds() throws Exception {
    String id = "entrypoint.id.1";
    // set up plug-in project and add entry point extension with same id twice
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map attributes = new HashMap();    
    attributes.put( "cardinality", "singleton-global" );
    attributes.put( "thread", "main" );
    attributes.put( "visible", "true" );
    project.createExtensionWithExtensionId( 
                             "org.eclipse.core.runtime.applications",
                             "application", 
                             attributes,
                             id );
    attributes = new HashMap();    
    attributes.put( "cardinality", "singleton-global" );
    attributes.put( "thread", "main" );
    attributes.put( "visible", "true" );
    project.createExtensionWithExtensionId( 
                             "org.eclipse.core.runtime.applications",
                             "application", 
                             attributes,
                             id );
    // find id and assert result
    ApplicationExtension findById = ApplicationExtension.findById( id );
    assertNotNull( findById );
    assertEquals( id, findById.getId() );
    assertEquals( project.getName(), findById.getProject() );
  }
  
  public void testFindByIdWithIncompleteExtension() throws Exception {
    // set up plug-in project and add incomplete entry point extension
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    project.createExtension( "org.eclipse.core.runtime.applications",
                             "application", 
                             null );
    ApplicationExtension findById = ApplicationExtension.findById( "some.id" );
    assertNull( findById );
  }
  
  public void testFindInWorkspaceWithEmptyWorkspace() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    ApplicationExtension[] extensions
      = ApplicationExtension.findInWorkspace( new NullProgressMonitor() );
    assertNotNull( extensions );
    assertEquals( 0, extensions.length );
  }

  public void testFindInWorkspace() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map attributes = new HashMap();    
    attributes.put( "cardinality", "singleton-global" );
    attributes.put( "thread", "main" );
    attributes.put( "visible", "true" );
    String id = "application.point.1";
    project.createExtensionWithExtensionId( 
                             "org.eclipse.core.runtime.applications",
                             "application", 
                             attributes,
                             id  );
    ApplicationExtension[] extensions
      = ApplicationExtension.findInWorkspace( new NullProgressMonitor() );
    assertNotNull( extensions );
    assertEquals( 1, extensions.length );
    assertEquals( id, extensions[ 0 ].getId() );
  }
  
  public void testFindInPlugins() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    String id = "application.point.1";
    project.createExtensionWithExtensionId( 
                             "org.eclipse.core.runtime.applications",
                             "application", 
                             null,
                             id  );
    TestPluginProject filteredProject = new TestPluginProject();
    deleteOnTearDown( filteredProject );
    Map attributes = new HashMap();
    attributes = new HashMap();
    attributes.put( "id", "dontFindMe" );
    filteredProject.createExtension( "org.eclipse.core.runtime.applications",
                                     "application", 
                                     attributes );
    String[] plugins = new String[] { project.getName() }; // name == plugin-id
    NullProgressMonitor monitor = new NullProgressMonitor();
    ApplicationExtension[] extensions
      = ApplicationExtension.findInWorkspacePlugins( plugins, monitor );
    assertEquals( 1, extensions.length );
    assertEquals( id, extensions[ 0 ].getId() );
    assertEquals( project.getName(), extensions[ 0 ].getProject() );
  }
  
  public void testFindInWorkspaceWithIncompleteExtension() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    project.createExtension( "org.eclipse.core.runtime.applications",
                             "application", 
                             null );
    ApplicationExtension[] extensions
      = ApplicationExtension.findInWorkspace( new NullProgressMonitor() );
    assertNotNull( extensions );
    assertEquals( 1, extensions.length );
    assertEquals( project.getName(), extensions[ 0 ].getProject() );
    assertEquals( null, extensions[ 0 ].getId() );
  }
  
  protected void setUp() throws Exception {
    super.setUp();
    projectsToDelete.clear();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
    Iterator iter = projectsToDelete.iterator();
    while( iter.hasNext() ) {
      TestPluginProject project = ( TestPluginProject )iter.next();
      project.delete();
    }
  }
  
  private void deleteOnTearDown( final TestPluginProject project ) {
    projectsToDelete.add( project );
  }
}
