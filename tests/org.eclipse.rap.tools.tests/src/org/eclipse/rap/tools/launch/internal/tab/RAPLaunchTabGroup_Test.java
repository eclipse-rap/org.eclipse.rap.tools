/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.launch.internal.tab;

import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationDialog;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.rap.tools.internal.tests.Fixture;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class RAPLaunchTabGroup_Test {

  private static final String JETTY_LOG_LEVEL
    = "-Dorg.eclipse.equinox.http.jetty.log.stderr.threshold=info";
  private static final String VM_ARGS
    = IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;

  @Test
  public void testDefaultVMArgumentJettyLogLevel() throws CoreException {
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
    RAPLaunchTabGroup launchTabGroup = new RAPLaunchTabGroup();
    LaunchConfigurationDialog dialog = new LaunchConfigurationDialog( null, config, null );
    launchTabGroup.createTabs( dialog, "run" );
    launchTabGroup.setDefaults( config );

    String vmArgs = config.getAttribute( VM_ARGS, "" );

    assertTrue( vmArgs.indexOf( JETTY_LOG_LEVEL ) != -1 );
  }

}
