/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


public class IntroPlugin extends AbstractUIPlugin {

  // The shared instance
  private static IntroPlugin plugin;

  public static IntroPlugin getDefault() {
    return plugin;
  }
  
  public static String getPluginId() {
    return getDefault().getBundle().getSymbolicName();
  }

  public void start( final BundleContext context ) throws Exception {
    super.start( context );
    plugin = this;
  }

  public void stop( final BundleContext context ) throws Exception {
    plugin = null;
    super.stop( context );
  }
  
  /**
   * Returns a service with the specified name or <code>null</code> if none.
   * 
   * @param serviceName name of service
   * @return service object or <code>null</code> if none
   */
  public Object acquireService( final String serviceName) {
    Object service = null;
    BundleContext bundleContext = plugin.getBundle().getBundleContext();
    ServiceReference reference = bundleContext.getServiceReference(serviceName);
    if( reference != null ) {
      service = bundleContext.getService( reference );
      if( service != null ) {
        bundleContext.ungetService( reference );
      }
    }
    return service;
  }
}
