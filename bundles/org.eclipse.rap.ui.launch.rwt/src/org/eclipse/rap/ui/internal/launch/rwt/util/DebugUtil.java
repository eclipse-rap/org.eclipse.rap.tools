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
package org.eclipse.rap.ui.internal.launch.rwt.util;

import org.eclipse.debug.core.*;
import org.eclipse.debug.core.model.RuntimeProcess;


public class DebugUtil {
  
  public static String getLaunchName( ILaunch launch ) {
    ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
    // the launch config might be null (e.g. if deleted) even though there
    // still exists a launch for that config  
    return launchConfiguration == null ? null : launchConfiguration.getName();
  }

  public static boolean containsCreateEventFor( DebugEvent[] events, ILaunch launch ) {
    boolean result = false;
    for( int i = 0; !result && i < events.length; i++ ) {
      DebugEvent event = events[ i ];
      result = isCreateEvent( event ) && DebugUtil.eventSourceEqualsLaunch( event, launch );
    }
    return result;
  }
  
  public static boolean containsTerminateEventFor( DebugEvent[] events, ILaunch launch ) {
    boolean result = false;
    for( int i = 0; !result && i < events.length; i++ ) {
      DebugEvent event = events[ i ];
      result = isTerminateEvent( event ) && DebugUtil.eventSourceEqualsLaunch( event, launch );
    }
    return result;
  }
  
  private static boolean eventSourceEqualsLaunch( DebugEvent debugEvent, ILaunch launch ) {
    boolean result = false;
    if( debugEvent.getSource() instanceof RuntimeProcess ) {
      RuntimeProcess runtimeProcess = ( RuntimeProcess )debugEvent.getSource();
      result = runtimeProcess.getLaunch() == launch;
    }
    return result;
  }
  
  private static boolean isCreateEvent( DebugEvent event ) {
    return event.getKind() == DebugEvent.CREATE;
  }

  private static boolean isTerminateEvent( DebugEvent event ) {
    return event.getKind() == DebugEvent.TERMINATE;
  }

  private DebugUtil() {
    // prevent instantiation
  }
}
