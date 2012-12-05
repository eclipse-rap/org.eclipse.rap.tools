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

import org.eclipse.debug.core.*;
import org.eclipse.rap.ui.internal.launch.rwt.util.DebugUtil;


class LaunchCleanup {
  
  static void register( ILaunch launch ) {
    new LaunchCleanup( launch ).register();  
  }

  private final ILaunch launch;

  private LaunchCleanup( ILaunch launch ) {
    this.launch = launch;
  }

  private void register() {
    DebugPlugin.getDefault().addDebugEventListener( new IDebugEventSetListener() {
      public void handleDebugEvents( DebugEvent[] events ) {
        if( DebugUtil.containsTerminateEventFor( events, launch ) ) {
          DebugPlugin.getDefault().removeDebugEventListener( this );
          cleanup();
        }
      }
    } );
  }

  private void cleanup() {
    new RWTLaunch( launch ).cleanUp();
  }
}
