/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.rap.ui.internal.launch.rwt.RWTLaunchActivator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

public final class Images {

  public static final String VIEW_MAIN_TAB = "VIEW_MAIN_TAB"; //$NON-NLS-1$
  public static final String VIEW_ARGUMENTS_TAB = "VIEW_ARGUMENTS_TAB"; //$NON-NLS-1$

  private static String ICONS_PATH = "$nl$/icons/full/"; //$NON-NLS-1$
  private static final String EVIEW = ICONS_PATH + "eview16/"; //$NON-NLS-1$

  private static ImageRegistry imageRegistry;

  public static Image getImage( String key ) {
    return getImageRegistry().get( key );
  }

  public static ImageDescriptor getImageDescriptor( String key ) {
    return getImageRegistry().getDescriptor( key );
  }

  private static ImageRegistry getImageRegistry() {
    if( imageRegistry == null ) {
      initializeImageRegistry();
    }
    return imageRegistry;
  }

  private static void initializeImageRegistry() {
    imageRegistry = new ImageRegistry( Display.getCurrent() );
    declareImages();
  }

  private static void declareImages() {
    declareRegistryImage( VIEW_ARGUMENTS_TAB, EVIEW + "arguments_tab.gif" ); //$NON-NLS-1$
    declareRegistryImage( VIEW_MAIN_TAB, EVIEW + "main_tab.gif" ); //$NON-NLS-1$
  }

  private static void declareRegistryImage( String key, String path ) {
    ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
    Bundle bundle = RWTLaunchActivator.getDefault().getBundle();
    URL url = null;
    if( bundle != null ) {
      url = FileLocator.find( bundle, new Path( path ), null );
      if( url != null ) {
        desc = ImageDescriptor.createFromURL( url );
      }
    }
    imageRegistry.put( key, desc );
  }
}
