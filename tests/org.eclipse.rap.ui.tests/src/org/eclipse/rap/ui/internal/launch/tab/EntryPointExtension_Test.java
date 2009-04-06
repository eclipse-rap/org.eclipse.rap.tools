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
package org.eclipse.rap.ui.internal.launch.tab;

import java.util.*;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.rap.ui.tests.TestPluginProject;


public class EntryPointExtension_Test extends TestCase {
  
  private List projectsToDelete = new ArrayList();

  public void testFindByIdWithEmptyWorkspace() throws Exception {
    EntryPointExtension findById = EntryPointExtension.findById( "some.id" );
    assertNull( findById );
  }

  public void testFindByIdWithSingleProject() throws Exception {
    String id = "entrypoint.id.1";
    String param = "param1";
    // set up plug-in project with entry point extension
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map attributes = new HashMap();
    attributes.put( "id", id );
    attributes.put( "class", "class1" );
    attributes.put( "parameter", param );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint", 
                             attributes );
    // find id and assert result
    EntryPointExtension findById = EntryPointExtension.findById( id );
    assertNotNull( findById );
    assertEquals( id, findById.getId() );
    assertEquals( param, findById.getParameter() );
    assertEquals( project.getName(), findById.getProject() );
  }

  public void testFindByIdWithDuplicateIds() throws Exception {
    String id = "entrypoint.id.1";
    // set up plug-in project and add entry point extension with same id twice
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map attributes = new HashMap();
    attributes.put( "id", id );
    attributes.put( "class", "class1" );
    attributes.put( "parameter", "param1" );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint", 
                             attributes );
    attributes = new HashMap();
    attributes.put( "id", id );
    attributes.put( "class", "class2" );
    attributes.put( "parameter", "param2" );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint", 
                             attributes );
    // find id and assert result
    EntryPointExtension findById = EntryPointExtension.findById( id );
    assertNotNull( findById );
    assertEquals( id, findById.getId() );
    assertEquals( project.getName(), findById.getProject() );
  }
  
  public void testFindByIdWithIncompleteExtension() throws Exception {
    // set up plug-in project and add incomplete entry point extension
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint", 
                             null );
    EntryPointExtension findById = EntryPointExtension.findById( "some.id" );
    assertNull( findById );
  }
  
  public void testFindInWorkspaceWithEmptyWorkspace() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    EntryPointExtension[] extensions
      = EntryPointExtension.findInWorkspace( new NullProgressMonitor() );
    assertNotNull( extensions );
    assertEquals( 0, extensions.length );
  }

  public void testFindInWorkspace() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map attributes = new HashMap();
    attributes.put( "id", "entry.point.1" );
    attributes.put( "class", "class2" );
    attributes.put( "parameter", "param2" );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint", 
                             attributes );
    EntryPointExtension[] extensions
      = EntryPointExtension.findInWorkspace( new NullProgressMonitor() );
    assertNotNull( extensions );
    assertEquals( 1, extensions.length );
    assertEquals( "entry.point.1", extensions[ 0 ].getId() );
  }
  
  public void testFindInPlugins() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map attributes = new HashMap();
    attributes.put( "id", "findMe" );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint", 
                             attributes );
    TestPluginProject filteredProject = new TestPluginProject();
    deleteOnTearDown( filteredProject );
    attributes = new HashMap();
    attributes.put( "id", "dontFindMe" );
    filteredProject.createExtension( "org.eclipse.rap.ui.entrypoint",
                                     "entrypoint", 
                                     attributes );
    String[] plugins = new String[] { project.getName() }; // name == plugin-id
    NullProgressMonitor monitor = new NullProgressMonitor();
    EntryPointExtension[] extensions
      = EntryPointExtension.findInPlugins( plugins, monitor );
    assertEquals( 1, extensions.length );
    assertEquals( "findMe", extensions[ 0 ].getId() );
    assertEquals( project.getName(), extensions[ 0 ].getProject() );
  }
  
  public void testFindInWorkspaceWithIncompleteExtension() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint", 
                             null );
    EntryPointExtension[] extensions
      = EntryPointExtension.findInWorkspace( new NullProgressMonitor() );
    assertNotNull( extensions );
    assertEquals( 1, extensions.length );
    assertEquals( project.getName(), extensions[ 0 ].getProject() );
    assertEquals( null, extensions[ 0 ].getParameter() );
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
