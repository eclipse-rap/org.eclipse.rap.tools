/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.*;


final class ExtensionUtil {

  static IPluginExtension[] getWorkspaceExtensions(
    final String extensionPoint,
    final IProgressMonitor monitor )
  {
    return getWorkspacePluginExtensions( null, extensionPoint, monitor );
  }

  static IPluginExtension[] getActiveExtensions( 
    final String extensionPoint,
    final IProgressMonitor monitor )
  {
    IPluginModelBase[] pluginModels = PluginRegistry.getActiveModels();
    return getExtensions( null, extensionPoint, pluginModels, monitor );
  }

  
  static IPluginExtension[] getWorkspacePluginExtensions(
    final String[] pluginIds,
    final String extensionPoint,
    final IProgressMonitor monitor )
  {
    IPluginModelBase[] pluginModels = PluginRegistry.getWorkspaceModels();
    return getExtensions( pluginIds, extensionPoint, pluginModels, monitor );
  }

  static String getAttribute( final IPluginElement element,
                              final String attributeName )
  {
    IPluginAttribute attribute = element.getAttribute( attributeName );
    return attribute == null ? null : attribute.getValue();
  }

  static String getProjectName( final IPluginExtension pluginExtension ) {
    String name = null;
    IPluginModelBase pluginModel = pluginExtension.getPluginModel();
    IResource workspaceResource = pluginModel.getUnderlyingResource();
    if( workspaceResource == null ) {
      name = pluginModel.getBundleDescription().getSymbolicName();
    } else {
      name = workspaceResource.getProject().getName();
    }
    return name;
  }

  //////////////////
  // Helping methods

  private static IPluginExtension[] getExtensions(
    final String[] pluginIds,
    final String extensionPoint,
    final IPluginModelBase[] pluginModels, 
    final IProgressMonitor monitor )
  {
    List list = new ArrayList();
    for( int i = 0; !isCanceled( monitor ) && i < pluginModels.length; i++ ) {
      String pluginId = pluginModels[ i ].getPluginBase().getId();
      if( isContained( pluginIds, pluginId ) ) {
        IPluginExtension[] extensions
          = pluginModels[ i ].getExtensions().getExtensions();
        for( int k = 0; k < extensions.length; k++ ) {
          if( extensionPoint.equals( extensions[ k ].getPoint() ) ) {
            list.add( extensions[ k ] );
          }
        }
      }
    }
    IPluginExtension[] result;
    if( isCanceled( monitor ) ) {
      // discard all eventually collected information if monitor was canceled
      result = new IPluginExtension[ 0 ];
    } else {
      result = new IPluginExtension[ list.size() ];
      list.toArray( result );
    }
    return result;
  }

  private static boolean isCanceled( final IProgressMonitor monitor ) {
    return monitor != null && monitor.isCanceled();
  }

  private static boolean isContained( final String[] pluginIds, 
                                      final String pluginId )
  {
    boolean result;
    if( pluginIds == null ) {
      result = true;
    } else {
      result = false;
      for( int i = 0; !result && i < pluginIds.length; i++ ) {
        if( pluginId.equals( pluginIds[ i ] ) ) {
          result = true;
        }
      }
    }
    return result;
  }

  private ExtensionUtil() {
    // prevent instantiation
  }
}
