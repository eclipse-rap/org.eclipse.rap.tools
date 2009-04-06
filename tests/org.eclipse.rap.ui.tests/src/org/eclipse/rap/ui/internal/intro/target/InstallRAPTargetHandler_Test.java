/*******************************************************************************
 * Copyright (c) 2009 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro.target;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.target.provisional.*;
import org.eclipse.rap.ui.tests.Fixture;


public class InstallRAPTargetHandler_Test extends TestCase {
  
  public void testInstallTarget() throws Exception {
    File destination = Fixture.createDirectory( null );
    InstallRAPTargetHandler.execute( destination.toString(), false );
    File eclipseDir = new File( destination, "eclipse" );
    assertTrue( eclipseDir.exists() );
    // clean up
    Fixture.deleteDirectory( destination );
  }
  
  public void testInstallTargetInDestinationWithBlanks() throws Exception {
    File destination = Fixture.createDirectory( "with blanks" );
    InstallRAPTargetHandler.execute( destination.toString(), false );
    File eclipseDir = new File( destination, "eclipse" );
    assertTrue( eclipseDir.exists() );
    // clean up
    Fixture.deleteDirectory( destination );
  }
  
  public void testInstallTwice() throws Exception {
    // Install and switch target once
    File destination1 = Fixture.createDirectory( null );
    InstallRAPTargetHandler.execute( destination1.toString(), false );
    File eclipseDir1 = new File( destination1, "eclipse" );
    assertTrue( eclipseDir1.exists() );
    // Install target a second time in a different location and switch to it
    File destination2 = Fixture.createDirectory( null );
    InstallRAPTargetHandler.execute( destination2.toString(), false );
    File eclipseDir2 = new File( destination1, "eclipse" );
    assertTrue( eclipseDir2.exists() );
    // clean up
    Fixture.deleteDirectory( destination1 );
    Fixture.deleteDirectory( destination2 );
  }

  public void testSwitchTarget() throws Exception {
    File destination = Fixture.createDirectory( null );
    InstallRAPTargetHandler.execute( destination.toString(), true );
    ITargetDefinition target = getCurrentTarget();
    assertNotNull( target );
    assertTrue( target.getName().startsWith( "Rich Ajax Platform " ) );
    assertTrue( target.getBundles().length > 0 );
    assertTrue( target.getProgramArguments().length() > 0 );
    assertTrue( target.getVMArguments().length() > 0 );
    // clean up
    Fixture.deleteDirectory( destination );
  }
  
  private static ITargetDefinition getCurrentTarget() throws CoreException {
    ITargetPlatformService service = getTargetPlatformService();
    ITargetHandle targetHandle = service.getWorkspaceTargetHandle();
    ITargetDefinition target = targetHandle.getTargetDefinition();
    target.resolve( new NullProgressMonitor() );
    return target;
  }

  private static ITargetPlatformService getTargetPlatformService() {
    String className = ITargetPlatformService.class.getName();
    PDECore pdeCore = PDECore.getDefault();
    return ( ITargetPlatformService )pdeCore.acquireService( className );
  }
}
