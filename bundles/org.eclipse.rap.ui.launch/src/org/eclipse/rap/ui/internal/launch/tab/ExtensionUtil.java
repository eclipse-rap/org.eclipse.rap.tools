/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.tab;

import java.util.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModel;
import org.eclipse.pde.internal.core.plugin.PluginHandler;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModel;
import org.osgi.framework.Bundle;


final class ExtensionUtil {

  static IPluginExtension[] getWorkspaceExtensions(
    final String extensionPoint,
    final IProgressMonitor monitor )
    throws CoreException
  {
    IPluginExtension[] result;
    if( isPDECore33() ) {
      result = getWorkspaceExtensions33( extensionPoint, monitor );
    } else {
      result = getWorkspaceExtensions34( extensionPoint, monitor );
    }
    return result;
  }

  static String getAttribute( final IPluginElement element,
                              final String attributeName )
  {
    IPluginAttribute attribute = element.getAttribute( attributeName );
    return attribute == null ? null : attribute.getValue();
  }

  static String getProjectName( final IPluginExtension pluginExtension )
  {
    String result = null;
    IPluginModelBase pluginModel = pluginExtension.getPluginModel();
    if( pluginModel instanceof WorkspacePluginModel ) {
      WorkspacePluginModel workspacePlugin = ( WorkspacePluginModel )pluginModel;
      IResource resource = workspacePlugin.getUnderlyingResource();
      result = resource.getProject().getName();
    }
    return result;
  }

  //////////////////
  // Helping methods

  private static IPluginExtension[] getWorkspaceExtensions33( 
    final String extensionPoint,
    final IProgressMonitor monitor )
    throws CoreException
  {
    List list = new ArrayList();
    IPluginModelBase[] workspacePlugins = findInWorkspace33( extensionPoint );
    for( int i = 0; !isCanceled( monitor ) && i < workspacePlugins.length; i++ )
    {
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
        IFile pluginXml = ( IFile )extensionsModel.getUnderlyingResource();
        WorkspacePluginModel workspacePlugin
          = new WorkspacePluginModel( pluginXml, false );
        // load using standard plugin handler w/o abbreviation
        workspacePlugin.load( pluginXml.getContents(),
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

  private static IPluginExtension[] getWorkspaceExtensions34(
    final String extensionPoint,
    final IProgressMonitor monitor )
  {
    List list = new ArrayList();
    IPluginModelBase[] pluginModels = PluginRegistry.getWorkspaceModels();
    for( int i = 0; !isCanceled( monitor ) && i < pluginModels.length; i++ ) {
      IPluginExtension[] extensions
        = pluginModels[ i ].getExtensions().getExtensions();
      for( int k = 0; k < extensions.length; k++ ) {
        if( extensionPoint.equals( extensions[ k ].getPoint() ) ) {
          list.add( extensions[ k ] );
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

  private static IPluginModelBase[] findInWorkspace33(
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

  private static boolean isCanceled( final IProgressMonitor monitor ) {
    return monitor != null && monitor.isCanceled();
  }

  private static boolean isPDECore33() {
    Bundle bundle = Platform.getBundle( "org.eclipse.pde.core" ); //$NON-NLS-1$
    Dictionary headers = bundle.getHeaders();
    String version = ( String )headers.get( "Bundle-Version" ); //$NON-NLS-1$
    return version.startsWith( "3.3" ); //$NON-NLS-1$
  }

  private ExtensionUtil() {
    // prevent instantiation
  }
}
