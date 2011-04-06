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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.*;
import org.eclipse.jdt.core.IType;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;


public class LaunchConfigCreator {

  public static ILaunchConfiguration fromType( IType type ) throws CoreException {
    return new LaunchConfigCreator( type ).create();
  }

  private final IType type;
  private ILaunchConfigurationWorkingCopy launchConfig;
  
  private LaunchConfigCreator( IType type ) {
    this.type = type;
  }

  private ILaunchConfiguration create() throws CoreException  {
    ILaunchConfigurationType configType = RWTLaunchConfig.getType();
    String name = launchConfigNameFromType();
    launchConfig = configType.newInstance( null, name );
    launchConfig.setMappedResources( new IResource[]{ type.getUnderlyingResource() } );
    RWTLaunchConfig.setDefaults( launchConfig );
    RWTLaunchConfig rwtLaunchConfig = new RWTLaunchConfig( launchConfig );
    rwtLaunchConfig.setProjectName( getProjectName() );
    rwtLaunchConfig.setEntryPoint( type.getFullyQualifiedName() );
    return launchConfig.doSave();
  }

  private String launchConfigNameFromType() {
    String qualifiedName = type.getTypeQualifiedName( '.' );
    return getLaunchManager().generateLaunchConfigurationName( qualifiedName );
  }

  private String getProjectName() {
    return type.getJavaProject().getElementName();
  }

  private static ILaunchManager getLaunchManager() {
    return DebugPlugin.getDefault().getLaunchManager();
  }
}
