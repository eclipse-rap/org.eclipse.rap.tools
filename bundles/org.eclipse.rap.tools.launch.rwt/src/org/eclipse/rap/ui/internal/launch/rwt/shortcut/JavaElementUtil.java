/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.shortcut;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;


class JavaElementUtil {

  static IJavaElement[] adapt( Object[] elements ) {
    List<IJavaElement> javaElements = new LinkedList<IJavaElement>();
    for( int i = 0; i < elements.length; i++ ) {
      IJavaElement javaElement = adapt( elements[ i ] );
      if( javaElement != null ) {
        javaElements.add( javaElement );
      }
    }    
    return javaElements.toArray( new IJavaElement[ javaElements.size() ] );
  }

  static IJavaElement adapt( Object object ) {
    IJavaElement result = null;
    if( object instanceof IAdaptable ) {
      IAdaptable adaptable = ( IAdaptable )object;
      result = ( IJavaElement )adaptable.getAdapter( IJavaElement.class );
    }
    return result;
  }
  
  
  private JavaElementUtil() {
    // prevent instantiation
  }
}
