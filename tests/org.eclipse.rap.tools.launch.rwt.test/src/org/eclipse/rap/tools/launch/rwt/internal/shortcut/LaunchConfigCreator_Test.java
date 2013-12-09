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
package org.eclipse.rap.tools.launch.rwt.internal.shortcut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IType;
import org.eclipse.rap.tools.launch.rwt.internal.config.BrowserMode;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.rap.tools.launch.rwt.internal.shortcut.LaunchConfigCreator;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LaunchConfigCreator_Test {

  private TestProject project;

  @Before
  public void setUp() throws Exception {
    project = new TestProject();
  }

  @After
  public void tearDown() throws Exception {
    project.delete();
  }

  @Test
  public void testFromType_createsLaunchConfig() throws CoreException {
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
    assertEquals( LaunchTarget.ENTRY_POINT, rwtLaunchConfig.getLaunchTarget() );
    assertEquals( project.getName(), rwtLaunchConfig.getProjectName() );
    assertEquals( "foo.Foo", rwtLaunchConfig.getEntryPoint() );
    assertTrue( rwtLaunchConfig.getOpenBrowser() );
    assertEquals( BrowserMode.INTERNAL, rwtLaunchConfig.getBrowserMode() );
  }

}
