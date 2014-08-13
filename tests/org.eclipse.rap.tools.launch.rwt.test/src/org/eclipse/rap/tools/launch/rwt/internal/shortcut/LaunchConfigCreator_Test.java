/*******************************************************************************
 * Copyright (c) 2011, 2014 Rüdiger Herrmann and others.
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

import static org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget.APP_CONFIG;
import static org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget.ENTRY_POINT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IType;
import org.eclipse.rap.tools.launch.rwt.internal.config.BrowserMode;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;
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
  public void testFromType_createsLaunchConfig_withEntryPoint() throws CoreException {
    String code
      = "package foo;\n"
      + "class Foo implements EntryPoint {\n"
      + "  public int createUI() {\n"
      + "    return 0;\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Foo", code );
    IType type = project.getJavaProject().findType( "foo.Foo" );

    ILaunchConfiguration launchConfig = LaunchConfigCreator.create( type, ENTRY_POINT, "/foo" );

    RWTLaunchConfig rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    assertEquals( "Foo", launchConfig.getName() );
    assertEquals( LaunchTarget.ENTRY_POINT, rwtLaunchConfig.getLaunchTarget() );
    assertEquals( project.getName(), rwtLaunchConfig.getProjectName() );
    assertEquals( "foo.Foo", rwtLaunchConfig.getEntryPoint() );
    assertEquals( "", rwtLaunchConfig.getAppConfig() );
    assertEquals( "/foo", rwtLaunchConfig.getServletPath() );
    assertTrue( rwtLaunchConfig.getOpenBrowser() );
    assertEquals( BrowserMode.INTERNAL, rwtLaunchConfig.getBrowserMode() );
  }

  @Test
  public void testFromType_createsLaunchConfig_withAppConfig() throws CoreException {
    String code
      = "package foo;\n"
      + "class Bar implements ApplicationConfiguration {\n"
      + "  public void configure( Application application ) {\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Bar", code );
    IType type = project.getJavaProject().findType( "foo.Bar" );

    ILaunchConfiguration launchConfig = LaunchConfigCreator.create( type, APP_CONFIG, "/foo" );

    RWTLaunchConfig rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    assertEquals( "Bar", launchConfig.getName() );
    assertEquals( LaunchTarget.APP_CONFIG, rwtLaunchConfig.getLaunchTarget() );
    assertEquals( project.getName(), rwtLaunchConfig.getProjectName() );
    assertEquals( "", rwtLaunchConfig.getEntryPoint() );
    assertEquals( "foo.Bar", rwtLaunchConfig.getAppConfig() );
    assertEquals( "/foo", rwtLaunchConfig.getServletPath() );
    assertTrue( rwtLaunchConfig.getOpenBrowser() );
    assertEquals( BrowserMode.INTERNAL, rwtLaunchConfig.getBrowserMode() );
  }

}
