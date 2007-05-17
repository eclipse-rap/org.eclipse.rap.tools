/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.ui.internal.launch.tab;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModel;
import org.eclipse.pde.internal.core.plugin.PluginHandler;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModel;


final class ExtensionLocator {
  
  static IPluginExtension[] getWorkspaceExtensions( 
    final String extensionPoint ) 
    throws CoreException 
  {
    List list = new ArrayList();
    IPluginModelBase[] workspacePlugins = findInWorkspace( extensionPoint );
    
    for( int i = 0; i < workspacePlugins.length; i++ ) {
      IPluginModelBase modelBase = workspacePlugins[ i ];
      /*
       * Here's the naught code. IBundlePluginModel contains a reference to the
       * ISharedWorkspacePlugin model which actually contains the extension
       * processing tidbits. We snag the resource for it (/plugin.xml) and
       * create our very own one (WorkspacePluginModel). This exposes the
       * load(InputStream, boolean, PluginHandler) method - so we instantiate a
       * PluginHandler that will do the fully loading and not the abbreviated
       * loading. ugh.
       */
      if( modelBase instanceof IBundlePluginModel ) {
        IBundlePluginModel bundlePlugin = ( IBundlePluginModel )modelBase;
        ISharedExtensionsModel extensionsModel 
          = bundlePlugin.getExtensionsModel();
        IFile file = ( IFile )extensionsModel.getUnderlyingResource();
        WorkspacePluginModel workspacePlugin 
          = new WorkspacePluginModel( file, false );
        // load using standard plugin handler w/o abbreviation
        workspacePlugin.load( file.getContents(), 
                              false, 
                              new PluginHandler( false ) );
        modelBase = workspacePlugin;
      }
      IPluginExtension[] extensions = modelBase.getExtensions().getExtensions();
      for( int k = 0; k < extensions.length; k++ ) {
        if( extensionPoint.equals( extensions[ k ].getPoint() ) ) {
          list.add( extensions[ k ] );
        }
      }
    }
    IPluginExtension[] result = new IPluginExtension[ list.size() ];
    list.toArray( result );
    return result;
  }

  static IPluginModelBase[] findInWorkspace( 
    final String extensionPoint )
  {
    List list = new ArrayList();
    IPluginModelBase[] workspaceModels = PluginRegistry.getWorkspaceModels();
    for( int i = 0; i < workspaceModels.length; i++ ) {
      IPluginExtension[] extensions 
        = workspaceModels[ i ].getExtensions().getExtensions();
      for( int k = 0; k < extensions.length; k++ ) {
        if( extensionPoint.equals( extensions[ k ].getPoint() ) ) {
          list.add( workspaceModels[ i ] );
        }
      }
    }
    IPluginModelBase[] result = new IPluginModelBase[ list.size() ];
    list.toArray( result );
    return result;
  }
  
  private ExtensionLocator() {
    // prevent instantiation
  }
}
