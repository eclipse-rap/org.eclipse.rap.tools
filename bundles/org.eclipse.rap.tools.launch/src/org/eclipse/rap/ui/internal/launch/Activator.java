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

package org.eclipse.rap.ui.internal.launch;


import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public final class Activator extends AbstractUIPlugin {

  public static final String PLUGIN_ID 
    = "org.eclipse.rap.ui.launch"; //$NON-NLS-1$
  
  // The shared instance
  private static Activator plugin;

  public static Activator getDefault() {
    return plugin;
  }
  
  public static String getPluginId() {
    return getDefault().getBundle().getSymbolicName();
  }
  
  /////////////////////////////
  // AbstractUIPlugin overrides
  
  public void start( final BundleContext context ) throws Exception {
    super.start( context );
    plugin = this;
  }

  public void stop( final BundleContext context ) throws Exception {
    plugin = null;
    super.stop( context );
  }
}
