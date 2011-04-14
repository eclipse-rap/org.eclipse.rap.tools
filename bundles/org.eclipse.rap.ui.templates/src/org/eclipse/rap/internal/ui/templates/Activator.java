/*******************************************************************************
 * Copyright (c) 2011 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "org.eclipse.rap.ui.templates"; //$NON-NLS-1$
  private static Activator plugin;

  public static Activator getDefault() {
    return plugin;
  }

  public void start( final BundleContext context ) throws Exception {
    super.start( context );
    plugin = this;
  }

  public void stop( final BundleContext context ) throws Exception {
    plugin = null;
    super.stop( context );
  }
}
