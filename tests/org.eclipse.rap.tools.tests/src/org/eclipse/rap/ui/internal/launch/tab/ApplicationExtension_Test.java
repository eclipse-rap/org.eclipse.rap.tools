/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
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


public class ApplicationExtension_Test {

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
    ApplicationExtension extension = ApplicationExtension.findById( "some.id" );

    assertNull( extension );
  }

  @Test
  public void testFindById_withSingleProject() throws Exception {
    String id = "application.id.1";
    // set up plug-in project with entry point extension
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put( "cardinality", "singleton-global" );
    attributes.put( "thread", "main" );
    attributes.put( "visible", "true" );
    project.createExtensionWithExtensionId(
                             "org.eclipse.core.runtime.applications",
                             "application",
                             attributes,
                             id );

    ApplicationExtension extension = ApplicationExtension.findById( id );

    assertNotNull( extension );
    assertEquals( id, extension.getId() );
    assertEquals( project.getName(), extension.getProject() );
  }

  @Test
  public void testFindById_withDuplicateIds() throws Exception {
    String id = "entrypoint.id.1";
    // set up plug-in project and add entry point extension with same id twice
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map<String, String> attributes1 = new HashMap<String, String>();
    attributes1.put( "cardinality", "singleton-global" );
    attributes1.put( "thread", "main" );
    attributes1.put( "visible", "true" );
    project.createExtensionWithExtensionId( "org.eclipse.core.runtime.applications",
                                            "application",
                                            attributes1,
                                            id );
    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put( "cardinality", "singleton-global" );
    attributes2.put( "thread", "main" );
    attributes2.put( "visible", "true" );

    project.createExtensionWithExtensionId( "org.eclipse.core.runtime.applications",
                                            "application",
                                            attributes2,
                                            id );

    ApplicationExtension extension = ApplicationExtension.findById( id );

    assertNotNull( extension );
    assertEquals( id, extension.getId() );
    assertEquals( project.getName(), extension.getProject() );
  }

  @Test
  public void testFindById_withIncompleteExtension() throws Exception {
    // set up plug-in project and add incomplete entry point extension
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    project.createExtension( "org.eclipse.core.runtime.applications",
                             "application",
                             null );

    ApplicationExtension extension = ApplicationExtension.findById( "some.id" );

    assertNull( extension );
  }

  @Test
  public void testFindInWorkspace() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put( "cardinality", "singleton-global" );
    attributes.put( "thread", "main" );
    attributes.put( "visible", "true" );
    String id = "application.point.1";
    project.createExtensionWithExtensionId( "org.eclipse.core.runtime.applications",
                                            "application",
                                            attributes,
                                            id );

    ApplicationExtension[] extensions
      = ApplicationExtension.findInWorkspace( new NullProgressMonitor() );

    assertNotNull( extensions );
    assertEquals( 1, extensions.length );
    assertEquals( id, extensions[ 0 ].getId() );
  }

  @Test
  public void testFindInWorkspace_withEmptyWorkspace() throws Exception {
    TestPluginProject project = new TestPluginProject();
    deleteOnTearDown( project );

    ApplicationExtension[] extensions
      = ApplicationExtension.findInWorkspace( new NullProgressMonitor() );

    assertNotNull( extensions );
    assertEquals( 0, extensions.length );
  }

  @Test
  public void testFindInWorkspace_withIncompleteExtension() throws Exception {
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

  @Test
  public void testFindInWorkspacePlugins() throws Exception {
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
    Map<String, String> attributes = new HashMap<String, String>();
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

  private void deleteOnTearDown( TestPluginProject project ) {
    projectsToDelete.add( project );
  }

}
