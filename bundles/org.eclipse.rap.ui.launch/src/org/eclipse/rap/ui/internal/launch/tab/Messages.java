/*******************************************************************************
 * Copyright (c) 2011 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.tab;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

  private static final String BUNDLE_NAME = "org.eclipse.rap.ui.internal.launch.tab.messages"; //$NON-NLS-1$
  public static String DataLocationBlock_ClearInstanceAreaLabel;
  public static String DataLocationBlock_InstanceAreaTitle;
  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, Messages.class );
  }

  private Messages() {
  }
}
