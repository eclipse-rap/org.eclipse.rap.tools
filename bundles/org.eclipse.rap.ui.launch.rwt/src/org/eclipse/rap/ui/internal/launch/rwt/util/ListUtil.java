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

import java.util.Arrays;
import java.util.List;


public final class ListUtil {

  public static void add( List<Object> list, Object[] array ) {
    list.addAll( Arrays.asList( array ) );
  }
  
  private ListUtil() {
    // prevent instantiation
  }
}
