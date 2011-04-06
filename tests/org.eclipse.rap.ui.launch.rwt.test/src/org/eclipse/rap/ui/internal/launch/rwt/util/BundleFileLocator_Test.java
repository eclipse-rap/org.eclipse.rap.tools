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

import org.eclipse.rap.ui.internal.launch.rwt.util.BundleFileLocator;

import junit.framework.TestCase;


public class BundleFileLocator_Test extends TestCase {
  
  public void testLocateWithExistingBundle() {
    String bundleName = "org.mortbay.jetty.server";
    String location = BundleFileLocator.locate( bundleName );
    
    assertNotNull( location );
    File file = new File( location );
    assertTrue( file.exists() );
    assertFalse( file.isDirectory() );
  }

  public void testLocateWithNonExistingBundle() {
    String location = BundleFileLocator.locate( "does.not.exist" );
    
    assertNull( location );
  }
}
