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
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class RWTLaunchShortcut_Test extends TestCase {

  private static class TestableRWTLaunchShortcut extends RWTLaunchShortcut {
    protected ILaunchConfiguration findLaunchConfiguration( IType type,
                                                            ILaunchConfigurationType configType )
    {
      return super.findLaunchConfiguration( type, configType );
    }
  }
  
  private TestableRWTLaunchShortcut launchShortcut;

  public void testGetConfigurationType() {
    ILaunchConfigurationType type = launchShortcut.getConfigurationType();
    assertEquals( RWTLaunchConfig.getType(), type );
  }
  
  public void testGetEditorEmptyMessage() {
    String message = launchShortcut.getEditorEmptyMessage();
    assertNotNull( message );
    assertTrue( message.length() > 0 );
  }

  public void testGetSelectionEmptyMessage() {
    String message = launchShortcut.getSelectionEmptyMessage();
    assertNotNull( message );
    assertTrue( message.length() > 0 );
  }
  
  public void testGetTypeSelectionTitle() {
    String title = launchShortcut.getTypeSelectionTitle();
    assertNotNull( title );
    assertTrue( title.length() > 0 );
  }
  
  public void testFindTypesWithNonJavaElements() throws Exception {
    IRunnableContext runnableContext = createRunnableContext();
    IType[] types = launchShortcut.findTypes( new Object[] { new Object() }, runnableContext );
  
    assertEquals( 0, types.length );
  }

  public void testCreateConfigurationProducesComparableLaunchConfig() throws CoreException {
    TestProject project = new TestProject();
    IType entryPointType = createEntryPointType( project );
    
    ILaunchConfiguration createdConfig = launchShortcut.createConfiguration( entryPointType );
    ILaunchConfiguration foundConfig
      = launchShortcut.findLaunchConfiguration( entryPointType, RWTLaunchConfig.getType() );
    
    assertEquals( createdConfig, foundConfig );
  }

  protected void setUp() throws Exception {
    launchShortcut = new TestableRWTLaunchShortcut();
  }
  
  protected void tearDown() throws Exception {
    TestProject.deleteAll();
    Fixture.deleteAllRWTLaunchConfigs();
  }

  private static IType createEntryPointType( TestProject project ) throws CoreException {
    String code
      = "package foo;\n"
      + "class Foo {\n"
      + "  public int createUI() {\n"
      + "    return 0\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    return project.getJavaProject().findType( "foo.Foo" );
  }

  private static IRunnableContext createRunnableContext() {
    return new IRunnableContext() {
      public void run( boolean fork,
                       boolean cancelable,
                       IRunnableWithProgress runnable )
        throws InvocationTargetException, InterruptedException
      {
        runnable.run( new NullProgressMonitor() );
      }
    };
  }
}
