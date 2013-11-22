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
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;
import org.junit.Test;


public class RWTLaunchTabGroup_Test {

  @Test
  public void testCreateTabs() {
    RWTLaunchTabGroup launchTabGroup = new RWTLaunchTabGroup();

    launchTabGroup.createTabs( null, null );

    ILaunchConfigurationTab[] tabs = launchTabGroup.getTabs();
    assertEquals( 7, tabs.length );
    assertTrue( tabs[ 0 ] instanceof RWTMainTab );
    assertTrue( tabs[ 1 ] instanceof ArgumentsTab );
    assertTrue( tabs[ 2 ] instanceof JavaJRETab );
    assertTrue( tabs[ 3 ] instanceof JavaClasspathTab );
    assertTrue( tabs[ 4 ] instanceof SourceLookupTab );
    assertTrue( tabs[ 5 ] instanceof EnvironmentTab );
    assertTrue( tabs[ 6 ] instanceof CommonTab );
  }

}
