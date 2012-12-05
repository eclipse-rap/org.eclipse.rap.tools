/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IPluginReference;

/**
 * Describes a dependency to another plugin (requires bundle).
 */
public class PluginReference implements IPluginReference {

  private int match;
  private String version;
  private String id;

  public PluginReference( String id, String version, int match ) {
    this.id = id;
    this.version = version;
    this.match = match;
  }

  public int getMatch() {
    return match;
  }

  public String getVersion() {
    return version;
  }

  public void setMatch( int match ) throws CoreException {
    this.match = match;
  }

  public void setVersion( String version ) throws CoreException {
    this.version = version;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) throws CoreException {
    this.id = id;
  }

  public boolean equals( Object obj ) {
    if( this == obj ) {
      return true;
    }
    if( obj == null ) {
      return false;
    }
    if( getClass() != obj.getClass() ) {
      return false;
    }
    PluginReference other = ( PluginReference )obj;
    if( id == null ) {
      if( other.id != null ) {
        return false;
      }
    } else if( !id.equals( other.id ) ) {
      return false;
    }
    if( match != other.match ) {
      return false;
    }
    if( version == null ) {
      if( other.version != null ) {
        return false;
      }
    } else if( !version.equals( other.version ) ) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
    result = prime * result + match;
    result = prime * result + ( ( version == null ) ? 0 : version.hashCode() );
    return result;
  }
}
