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

import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;


public final class Images {
  
  private static final String ICONS_PATH 
    = "$nl$/icons/"; //$NON-NLS-1$
  private static final String PATH_WIZ_BAN 
    = ICONS_PATH + "wizban/"; //$NON-NLS-1$
  
  public static final ImageDescriptor EXTRACT_TARGET 
    = create( PATH_WIZ_BAN + "extract_target.gif" ); //$NON-NLS-1$
  
  private static ImageDescriptor create( final String name ) { 
    Bundle bundle = IntroPlugin.getDefault().getBundle();
    IPath path = new Path( name );
    URL imageUrl = FileLocator.find( bundle, path, null );
    return ImageDescriptor.createFromURL( imageUrl );
  }

  private Images() {
    // prevent instantiation
  }
}
