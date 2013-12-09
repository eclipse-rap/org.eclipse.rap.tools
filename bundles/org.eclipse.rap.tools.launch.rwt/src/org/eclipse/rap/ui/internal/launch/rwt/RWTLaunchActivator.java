/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class RWTLaunchActivator extends AbstractUIPlugin {

  public static RWTLaunchActivator getDefault() {
    return plugin;
  }

  public static String getPluginId() {
    return RWTLaunchActivator.getDefault().getBundle().getSymbolicName();
  }

  private static RWTLaunchActivator plugin;

  public void start( BundleContext context ) throws Exception {
    super.start( context );
    plugin = this;
  }

  public void stop( BundleContext context ) throws Exception {
    plugin = null;
    super.stop( context );
  }

}
