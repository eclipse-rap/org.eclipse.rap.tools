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
package org.eclipse.rap.tools.launch.rwt.internal.tab;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.tab.RWTLaunchTab;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.eclipse.swt.widgets.Composite;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RWTLaunchTab_Test {

  private TestableRWTLaunchTab launchTab;
  private ILaunchConfigurationWorkingCopy launchConfig;

  @Before
  public void setUp() throws Exception {
    launchTab = new TestableRWTLaunchTab();
    launchConfig = Fixture.createRWTLaunchConfig();
  }

  @After
  public void tearDown() throws Exception {
    launchConfig.delete();
  }

  @Test
  public void testPerformApply() {
    launchTab.setDirty( false );
    launchTab.performApply( launchConfig );

    boolean wasInvoked
      = launchTab.wasInvoked( TestableRWTLaunchTab.METHOD_PERFORM_APPLY,
                              launchConfig );
    assertTrue( wasInvoked );
    assertTrue( launchTab.isDirty() );
  }

  @Test
  public void testInitializeFrom() {
    launchTab.initializeFrom( launchConfig );

    boolean wasInvoked
      = launchTab.wasInvoked( TestableRWTLaunchTab.METHOD_INITIALIZE_FROM,
                              launchConfig );
    assertTrue( wasInvoked );
    assertFalse( launchTab.isDirty() );
  }

  @Test
  public void testValidate() {
    new RWTLaunchConfig( launchConfig ).setProjectName( "does.not.exist" );
    launchTab.performApply( launchConfig );

    assertFalse( launchTab.isValid( launchConfig ) );
  }

  private static class MethodInvocation {
    final String methodName;
    final RWTLaunchConfig launchConfig;

    public MethodInvocation( String methodName, RWTLaunchConfig launchConfig ) {
      this.methodName = methodName;
      this.launchConfig = launchConfig;
    }
  }

  private static class TestableRWTLaunchTab extends RWTLaunchTab {
    static final String METHOD_PERFORM_APPLY = "performApply";
    static final String METHOD_INITIALIZE_FROM = "initializeFrom";

    private final List<MethodInvocation> invokedMethods;

    TestableRWTLaunchTab() {
      invokedMethods = new ArrayList<MethodInvocation>();
    }

    boolean wasInvoked( String methodName, ILaunchConfiguration launchConfig ) {
      boolean result = false;
      Iterator iter = invokedMethods.iterator();
      while( !result && iter.hasNext() ) {
        MethodInvocation methodInvokation = ( MethodInvocation )iter.next();
        boolean nameEquals = methodInvokation.methodName.equals( methodName );
        ILaunchConfiguration underlyingLaunchConfig
          = methodInvokation.launchConfig.getUnderlyingLaunchConfig();
        boolean launchConfigEquals = underlyingLaunchConfig == launchConfig;
        result = nameEquals && launchConfigEquals;
      }
      return result;
    }

    public final void createControl( Composite parent ) {
      throw new UnsupportedOperationException();
    }

    public final String getName() {
      return "Test Launch Tab";
    }

    @Override
    public void setDirty( boolean dirty ) {
      super.setDirty( dirty );
    }

    @Override
    public boolean isDirty() {
      return super.isDirty();
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
      super.setErrorMessage( errorMessage );
    }

    @Override
    public void initializeFrom( RWTLaunchConfig launchConfig ) {
      addMethodInvocation( METHOD_INITIALIZE_FROM, launchConfig );
    }

    @Override
    public void performApply( RWTLaunchConfig launchConfig ) {
      addMethodInvocation( METHOD_PERFORM_APPLY, launchConfig );
    }

    private void addMethodInvocation( String method, RWTLaunchConfig config ) {
      invokedMethods.add( new MethodInvocation( method, config ) );
    }
  }

}
