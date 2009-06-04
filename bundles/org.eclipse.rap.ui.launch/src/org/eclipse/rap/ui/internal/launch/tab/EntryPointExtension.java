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

import java.util.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.*;

public final class EntryPointExtension extends AbstractExtension {

  private static final String EXTENSION_ID
    = "org.eclipse.rap.ui.entrypoint"; //$NON-NLS-1$
  private static final String ATTR_PARAMETER = "parameter"; //$NON-NLS-1$
  private static final String ATTR_ID = "id"; //$NON-NLS-1$

  public static EntryPointExtension findById( final String id )
    throws CoreException
  {
    EntryPointExtension result = null;
    if( id != null ) {
      EntryPointExtension[] extensions = findInWorkspace( null );
      for( int i = 0; result == null && i < extensions.length; i++ ) {
        if( id.equals( extensions[ i ].getId() ) ) {
          result = extensions[ i ];
        }
      }
    }
    return result;
  }

  public static EntryPointExtension[] findInPlugins(
    final String[] pluginIds,
    final IProgressMonitor monitor )
    throws CoreException
  {
    IPluginExtension[] extensions
      = ExtensionUtil.getPluginExtensions( pluginIds, EXTENSION_ID, monitor );
    return findInPluginExtensions( extensions );
  }

  public static EntryPointExtension[] findInWorkspace(
    final IProgressMonitor monitor )
    throws CoreException
  {
    IPluginExtension[] extensions
      = ExtensionUtil.getWorkspaceExtensions( EXTENSION_ID, monitor );
    return findInPluginExtensions( extensions );
  }

  private static EntryPointExtension[] findInPluginExtensions( 
    final IPluginExtension[] extensions )
  {
    List list = new ArrayList();
    for( int i = 0; i < extensions.length; i++ ) {
      EntryPointExtension[] entryPoints;
      entryPoints = getEntryPointExtensions( extensions[ i ] );
      list.addAll( Arrays.asList( entryPoints ) );
    }
    EntryPointExtension[] result = new EntryPointExtension[ list.size() ];
    list.toArray( result );
    return result;
  }

  private static final EntryPointExtension[] getEntryPointExtensions(
    final IPluginExtension pluginExtension )
  {
    IPluginObject[] children = pluginExtension.getChildren();
    EntryPointExtension[] result = new EntryPointExtension[ children.length ];
    for( int i = 0; i < children.length; i++ ) {
      String project = ExtensionUtil.getProjectName( pluginExtension );
      IPluginElement element = ( IPluginElement )children[ i ];
      String parameter = ExtensionUtil.getAttribute( element, ATTR_PARAMETER );
      String id = ExtensionUtil.getAttribute( element, ATTR_ID );
      result[ i ] = new EntryPointExtension( project, id, parameter );
    }
    return result;
  }


  private final String id;
  private final String parameter;

  public EntryPointExtension( final String project,
                              final String id,
                              final String parameter )
  {
    super( project );
    this.id = id;
    this.parameter = parameter;
  }

  public final String getId() {
    return id;
  }

  public final String getParameter() {
    return parameter;
  }

  /////////////////////
  // hashCode & equals

  public final int hashCode() {
    int prime = 31;
    int result = 1;
    int valueHashCode = parameter == null ? 0 : parameter.hashCode();
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
      EntryPointExtension other = ( EntryPointExtension )obj;
      if( parameter == null ) {
        if( other.parameter != null ) {
          result = false;
        }
      } else if( !parameter.equals( other.parameter ) ) {
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