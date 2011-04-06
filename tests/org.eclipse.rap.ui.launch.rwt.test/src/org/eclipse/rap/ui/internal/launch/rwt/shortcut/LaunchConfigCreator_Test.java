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

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IType;
import org.eclipse.rap.ui.internal.launch.rwt.config.BrowserMode;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class LaunchConfigCreator_Test extends TestCase {
  
  private TestProject project;

  public void testProjectOfLaunchConfigCreatedByFromType() throws CoreException {
    String code
      = "package foo;\n"
      + "class Foo implements IEntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );
    
    ILaunchConfiguration launchConfig = LaunchConfigCreator.fromType( type );
    
    RWTLaunchConfig rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    assertEquals( "Foo", launchConfig.getName() );
    assertFalse( rwtLaunchConfig.getUseWebXml() );
    assertEquals( project.getName(), rwtLaunchConfig.getProjectName() );
    assertEquals( "foo.Foo", rwtLaunchConfig.getEntryPoint() );
    assertTrue( rwtLaunchConfig.getOpenBrowser() );
    assertEquals( BrowserMode.INTERNAL, rwtLaunchConfig.getBrowserMode() );
  }
  
  protected void setUp() throws Exception {
    project = new TestProject();
  }
  
  protected void tearDown() throws Exception {
    project.delete();
  }
}
