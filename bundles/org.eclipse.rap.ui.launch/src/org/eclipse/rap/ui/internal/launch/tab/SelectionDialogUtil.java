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

package org.eclipse.rap.ui.internal.launch.tab;

import org.eclipse.ui.dialogs.SearchPattern;


final class SelectionDialogUtil {

  static SearchPattern createSearchPattern() {
    return new ExtensionSearchPattern();
  }
  
  static String getLabel( final String project, final String attribute ) {
    StringBuffer result = new StringBuffer();
    if( attribute == null ) {
      result.append( "(unknown)" );
    } else {
      result.append( attribute );
    }
    result.append( " - " ); //$NON-NLS-1$
    if( project == null ) {
      result.append( "(unknown project)" );
    } else {
      result.append( project );
    }
    return result.toString();
  }

  private SelectionDialogUtil() {
    // prevent instantiation
  }
  
  ////////////////
  // Inner classes
  
  private static final class ExtensionSearchPattern extends SearchPattern {

    public void setPattern( final String stringPattern ) {
      String pattern = stringPattern;
      if( pattern.length() == 0 ) {
        pattern = "**"; //$NON-NLS-1$
      }
      super.setPattern( pattern );
    }
  }
}
