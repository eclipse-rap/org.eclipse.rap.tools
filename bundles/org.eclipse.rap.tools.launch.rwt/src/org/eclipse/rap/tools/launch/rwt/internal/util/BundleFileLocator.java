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
package org.eclipse.rap.tools.launch.rwt.internal.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.rap.tools.launch.rwt.internal.RWTLaunchActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


public final class BundleFileLocator {

  private final Bundle bundle;

  private BundleFileLocator( String symbolicName ) {
    bundle = findBundle( symbolicName );
  }

  public static String locate( String bundleSymbolicName ) {
    return new BundleFileLocator( bundleSymbolicName ).locate();
  }

  public String locate() {
    String result = null;
    if( bundle != null ) {
      result = locateExistingBundle();
    }
    return result;
  }

  private String locateExistingBundle() {
    String result = null;
    try {
      File bundleFile = FileLocator.getBundleFile( bundle );
      result = bundleFile.getAbsolutePath();
    } catch( IOException ioe ) {
      handleIOException( ioe );
    }
    return result;
  }

  private void handleIOException( IOException exception ) {
    String symbolicName = bundle.getSymbolicName();
    String msg= "Failed to obtain file location for bundle: " + symbolicName; //$NON-NLS-1$
    throw new RuntimeException( msg, exception );
  }

  private static Bundle findBundle( String symbolicName ) {
    Bundle result = null;
    Bundle[] bundles = getAllBundles();
    for( int i = 0; result == null && i < bundles.length; i++ ) {
      if( bundles[ i ].getSymbolicName().equals( symbolicName ) ) {
        result = bundles[ i ];
      }
    }
    return result;
  }

  private static Bundle[] getAllBundles() {
    Bundle bundle = RWTLaunchActivator.getDefault().getBundle();
    BundleContext bundleContext = bundle.getBundleContext();
    Bundle[] bundles = bundleContext.getBundles();
    return bundles;
  }

}
