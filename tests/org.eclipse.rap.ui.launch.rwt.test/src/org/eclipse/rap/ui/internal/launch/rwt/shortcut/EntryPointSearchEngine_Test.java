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
package org.eclipse.rap.ui.internal.launch.rwt.shortcut;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class EntryPointSearchEngine_Test extends TestCase {

  private TestProject project;
  private EntryPointSearchEngine searchEngine;
  
  public void testSearchWithValidMethod() throws Exception {
    IType type = createEntryPointType();
    IJavaElement method = type.getMethod( "createUI", null );
    assertTrue( method.exists() );  // precondition
    
    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { method } );
    
    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }

  public void testSearchWithWrongMethodSignature() throws Exception {
    String code 
      = "package foo;\n"
      + "class Foo implements IEntryPoint {\n"
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
  
  public void testSearchWithoutIEntryPointInterface() throws Exception {
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
  
  public void testSearchOnInterfaceWithInvalidIEntryPoint() throws Exception {
    String code 
      = "package foo;\n"
      + "interface Foo extends IEntryPoint {\n"
      + "  public int createUI();\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );
    IJavaElement method = type.getMethod( "createUI", null );
    assertTrue( method.exists() );  // precondition

    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { method } );
    
    assertEquals( 0, entryPointTypes.length );
  }
  
  public void testSearchOnMethodWithInvalidIEntryPoint() throws Exception {
    String code
      = "package foo;\n"
      + "interface Foo extends IEntryPoint {\n"
      + "  public int createUI();\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );
    
    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );
    
    assertEquals( 0, entryPointTypes.length );
  }
  
  public void testSearchWithValidIEntryPointClass() throws Exception {
    IType type = createEntryPointType();
    
    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );
    
    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }
  
  public void testSearchNameWithCompilationUnit() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo implements IEntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0\n"
      + "  }\n"
      + "}\n";
    ICompilationUnit compilationUnit = project.createJavaClass( "foo", "Foo", code );
    
    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { compilationUnit } );
    
    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }
  
  public void testSearchWithCompilationUnit() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo implements IEntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0\n"
      + "  }\n"
      + "  class NestedFoo implements IEntryPoint {\n"
      + "    public int createUI() {\n"
      + "      return 0\n"
      + "    }\n"
      + "  }\n"
      + "}\n";
    ICompilationUnit compilationUnit = project.createJavaClass( "foo", "Foo", code );
    
    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { compilationUnit } );
    
    assertEquals( 2, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
    assertEquals( "foo.Foo$NestedFoo", entryPointTypes[ 1 ].getFullyQualifiedName() );
  }
  
  public void testSearchWithNestedEntryPoint() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo {\n"
      + "  class NestedFoo implements IEntryPoint {\n"
      + "    public int createUI() {\n"
      + "      return 0\n"
      + "    }\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );
    
    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );
    
    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo$NestedFoo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }
  
  public void testSearchWithNestedEntryPointRespectsScope() throws Exception {
    String code
      = "package foo;\n"
      + "class Foo implements IEntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0\n"
      + "  }\n"
      + "  class NestedFoo implements IEntryPoint {\n"
      + "    public int createUI() {\n"
      + "      return 0\n"
      + "    }\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo.NestedFoo" );
    
    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { type } );

    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo$NestedFoo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }
  
  public void testSearchWithProject() throws Exception {
    IType type = createEntryPointType();
    IJavaProject javaProject = type.getJavaProject();
    
    IType[] entryPointTypes = searchEngine.search( new IJavaElement[] { javaProject } );

    assertEquals( 1, entryPointTypes.length );
    assertEquals( "foo.Foo", entryPointTypes[ 0 ].getFullyQualifiedName() );
  }

  protected void setUp() throws Exception {
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
  
  protected void tearDown() throws Exception {
    project.delete();
  }

  private IType createEntryPointType() throws CoreException {
    String code
      = "package foo;\n"
      + "class Foo implements IEntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );
    return type;
  }
}
