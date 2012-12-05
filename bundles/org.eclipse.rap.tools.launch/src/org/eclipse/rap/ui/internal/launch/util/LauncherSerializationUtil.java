/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.ui.internal.launch.util;

import org.eclipse.rap.ui.internal.launch.tab.*;


public class LauncherSerializationUtil {
  
  public static String serializeBrandingExtension(
    final BrandingExtension extension )
  {
    return extension.getServletName();
  }
  
  public static String serializeEntryPointExntesion( 
    final EntryPointExtension extension )
  {
    return extension.getParameter();
  }
  
  public static String serializeApplicationExtension( 
    final ApplicationExtension extension )
  {
    return extension.getProject() + "." + extension.getId(); //$NON-NLS-1$
  }
}
