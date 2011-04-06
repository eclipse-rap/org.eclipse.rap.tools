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
package org.eclipse.rap.ui.internal.launch.rwt.delegate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.*;
import org.eclipse.rap.ui.internal.launch.rwt.util.DebugUtil;

class LaunchTerminator {

  static void terminatePrevious( ILaunch launch, IProgressMonitor monitor ) throws CoreException {
    String taskName = "Terminating previous launch";
    monitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
    try {
      ILaunch runningLaunch = findRunning( launch );
      if( runningLaunch != null ) {
        new LaunchTerminator( runningLaunch ).terminate();
      }
    } finally {
      monitor.done();
    }
  }
  
  private static ILaunch findRunning( ILaunch launch ) {
    ILaunchConfiguration config = launch.getLaunchConfiguration();
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    ILaunch[] runningLaunches = launchManager.getLaunches();
    ILaunch result = null;
    for( int i = 0; result == null && i < runningLaunches.length; i++ ) {
      ILaunch runningLaunch = runningLaunches[ i ];
      if(    runningLaunch != launch 
          && config.getName().equals( DebugUtil.getLaunchName( runningLaunch ) ) 
          && !runningLaunch.isTerminated() )
      {
        result = runningLaunches[ i ];  
      }
    }
    return result;
  }

  private final ILaunch launch;
  private final Object signal;
  private volatile boolean terminated;

  private LaunchTerminator( ILaunch launch ) {
    this.launch = launch;
    this.signal = new Object();
  }
  
  private void terminate() throws DebugException {
    synchronized( signal ) {
      terminated = false;
    }
    DebugPlugin debugPlugin = DebugPlugin.getDefault();
    debugPlugin.addDebugEventListener( new IDebugEventSetListener() {
      public void handleDebugEvents( DebugEvent[] events ) {
        if( DebugUtil.containsTerminateEventFor( events, launch ) ) {
          DebugPlugin.getDefault().removeDebugEventListener( this );
          signalTerminated();
        }
      }
    } );
    launch.terminate();
    waitUntilTerminated();
  }

  private void waitUntilTerminated() {
    try {
      synchronized( signal ) {
        if( !terminated ) {
          signal.wait();
        }
      }
    } catch( InterruptedException e ) {
      Thread.interrupted();
    }
  }

  private void signalTerminated() {
    synchronized( signal ) {
      terminated = true;
      signal.notifyAll();
    }
  }
}
