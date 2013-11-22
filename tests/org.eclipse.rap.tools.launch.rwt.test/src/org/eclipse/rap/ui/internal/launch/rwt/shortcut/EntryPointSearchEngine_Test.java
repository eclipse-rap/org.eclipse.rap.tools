/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.shortcut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class EntryPointSearchEngine_Test {

  private TestProject project;
  private EntryPointSearchEngine searchEngine;

  @Before
  public void setUp() throws Exception {
    IRunnableContext runnableContext = new IRunnableContext() {
      public void run( boolean fork,
                       boolean cancelable,
                       IRunnableWithProgress runnable )
        throws InvocationTargetException, InterruptedException
      {
        runnable.run( new NullProgressMonitor() );
      }
    };
    searchEngine = new EntryPointSearchEngine( runnableContext );
    project = new TestProject();
  }

  @After
  public void tearDown() throws Exception {
    project.delete();
  }

  @Test
  public void testSearch_withValidMethod() throws Exception {
    IType type = createEntryPointType();
    IJavaElement method = type.getMethod( "createUI", null );
    assertTrue( method.exists() );  // precondition

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { method } );

    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }

  @Test
  public void testSearch_withWrongMethodSignature() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo implements EntryPoint {\n"
      + "  public int createUI( Object arg0 ) {\n"
      + "    return 0;\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );
    IJavaElement method = type.getMethod( "createUI", new String[] { "QObject;" } );
    assertTrue( method.exists() );  // precondition

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { method } );

    assertEquals( 0, entryPointTypes.length );
  }

  @Test
  public void testSearch_withoutEntryPointInterface() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo {\n"
      + "  public int createUI() {\n"
      + "    return 0;\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );
    IJavaElement method = type.getMethod( "createUI", null );
    assertTrue( method.exists() );  // precondition

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { method } );

    assertEquals( 0, entryPointTypes.length );
  }

  @Test
  public void testSearch_onInterfaceWithInvalidEntryPoint() throws Exception {
    String code
      = "package foo;\n"
      + "interface Foo extends EntryPoint {\n"
      + "  public int createUI();\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );
    IJavaElement method = type.getMethod( "createUI", null );
    assertTrue( method.exists() );  // precondition

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { method } );

    assertEquals( 0, entryPointTypes.length );
  }

  @Test
  public void testSearch_onMethodWithInvalidEntryPoint() throws Exception {
    String code
      = "package foo;\n"
      + "interface Foo extends EntryPoint {\n"
      + "  public int createUI();\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );

    assertEquals( 0, entryPointTypes.length );
  }

  @Test
  public void testSearch_withValidEntryPointClass() throws Exception {
    IType type = createEntryPointType();

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );

    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }

  @Test
  public void testSearch_withValidIEntryPointClass() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo implements IEntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0;\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );

    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }

  @Test
  public void testSearch_nameWithCompilationUnit() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo implements EntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0;\n"
      + "  }\n"
      + "}\n";
    ICompilationUnit compilationUnit = project.createJavaClass( "foo", "Foo", code );

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { compilationUnit } );

    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }

  @Test
  public void testSearch_withCompilationUnit() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo implements EntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0;\n"
      + "  }\n"
      + "  class NestedFoo implements EntryPoint {\n"
      + "    public int createUI() {\n"
      + "      return 0;\n"
      + "    }\n"
      + "  }\n"
      + "}\n";
    ICompilationUnit compilationUnit = project.createJavaClass( "foo", "Foo", code );

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { compilationUnit } );

    assertEquals( 2, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
    assertEquals( "foo.Foo$NestedFoo", entryPointTypes[ 1 ].getFullyQualifiedName() );
  }

  @Test
  public void testSearch_withNestedEntryPoint() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo {\n"
      + "  class NestedFoo implements EntryPoint {\n"
      + "    public int createUI() {\n"
      + "      return 0;\n"
      + "    }\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );

    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo$NestedFoo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }

  @Test
  public void testSearch_withNestedEntryPointRespectsScope() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo implements EntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0;\n"
      + "  }\n"
      + "  class NestedFoo implements EntryPoint {\n"
      + "    public int createUI() {\n"
      + "      return 0;\n"
      + "    }\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo.NestedFoo" );

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );

    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo$NestedFoo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }

  @Test
  public void testSearch_withExtendedAbstractEntryPoint() throws Exception {
    String code
    = "package foo;\n"
        + "class Foo extends AbstractEntryPoint {\n"
        + "  protected void createContents( Composite parent ) {\n"
        + "  }\n"
        + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );

    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }

  @Test
  public void testSearch_withoutExtendsAbstractEntryPoint() throws Exception {
    String code
    = "package foo;\n"
        + "class Foo {\n"
        + "  protected void createContents( Composite parent ) {\n"
        + "  }\n"
        + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );

    assertEquals( 0, entryPointTypes.length );
  }

  @Test
  public void testSearch_withAbstractClass() throws Exception {
    String code
    = "package foo;\n"
        + "abstract class Foo implements EntryPoint {\n"
        + "  public int createUI() {\n"
        + "    return 0;\n"
        + "  }\n"
        + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );

    assertEquals( 0, entryPointTypes.length );
  }

  @Test
  public void testSearch_withProject() throws Exception {
    IType type = createEntryPointType();
    IJavaProject javaProject = type.getJavaProject();

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { javaProject } );

    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }

  private IType createEntryPointType() throws CoreException {
    String code
      = "package foo;\n"
      + "class Foo implements EntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0;\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    return project.getJavaProject().findType( "foo.Foo" );
  }

}
