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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.*;
import org.eclipse.jdt.core.IType;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;


public class LaunchConfigCreator {

  static ILaunchConfiguration create( IType type, LaunchTarget launchTarget, String servletPath )
    throws CoreException
  {
    ILaunchConfigurationType configType = RWTLaunchConfig.getType();
    String name = launchConfigNameFromType( type );
    ILaunchConfigurationWorkingCopy launchConfig = configType.newInstance( null, name );
    launchConfig.setMappedResources( new IResource[]{ type.getUnderlyingResource() } );
    RWTLaunchConfig.setDefaults( launchConfig );
    RWTLaunchConfig rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    rwtLaunchConfig.setProjectName( getProjectName( type ) );
    rwtLaunchConfig.setLaunchTarget( launchTarget );
    if( LaunchTarget.ENTRY_POINT.equals( launchTarget ) ) {
      rwtLaunchConfig.setEntryPoint( type.getFullyQualifiedName() );
    } else {
      rwtLaunchConfig.setAppConfig( type.getFullyQualifiedName() );
    }
    if( servletPath != null ) {
      rwtLaunchConfig.setServletPath( servletPath );
    }
    return launchConfig.doSave();
  }

  private static String launchConfigNameFromType( IType type ) {
    String qualifiedName = type.getTypeQualifiedName( '.' ); //$NON-NLS-1$
    return getLaunchManager().generateLaunchConfigurationName( qualifiedName );
  }

  private static String getProjectName( IType type ) {
    return type.getJavaProject().getElementName();
  }

  private static ILaunchManager getLaunchManager() {
    return DebugPlugin.getDefault().getLaunchManager();
  }

  private LaunchConfigCreator() {
    // prevent instantiation
  }

}
