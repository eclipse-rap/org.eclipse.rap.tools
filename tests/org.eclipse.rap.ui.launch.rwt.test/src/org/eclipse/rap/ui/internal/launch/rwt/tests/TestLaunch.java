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
package org.eclipse.rap.ui.internal.launch.rwt.tests;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;


public class TestLaunch implements ILaunch {

  private final ILaunchConfiguration launchConfig;
  private final Map attributes;
  
  public TestLaunch( ILaunchConfiguration launchConfig ) {
    this.launchConfig = launchConfig;
    attributes = new HashMap();
  }

  public boolean canTerminate() {
    return false;
  }

  public boolean isTerminated() {
    return false;
  }

  public void terminate() throws DebugException {
  }

  public Object getAdapter( Class adapter ) {
    return null;
  }

  public Object[] getChildren() {
    return null;
  }

  public IDebugTarget getDebugTarget() {
    return null;
  }

  public IProcess[] getProcesses() {
    return null;
  }

  public IDebugTarget[] getDebugTargets() {
    return null;
  }

  public void addDebugTarget( IDebugTarget target ) {
  }

  public void removeDebugTarget( IDebugTarget target ) {
  }

  public void addProcess( IProcess process ) {
  }

  public void removeProcess( IProcess process ) {
  }

  public ISourceLocator getSourceLocator() {
    return null;
  }

  public void setSourceLocator( ISourceLocator sourceLocator ) {
  }

  public String getLaunchMode() {
    return null;
  }

  public ILaunchConfiguration getLaunchConfiguration() {
    return launchConfig;
  }

  public void setAttribute( String key, String value ) {
    attributes.put( key, value );
  }

  public String getAttribute( String key ) {
    return ( String )attributes.get( key );
  }

  public boolean hasChildren() {
    return false;
  }
}
