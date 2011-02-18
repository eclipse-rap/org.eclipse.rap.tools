/******************************************************************************* 
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.tab;

import java.util.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginExtension;

public final class ApplicationExtension extends AbstractExtension {

  private static final String EXTENSION_ID
    = "org.eclipse.core.runtime.applications"; //$NON-NLS-1$

  public static ApplicationExtension findById( final String id ) {
    ApplicationExtension result = null;
    if( id != null ) {
      ApplicationExtension[] extensions = findInWorkspace( null );
      for( int i = 0; result == null && i < extensions.length; i++ ) {
        if( id.equals( extensions[ i ].getId() ) ) {
          result = extensions[ i ];
        }
      }
    }
    return result;
  }

  public static ApplicationExtension[] findInPlugins(
    final String[] pluginIds,
    final IProgressMonitor monitor )
  {
    IPluginExtension[] extensions
      = ExtensionUtil.getWorkspacePluginExtensions( pluginIds,
                                                    EXTENSION_ID,
                                                    monitor );
    return findInPluginExtensions( extensions );
  }

  public static ApplicationExtension[] findInWorkspace(
    final IProgressMonitor monitor )
  {
    IPluginExtension[] extensions
      = ExtensionUtil.getWorkspaceExtensions( EXTENSION_ID, monitor );
    return findInPluginExtensions( extensions );
  }

  public static ApplicationExtension[] findAllActive(
    final IProgressMonitor monitor )
  {
    IPluginExtension[] extensions
      = ExtensionUtil.getActiveExtensions( EXTENSION_ID, monitor );
    return findInPluginExtensions( extensions );
  }

  private static ApplicationExtension[] findInPluginExtensions(
    final IPluginExtension[] extensions )
  {
    List list = new ArrayList();
    for( int i = 0; i < extensions.length; i++ ) {
      ApplicationExtension[] applications;
      applications = getApplicationExtensions( extensions[ i ] );
      list.addAll( Arrays.asList( applications ) );
    }
    ApplicationExtension[] result = new ApplicationExtension[ list.size() ];
    list.toArray( result );
    return result;
  }

  private static final ApplicationExtension[] getApplicationExtensions(
    final IPluginExtension pluginExtension )
  {
    ApplicationExtension[] result = new ApplicationExtension[ 1 ];
    String id = pluginExtension.getId();
    String bundleId = pluginExtension.getPluginBase().getId();
    result[ 0 ] = new ApplicationExtension( bundleId, id );
    return result;
  }

  private final String id;

  public ApplicationExtension( final String project, final String id ) {
    super( project );
    this.id = id;
  }

  public final String getId() {
    return id;
  }

  /////////////////////
  // hashCode & equals

  public final int hashCode() {
    int prime = 31;
    int result = 1;
    int valueHashCode = id == null ? 0 : id.hashCode();
    result = prime * result + valueHashCode;
    int projectHashCode = project == null ? 0 : project.hashCode();
    result = prime * result + projectHashCode;
    return result;
  }

  public final boolean equals( final Object obj ) {
    boolean result;
    if( this == obj ) {
      result = true;
    } else if( obj == null ) {
      result = false;
    } else if( getClass() != obj.getClass() ) {
      result = false;
    } else {
      result = true;
      ApplicationExtension other = ( ApplicationExtension )obj;
      if( id == null ) {
        if( other.id != null ) {
          result = false;
        }
      } else if( !id.equals( other.id ) ) {
        result = false;
      }
      if( project == null ) {
        if( other.project != null ) {
          result = false;
        }
      } else if( !project.equals( other.project ) ) {
        result = false;
      }
    }
    return result;
  }
}