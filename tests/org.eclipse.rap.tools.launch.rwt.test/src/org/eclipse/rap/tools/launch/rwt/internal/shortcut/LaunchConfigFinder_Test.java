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

import static org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget.APP_CONFIG;
import static org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget.ENTRY_POINT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IType;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.rap.tools.launch.rwt.internal.shortcut.LaunchConfigFinder.LaunchConfigSelector;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LaunchConfigFinder_Test {

  private LaunchConfigFinder launchConfigFinder;
  private TestLaunchConfigSelector launchConfigSelector;
  private TestProject project;

  @Before
  public void setUp() throws Exception {
    project = new TestProject();
    launchConfigSelector = new TestLaunchConfigSelector();
    launchConfigFinder = new LaunchConfigFinder( launchConfigSelector );
  }

  @After
  public void tearDown() throws Exception {
    Fixture.deleteAllRWTLaunchConfigs();
    project.delete();
  }

  @Test
  public void testForType_whenMatchingEntryPointLaunchConfigIsPresent() throws CoreException {
    IType entryPointType = createEntryPointType();
    RWTLaunchConfig launchConfig = createLaunchConfigFromType( entryPointType, ENTRY_POINT );

    ILaunchConfiguration foundConfig = launchConfigFinder.forType( entryPointType );

    assertTrue( foundConfig.contentsEqual( launchConfig.getUnderlyingLaunchConfig() ) );
    assertFalse( launchConfigSelector.wasInvoked );
  }

  @Test
  public void testForType_whenMatchingAppConfigLaunchConfigIsPresent() throws CoreException {
    IType appConfigType = createAppConfigType();
    RWTLaunchConfig launchConfig = createLaunchConfigFromType( appConfigType, APP_CONFIG );

    ILaunchConfiguration foundConfig = launchConfigFinder.forType( appConfigType );

    assertTrue( foundConfig.contentsEqual( launchConfig.getUnderlyingLaunchConfig() ) );
    assertFalse( launchConfigSelector.wasInvoked );
  }

  @Test
  public void testForType_whenMultipleMatchingLaunchConfigsArePresent() throws CoreException {
    IType entryPointType = createEntryPointType();
    RWTLaunchConfig launchConfig1 = createLaunchConfigFromType( entryPointType, ENTRY_POINT );
    createLaunchConfigFromType( entryPointType, ENTRY_POINT );

    ILaunchConfiguration foundConfig = launchConfigFinder.forType( entryPointType );

    assertTrue( foundConfig.contentsEqual( launchConfig1.getUnderlyingLaunchConfig() ) );
    assertTrue( launchConfigSelector.wasInvoked );
  }

  @Test
  public void testForType_whenLaunchConfigWithoutProjectIsPresent() throws CoreException {
    IType entryPointType = createEntryPointType();
    ILaunchConfigurationWorkingCopy launchConfig = Fixture.createRWTLaunchConfig();
    RWTLaunchConfig rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    rwtLaunchConfig.setLaunchTarget( LaunchTarget.ENTRY_POINT );
    rwtLaunchConfig.setEntryPoint( entryPointType.getFullyQualifiedName() );
    launchConfig.doSave();

    ILaunchConfiguration foundConfig = launchConfigFinder.forType( entryPointType );

    assertNull( foundConfig );
  }

  @Test
  public void testForType_whenNoLaunchConfigIs() throws CoreException {
    IType entryPointType = createEntryPointType();

    ILaunchConfiguration foundConfig = launchConfigFinder.forType( entryPointType );

    assertNull( foundConfig );
  }

  @Test
  public void testForType_whenSimilarWebXmlConfigIsPresent() throws CoreException {
    IType entryPointType = createEntryPointType();
    ILaunchConfigurationWorkingCopy launchConfig = Fixture.createRWTLaunchConfig();
    RWTLaunchConfig rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    rwtLaunchConfig.setProjectName( project.getName() );
    rwtLaunchConfig.setEntryPoint( entryPointType.getFullyQualifiedName() );
    rwtLaunchConfig.setLaunchTarget( LaunchTarget.WEB_XML );

    ILaunchConfiguration foundConfig = launchConfigFinder.forType( entryPointType );

    assertNull( foundConfig );
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

  private IType createAppConfigType() throws CoreException {
    String code
      = "package foo;\n"
      + "class Bar implements ApplicationConfiguration {\n"
      + "  public void configure( Application application ) {\n"
      + "  }\n"
      + "}\n";
    project.createJavaClass( "foo", "Bar", code );
    return project.getJavaProject().findType( "foo.Bar" );
  }

  private static RWTLaunchConfig createLaunchConfigFromType( IType type, LaunchTarget launchTarget )
    throws CoreException
  {
    ILaunchConfigurationWorkingCopy launchConfig = Fixture.createRWTLaunchConfig();
    RWTLaunchConfig result = new RWTLaunchConfig( launchConfig );
    result.setLaunchTarget( launchTarget );
    result.setProjectName( type.getJavaProject().getElementName() );
    if( ENTRY_POINT.equals( launchTarget ) ) {
      result.setEntryPoint( type.getFullyQualifiedName() );
    } else if( APP_CONFIG.equals( launchTarget ) ) {
      result.setAppConfig( type.getFullyQualifiedName() );
    }
    launchConfig.doSave();
    return result;
  }

  private static class TestLaunchConfigSelector implements LaunchConfigSelector {
    boolean wasInvoked;
    public ILaunchConfiguration select( ILaunchConfiguration[] launchConfigs ) {
      wasInvoked = true;
      return launchConfigs[ 0 ];
    }
  }

}
