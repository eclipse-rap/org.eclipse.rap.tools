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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.*;


final class BrandingExtension extends AbstractExtension {
  
  private static final String EXTENSION_ID 
    = "org.eclipse.rap.ui.branding"; //$NON-NLS-1$
  private static final String ATTR_SERVLET_NAME 
    = "servletName"; //$NON-NLS-1$
  private static final String DEFAULT_ENTRYPOINT_ID 
    = "defaultEntrypointId"; //$NON-NLS-1$

  static BrandingExtension[] findInWorkspace( final IProgressMonitor monitor ) 
    throws CoreException 
  {
    IPluginExtension[] extensions 
      = ExtensionUtil.getWorkspaceExtensions( EXTENSION_ID, monitor );
    List list = new ArrayList();
    for( int i = 0; i < extensions.length; i++ ) {
      BrandingExtension[] brandings = getBrandingExtensions( extensions[ i ] );
      list.addAll( Arrays.asList( brandings ) );
    }
    BrandingExtension[] result = new BrandingExtension[ list.size() ];
    list.toArray( result );
    return result;
  }

  private static final BrandingExtension[] getBrandingExtensions( 
    final IPluginExtension pluginExtension ) 
  {
    IPluginObject[] children = pluginExtension.getChildren();
    BrandingExtension[] result = new BrandingExtension[ children.length ];
    for( int i = 0; i < children.length; i++ ) {
      String project = ExtensionUtil.getProjectName( pluginExtension );
      IPluginElement element = ( IPluginElement )children[ i ];
      String servletName 
        = ExtensionUtil.getAttribute( element, ATTR_SERVLET_NAME );
      String defaultEntryPointId 
        = ExtensionUtil.getAttribute( element, DEFAULT_ENTRYPOINT_ID );
      result[ i ] 
        = new BrandingExtension( project, servletName, defaultEntryPointId );
    }
    return result;
  }
  
  private final String servletName;
  private final String defaultEntryPointId;

  BrandingExtension( final String project, 
                     final String servletName, 
                     final String defaultEntryPointId ) 
  {
    super( project );
    this.servletName = servletName;
    this.defaultEntryPointId = defaultEntryPointId;
  }
  
  final String getServletName() {
    return servletName;
  }
  
  final String getDefaultEntryPointId() {
    return defaultEntryPointId;
  }

  /////////////////////
  // hashCode & equals
  
  public final int hashCode() {
    int prime = 31;
    int result = 1;
    int valueHashCode = servletName == null ? 0 : servletName.hashCode();
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
      BrandingExtension other = ( BrandingExtension )obj;
      if( servletName == null ) {
        if( other.servletName != null ) {
          result = false;
        }
      } else if( !servletName.equals( other.servletName ) ) {
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
