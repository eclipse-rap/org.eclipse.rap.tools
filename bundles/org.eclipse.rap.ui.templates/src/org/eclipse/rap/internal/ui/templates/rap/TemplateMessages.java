/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates.rap;

import org.eclipse.osgi.util.NLS;

public class TemplateMessages extends NLS {

  private static final String BUNDLE_NAME
    = "org.eclipse.rap.internal.ui.templates.rap.messages"; //$NON-NLS-1$
  
  public static String AbstractRAPWizard_Modifying;
  
  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, TemplateMessages.class );
  }

  private TemplateMessages() {
  }
}
