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

import java.util.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModel;

final class EntryPointExtension {
  
  private static final String EXTENSION_ID 
    = "org.eclipse.rap.ui.workbench.entrypoint"; //$NON-NLS-1$

  private static final String PARAMETER = "parameter"; //$NON-NLS-1$

  static EntryPointExtension[] findInWorkspace() throws CoreException {
    IPluginExtension[] extensions 
      = ExtensionLocator.getWorkspaceExtensions( EXTENSION_ID );
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
      IPluginElement element = ( IPluginElement )children[ i ];
      String parameter = null;
      IPluginAttribute attribute = element.getAttribute( PARAMETER ); 
      if( attribute != null ) {
        parameter = attribute.getValue();
      }
      String project = getProjectName( pluginExtension );
      result[ i ] = new EntryPointExtension( project, parameter );
    }
    return result;
  }

  private static String getProjectName( final IPluginExtension pluginExtension )
  {
    String result = null;
    IPluginModelBase pluginModel = pluginExtension.getPluginModel();
    if( pluginModel instanceof WorkspacePluginModel ) {
      WorkspacePluginModel workspacePlugin 
        = ( WorkspacePluginModel )pluginModel;
      IResource resource = workspacePlugin.getUnderlyingResource();
      result = resource.getProject().getName();
    }
    return result;
  }
  
  private final String parameter;
  private final String project;
  
  EntryPointExtension( final String project, final String parameter ) {
    this.parameter = parameter;
    this.project = project;
  }
  
  /////////
  // Getter

  String getParameter() {
    return parameter;
  }
  
  String getProject() {
    return project;
  }

  ////////////////////
  // hashCode & equals
  
  public int hashCode() {
    int prime = 31;
    int result = 1;
    int paramHashCode = ( ( parameter == null ) ? 0 : parameter.hashCode() );
    result = prime * result + paramHashCode;
    int projectHashCode = ( ( project == null ) ? 0 : project.hashCode() );
    result = prime * result + projectHashCode;
    return result;
  }

  public boolean equals( final Object obj ) {
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