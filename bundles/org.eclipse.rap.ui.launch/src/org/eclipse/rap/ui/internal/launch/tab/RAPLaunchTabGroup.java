/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.tab;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.pde.ui.launcher.OSGiLauncherTabGroup;
import org.eclipse.rap.ui.internal.launch.Activator;


// TODO [rh] Could be replaced with org.eclipse.debug.ui.launchConfigurationTabs
//      extension point introduced in 3.3
public final class RAPLaunchTabGroup extends OSGiLauncherTabGroup
{

  private static final String NEW_LINE = "\n"; //$NON-NLS-1$
  private static final String ATTR_VM_ARGUMENTS
    = IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;
  private static final String RWT_COMPRESSION_ON
    = "-Dorg.eclipse.rwt.compression=true"; //$NON-NLS-1$
  private static final String RWT_COMPRESSION_OFF
    = "-Dorg.eclipse.rwt.compression=false"; //$NON-NLS-1$
  private static final String JETTY_LOG_LEVEL
    = "-Dorg.eclipse.equinox.http.jetty.log.stderr.threshold=info"; //$NON-NLS-1$

  public void createTabs( final ILaunchConfigurationDialog dialog,
                          final String mode )
  {
    super.createTabs( dialog, mode );
    // Prepend existing tabs from OSGi launch with 'Main' tab
    setTabs( insertTab( getTabs(), 0, new MainTab() ) );
  }

  private static ILaunchConfigurationTab[] insertTab(
    final ILaunchConfigurationTab[] tabs,
    final int position,
    final ILaunchConfigurationTab newTab )
  {
    ILaunchConfigurationTab[] result
    = new ILaunchConfigurationTab[ tabs.length + 1 ];
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

  public void setDefaults( final ILaunchConfigurationWorkingCopy config ) {
    super.setDefaults( config );
    String vmArguments = ""; //$NON-NLS-1$
    try {
      vmArguments = config.getAttribute( ATTR_VM_ARGUMENTS, "" ); //$NON-NLS-1$
    } catch( CoreException e ) {
      Activator.getDefault().getLog().log( e.getStatus() );
    }
    vmArguments = appendRwtCompression( vmArguments );
    vmArguments = appendJettyLogLevel( vmArguments );
    config.setAttribute( ATTR_VM_ARGUMENTS, vmArguments );
  }
  
  private static String appendRwtCompression( final String vmArguments ) {
    String result = vmArguments;
    if(    vmArguments.indexOf( RWT_COMPRESSION_ON ) == -1
        && vmArguments.indexOf( RWT_COMPRESSION_OFF ) == -1 )
    {
      if( result.length() > 0 ) {
        result += NEW_LINE;
      }
      result += RWT_COMPRESSION_ON;
    }
    return result;
  }
  
  private static String appendJettyLogLevel( final String vmArguments ) {
    String result = vmArguments;
    if( result.length() > 0 ) {
      result += NEW_LINE;
    }
    result += JETTY_LOG_LEVEL;
    return result;
  }
}
