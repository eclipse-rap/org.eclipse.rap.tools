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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.*;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;


class LaunchConfigFinder {

  private final LaunchConfigSelector launchConfigSelector;
  private List<ILaunchConfiguration> candidateConfigs;
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
    this.candidateConfigs = new LinkedList<ILaunchConfiguration>();
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
      result = candidateConfigs.get( 0 );
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

  private boolean isLaunchConfigForType( RWTLaunchConfig config ) throws JavaModelException {
    String projectName = type.getJavaProject().getElementName();
    boolean projectEquals = config.getProjectName().equals( projectName );
    boolean launchTargetEquals = LaunchTarget.ENTRY_POINT.equals( config.getLaunchTarget() );
    boolean applicationEquals = config.getEntryPoint().equals( type.getFullyQualifiedName() );
    if( new TypeInspector( type ).isApplicationConfigurationType() ) {
      launchTargetEquals = LaunchTarget.APP_CONFIG.equals( config.getLaunchTarget() );
      applicationEquals = config.getAppConfig().equals( type.getFullyQualifiedName() );
    }
    return projectEquals && launchTargetEquals && applicationEquals;
  }

  private static ILaunchConfiguration[] listExistingLaunchConfigs() throws CoreException {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    return launchManager.getLaunchConfigurations( RWTLaunchConfig.getType() );
  }

  interface LaunchConfigSelector {
    ILaunchConfiguration select( ILaunchConfiguration[] launchConfigs );
  }

}
