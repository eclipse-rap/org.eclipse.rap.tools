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
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IType;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.shortcut.LaunchConfigFinder.LaunchConfigSelector;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class LaunchConfigFinder_Test extends TestCase {

  private static class TestLaunchConfigSelector implements LaunchConfigSelector {
    boolean wasInvoked;
    public ILaunchConfiguration select( ILaunchConfiguration[] launchConfigs ) {
      wasInvoked = true;
      return launchConfigs[ 0 ];
    }
  }
  
  private LaunchConfigFinder launchConfigFinder;
  private TestLaunchConfigSelector launchConfigSelector;
  private TestProject project;
  
  public void testForTypeWhenMatchingLaunchConfigIsPresent() throws CoreException {
    IType entryPointType = createEntryPointType();
    RWTLaunchConfig launchConfig = createLaunchConfigFromType( entryPointType );
    
    ILaunchConfiguration foundConfig = launchConfigFinder.forType( entryPointType );
    
    assertTrue( foundConfig.contentsEqual( launchConfig.getUnderlyingLaunchConfig() ) );
    assertFalse( launchConfigSelector.wasInvoked );
  }

  public void testForTypeWhenMultipleMatchingLaunchConfigsArePresent() throws CoreException {
    IType entryPointType = createEntryPointType();
    RWTLaunchConfig launchConfig1 = createLaunchConfigFromType( entryPointType );
    createLaunchConfigFromType( entryPointType );
    
    ILaunchConfiguration foundConfig = launchConfigFinder.forType( entryPointType );
    
    assertTrue( foundConfig.contentsEqual( launchConfig1.getUnderlyingLaunchConfig() ) );
    assertTrue( launchConfigSelector.wasInvoked );
  }
  
  public void testForTypeWhenLaunchConfigWithoutProjectIsPresent() throws CoreException {
    IType entryPointType = createEntryPointType();
    ILaunchConfigurationWorkingCopy launchConfig = Fixture.createRWTLaunchConfig();
    RWTLaunchConfig rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    rwtLaunchConfig.setUseWebXml( false );
    rwtLaunchConfig.setEntryPoint( entryPointType.getFullyQualifiedName() );
    launchConfig.doSave();
    
    ILaunchConfiguration foundConfig = launchConfigFinder.forType( entryPointType );
    
    assertNull( foundConfig );
  }

  public void testForTypeWhenNoLaunchConfigIs() throws CoreException {
    IType entryPointType = createEntryPointType();
    
    ILaunchConfiguration foundConfig = launchConfigFinder.forType( entryPointType );
    
    assertNull( foundConfig );
  }
  
  public void testForTypeWhenSimilarWebXmlConfigIsPresent() throws CoreException {
    IType entryPointType = createEntryPointType();
    ILaunchConfigurationWorkingCopy launchConfig = Fixture.createRWTLaunchConfig();
    RWTLaunchConfig rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    rwtLaunchConfig.setProjectName( project.getName() );
    rwtLaunchConfig.setEntryPoint( entryPointType.getFullyQualifiedName() );
    rwtLaunchConfig.setUseWebXml( true );

    ILaunchConfiguration foundConfig = launchConfigFinder.forType( entryPointType );
    
    assertNull( foundConfig );
  }
  
  protected void setUp() throws Exception {
    project = new TestProject();
    launchConfigSelector = new TestLaunchConfigSelector();
    launchConfigFinder = new LaunchConfigFinder( launchConfigSelector );
  }
  
  protected void tearDown() throws Exception {
    Fixture.deleteAllRWTLaunchConfigs();
    project.delete();
  }

  private IType createEntryPointType() throws CoreException {
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

  private static RWTLaunchConfig createLaunchConfigFromType( IType entryPointType )
    throws CoreException
  {
    ILaunchConfigurationWorkingCopy launchConfig = Fixture.createRWTLaunchConfig();
    RWTLaunchConfig result = new RWTLaunchConfig( launchConfig );
    result.setUseWebXml( false );
    result.setProjectName( entryPointType.getJavaProject().getElementName() );
    result.setEntryPoint( entryPointType.getFullyQualifiedName() );
    launchConfig.doSave();
    return result;
  }
}
