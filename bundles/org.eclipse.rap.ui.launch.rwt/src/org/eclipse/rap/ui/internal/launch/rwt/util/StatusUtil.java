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

import org.eclipse.core.runtime.*;
import org.eclipse.rap.ui.internal.launch.rwt.RWTLaunchActivator;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.Bundle;


public class StatusUtil {
  
  public static void log( Throwable throwable ) {
    String symbolicName
      = RWTLaunchActivator.getDefault().getBundle().getSymbolicName();
    String message = throwable.getMessage();
    IStatus status
      = new Status( IStatus.ERROR, symbolicName, message, throwable );
    StatusManager.getManager().handle( status, StatusManager.LOG );
  }
  
  public static void showCoreException( CoreException exception ) {
    Bundle bundle = RWTLaunchActivator.getDefault().getBundle();
    String pluginId = bundle.getSymbolicName();
    StatusManager.getManager().handle( exception, pluginId );
    IStatus status = exception.getStatus();
    StatusManager.getManager().handle( status, StatusManager.SHOW );
  }

  private StatusUtil() {
    // prevent instantiation
  }
}
