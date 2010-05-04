/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.*;


final class ExtensionUtil {

  static IPluginExtension[] getWorkspaceExtensions(
    final String extensionPoint,
    final IProgressMonitor monitor )
  {
    return getPluginExtensions( null, extensionPoint, monitor );
  }

  static IPluginExtension[] getPluginExtensions(
    final String[] pluginIds,
    final String extensionPoint,
    final IProgressMonitor monitor )
  {
    return getWorkspaceExtensions( pluginIds, extensionPoint, monitor );
  }

  static String getAttribute( final IPluginElement element,
                              final String attributeName )
  {
    IPluginAttribute attribute = element.getAttribute( attributeName );
    return attribute == null ? null : attribute.getValue();
  }

  static String getProjectName( final IPluginExtension pluginExtension ) {
    IPluginModelBase pluginModel = pluginExtension.getPluginModel();
    return pluginModel.getUnderlyingResource().getProject().getName();
  }

  //////////////////
  // Helping methods

  private static IPluginExtension[] getWorkspaceExtensions(
    final String[] pluginIds,
    final String extensionPoint,
    final IProgressMonitor monitor )
  {
    List list = new ArrayList();
    IPluginModelBase[] pluginModels = PluginRegistry.getWorkspaceModels();
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
