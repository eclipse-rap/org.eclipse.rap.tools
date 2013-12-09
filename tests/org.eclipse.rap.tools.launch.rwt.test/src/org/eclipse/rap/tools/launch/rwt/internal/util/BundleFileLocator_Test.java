/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.rap.tools.launch.rwt.internal.util.BundleFileLocator;
import org.junit.Test;


public class BundleFileLocator_Test {

  @Test
  public void testLocate_withExistingBundle() {
    String bundleName = "org.eclipse.jetty.server";

    String location = BundleFileLocator.locate( bundleName );

    assertNotNull( location );
    File file = new File( location );
    assertTrue( file.exists() );
    assertFalse( file.isDirectory() );
  }

  @Test
  public void testLocate_withNonExistingBundle() {
    String location = BundleFileLocator.locate( "does.not.exist" );

    assertNull( location );
  }

}
