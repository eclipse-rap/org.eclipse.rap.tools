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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.*;
import org.eclipse.jdt.core.IType;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;


class LaunchConfigFinder {
  
  interface LaunchConfigSelector {
    ILaunchConfiguration select( ILaunchConfiguration[] launchConfigs );
  }
  
  private final LaunchConfigSelector launchConfigSelector;
  private List /*ILaunchConfiguration*/ candidateConfigs;
  private IType type;

  LaunchConfigFinder( LaunchConfigSelector launchConfigSelector ) {
    this.launchConfigSelector = launchConfigSelector;
  }

  ILaunchConfiguration forType( IType type ) throws CoreException {
    initialize( type );
    collectCandidateLaunchConfigs();
    return chooseLaunchConfig();
  }

  private void initialize( IType type ) {
    this.candidateConfigs = new LinkedList();
    this.type = type;
  }

  private void collectCandidateLaunchConfigs() throws CoreException {
    ILaunchConfiguration[] configs = listExistingLaunchConfigs();
    for( int i = 0; i < configs.length; i++ ) {
      RWTLaunchConfig config = new RWTLaunchConfig( configs[ i ] );
      if( isLaunchConfigForType( config ) ) {
        candidateConfigs.add( configs[ i ] );
      }
    }
  }

  private ILaunchConfiguration chooseLaunchConfig() {
    ILaunchConfiguration result = null;
    int candidateCount = candidateConfigs.size();
    if( candidateCount == 1 ) {
      result = ( ILaunchConfiguration )candidateConfigs.get( 0 );
    } else if( candidateCount > 1 ) {
      result = launchConfigSelector.select( candidateConfigsAsArray() );
    }
    return result;
  }

  private ILaunchConfiguration[] candidateConfigsAsArray() {
    ILaunchConfiguration[] result = new ILaunchConfiguration[ candidateConfigs.size() ];
    candidateConfigs.toArray( result );
    return result;
  }

  private boolean isLaunchConfigForType( RWTLaunchConfig config ) {
    String projectName = type.getJavaProject().getElementName();
    boolean entryPointEquals = config.getEntryPoint().equals( type.getFullyQualifiedName() );
    boolean projectEquals = config.getProjectName().equals( projectName );
    boolean useEntryPoint = !config.getUseWebXml();
    return projectEquals && entryPointEquals && useEntryPoint;
  }

  private static ILaunchConfiguration[] listExistingLaunchConfigs() throws CoreException {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    return launchManager.getLaunchConfigurations( RWTLaunchConfig.getType() );
  }
}
