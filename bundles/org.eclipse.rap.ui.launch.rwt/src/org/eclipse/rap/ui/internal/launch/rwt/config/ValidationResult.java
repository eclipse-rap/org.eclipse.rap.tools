/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.config;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.rap.ui.internal.launch.rwt.RWTLaunchActivator;

public class ValidationResult {
  private final List<IStatus> results;
  
  public ValidationResult() {
    results = new LinkedList<IStatus>();
  }
  
  public void addError( String message, int code ) {
    addStatus( IStatus.ERROR, message, code );
  }
  
  public void addWarning( String message, int code ) {
    addStatus( IStatus.WARNING, message, code );
  }

  public IStatus[] getAll() {
    IStatus[] result = new IStatus[ results.size() ];
    results.toArray( result );
    return result;
  }
  
  public IStatus[] getErrors() {
    return getStates( IStatus.ERROR );
  }
  
  public IStatus[] getWarnings() {
    return getStates( IStatus.WARNING );
  }
  
  public boolean contains( int code ) {
    IStatus[] all = getAll();
    boolean result = false;
    for( int i = 0; !result && i < all.length; i++ ) {
      if( all[ i ].getCode() == code ) {
        result = true;
      }
    }
    return result;
  }

  private void addStatus( int severity, String message, int code ) {
    if( message == null ) {
      throw new NullPointerException( "message" ); //$NON-NLS-1$
    }
    String pluginId = getPluginId();
    IStatus status = new Status( severity, pluginId, code, message, null );
    results.add( status );
  }

  private IStatus[] getStates( int severity ) {
    int count = countStates( severity );
    IStatus[] states = getAll();
    int index = 0;
    IStatus[] result = new IStatus[ count ];
    for( int i = 0; i < states.length; i++ ) {
      if( states[ i ].matches( severity ) ) {
        result[ index ] = states[ i ];
        index++;
      }
    }
    return result;
  }

  private int countStates( int severity ) {
    int result = 0;
    IStatus[] states = getAll();
    for( int i = 0; i < states.length; i++ ) {
      if( states[ i ].matches( severity ) ) {
        result++;
      }
    }
    return result;
  }

  private String getPluginId() {
    return RWTLaunchActivator.getDefault().getBundle().getSymbolicName();
  }
}