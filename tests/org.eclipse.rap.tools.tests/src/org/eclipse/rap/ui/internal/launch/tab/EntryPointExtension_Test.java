/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.tab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.rap.ui.tests.TestPluginProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class EntryPointExtension_Test {

  private final List<TestPluginProject> projectsToDelete = new ArrayList<TestPluginProject>();

  @Before
  public void setUp() throws Exception {
    projectsToDelete.clear();
  }

  @After
  public void tearDown() throws Exception {
    for( TestPluginProject project : projectsToDelete ) {
      project.delete();
    }
  }

  @Test
  public void testFindById_withEmptyWorkspace() throws Exception {
    EntryPointExtension extensions = EntryPointExtension.findById( "some.id" );

    assertNull( extensions );
  }

  @Test
  public void testFindById_withSingleProject() throws Exception {
    String id = "entrypoint.id.1";
    String param = "param1";
    // set up plug-in project with entry point extension
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put( "id", id );
    attributes.put( "class", "class1" );
    attributes.put( "parameter", param );
    project.createExtension( "org.eclipse.rap.ui.entrypoint", "entrypoint", attributes );

    EntryPointExtension extensions = EntryPointExtension.findById( id );

    assertNotNull( extensions );
    assertEquals( id, extensions.getId() );
    assertEquals( param, extensions.getParameter() );
    assertEquals( project.getName(), extensions.getProject() );
  }

  @Test
  public void testFindById_withDuplicateIds() throws Exception {
    String id = "entrypoint.id.1";
    // set up plug-in project and add entry point extension with same id twice
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map<String, String> attributes1 = new HashMap<String, String>();
    attributes1.put( "id", id );
    attributes1.put( "class", "class1" );
    attributes1.put( "parameter", "param1" );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint",
                             attributes1 );
    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put( "id", id );
    attributes2.put( "class", "class2" );
    attributes2.put( "parameter", "param2" );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint",
                             attributes2 );

    EntryPointExtension extensions = EntryPointExtension.findById( id );

    assertNotNull( extensions );
    assertEquals( id, extensions.getId() );
    assertEquals( project.getName(), extensions.getProject() );
  }

  @Test
  public void testFindById_withIncompleteExtension() throws Exception {
    // set up plug-in project and add incomplete entry point extension
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint",
                             null );

    EntryPointExtension extensions = EntryPointExtension.findById( "some.id" );

    assertNull( extensions );
  }

  @Test
  public void testFindInWorkspace() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map<String, String> attributes = new HashMap<String, String>();
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

  @Test
  public void testFindInWorkspace_withEmptyWorkspace() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );

    EntryPointExtension[] extensions
      = EntryPointExtension.findInWorkspace( new NullProgressMonitor() );

    assertNotNull( extensions );
    assertEquals( 0, extensions.length );
  }

  @Test
  public void testFindInWorkspace_withIncompleteExtension() throws Exception {
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

  @Test
  public void testFindInPlugins() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map<String, String> attributes1 = new HashMap<String, String>();
    attributes1.put( "id", "findMe" );
    project.createExtension( "org.eclipse.rap.ui.entrypoint",
                             "entrypoint",
                             attributes1 );
    TestPluginProject filteredProject = new TestPluginProject();
    deleteOnTearDown( filteredProject );
    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put( "id", "dontFindMe" );
    filteredProject.createExtension( "org.eclipse.rap.ui.entrypoint",
                                     "entrypoint",
                                     attributes2 );
    String[] plugins = new String[] { project.getName() }; // name == plugin-id
    NullProgressMonitor monitor = new NullProgressMonitor();

    EntryPointExtension[] extensions
      = EntryPointExtension.findInWorkspacePlugins( plugins, monitor );

    assertEquals( 1, extensions.length );
    assertEquals( "findMe", extensions[ 0 ].getId() );
    assertEquals( project.getName(), extensions[ 0 ].getProject() );
  }

  private void deleteOnTearDown( TestPluginProject project ) {
    projectsToDelete.add( project );
  }

}
