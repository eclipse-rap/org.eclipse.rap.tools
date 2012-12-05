/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rap.ui.internal.launch.junit;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationTab;
import org.eclipse.pde.ui.launcher.OSGiLauncherTabGroup;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig;
import org.eclipse.rap.ui.internal.launch.tab.MainTab;


// TODO [rh] Could be replaced with org.eclipse.debug.ui.launchConfigurationTabs
//      extension point introduced in 3.3
public final class RAPJUnitLaunchTabGroup extends OSGiLauncherTabGroup {

  public void createTabs( ILaunchConfigurationDialog dialog, String mode ) {
    super.createTabs( dialog, mode );
    // Prepend existing tabs from OSGi launch with 'Main' tab
    setTabs( insertTab( getTabs(), 0, new MainTab() ) );
    setTabs( insertTab( getTabs(), 0, new JUnitLaunchConfigurationTab() ) );
  }

  public void setDefaults( ILaunchConfigurationWorkingCopy config ) {
    super.setDefaults( config );
    config.setAttribute( RAPLaunchConfig.SERVLET_PATH, "/rapjunit" ); //$NON-NLS-1$
  }

  private static ILaunchConfigurationTab[] insertTab( ILaunchConfigurationTab[] tabs,
                                                      int position,
                                                      ILaunchConfigurationTab newTab )
  {
    ILaunchConfigurationTab[] result = new ILaunchConfigurationTab[ tabs.length + 1 ];
    int offset = 0;
    for( int i = 0; i < result.length; i++ ) {
      if( i == position ) {
        result[ i ] = newTab;
        offset = -1;
      } else {
        result[ i ] = tabs[ i + offset ];
      }
    }
    return result;
  }
}
