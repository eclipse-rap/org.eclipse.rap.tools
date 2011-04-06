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
package org.eclipse.rap.ui.internal.launch.rwt.util;

import java.io.File;


public class FileUtil {
  
  public static boolean delete( File file ) {
    boolean result = true;
    if( file.isDirectory() ) {
      File[] files = file.listFiles();
      for( int i = 0; i < files.length; i++ ) {
        result = result & delete( files[ i ] );
      }
    }
    result = result & file.delete();
    return result;
  }
}
