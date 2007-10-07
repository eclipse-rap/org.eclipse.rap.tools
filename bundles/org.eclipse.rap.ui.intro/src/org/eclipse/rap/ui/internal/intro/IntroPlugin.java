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
package org.eclipse.rap.ui.internal.intro;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;



public class IntroPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID 
    = "org.eclipse.rap.ui.intro"; //$NON-NLS-1$
  
  // The shared instance
  private static IntroPlugin plugin;

  public static IntroPlugin getDefault() {
    return plugin;
  }

  public void start( BundleContext context ) throws Exception {
    super.start( context );
    plugin = this;
  }

  public void stop( BundleContext context ) throws Exception {
    plugin = null;
    super.stop( context );
  }
}
