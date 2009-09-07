/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.tab;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationDialog;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.rap.ui.tests.Fixture;

public class RAPLaunchTabGroup_Test extends TestCase {
  
  private static final String RWT_COMPRESSION_ON 
    = "-Dorg.eclipse.rwt.compression=true"; //$NON-NLS-1$
  private static final String RWT_COMPRESSION_OFF 
    = "-Dorg.eclipse.rwt.compression=false"; //$NON-NLS-1$
  private static final String VM_ARGS 
    = IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;

  public void testCheckCompressionVMArgumentAdded() throws CoreException {
    // prepare the launch configuration
    ILaunchConfigurationWorkingCopy config = Fixture.createRAPLaunchConfig();
 // prepare the launch tab group
    RAPLaunchTabGroup launchTabGroup = new RAPLaunchTabGroup();
    LaunchConfigurationDialog dialog 
      = new LaunchConfigurationDialog( null, config, null );
    launchTabGroup.createTabs( dialog, "run" );
    // the method under test
    launchTabGroup.setDefaults( config );
    // check the VM args 
    String vmArgs = config.getAttribute( VM_ARGS, "" );
    assertTrue( vmArgs.indexOf( RWT_COMPRESSION_ON) != -1 );
    assertTrue( vmArgs.indexOf( RWT_COMPRESSION_OFF) == -1 );    
  }
}
