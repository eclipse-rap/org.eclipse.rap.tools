/*******************************************************************************
 * Copyright (c) 2011 EclipseSource.
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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.target.provisional.IResolvedBundle;
import org.eclipse.pde.internal.core.target.provisional.ITargetDefinition;
import org.eclipse.pde.internal.core.target.provisional.ITargetHandle;
import org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService;
import org.osgi.framework.Bundle;

public class TargetSwitcher_Test extends TestCase {

  private final static String[] ROOT_IU = new String[]{
    "org.eclipse.rap.target.test.feature.feature.group"
  };
  private final static String targetVersion = "1.4";
  private ITargetHandle[] initialTargets;
  private List filesToDelete;
  
  protected void setUp() throws Exception {
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    initialTargets = targetPlatformService.getTargets( new NullProgressMonitor() );
    filesToDelete = new ArrayList();
  }

  protected void tearDown() throws Exception {
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    ITargetHandle[] currentTargets 
      = targetPlatformService.getTargets( new NullProgressMonitor() );
    // Delete all available targets
    for( int i = 0; i < currentTargets.length; i++ ) {
      targetPlatformService.deleteTarget( currentTargets[ i ] );
    }
    // Add initial targets
    for( int i = 0; i < initialTargets.length; i++ ) {
      ITargetDefinition targetDefinition 
        = initialTargets[ i ].getTargetDefinition();
      targetPlatformService.saveTargetDefinition( targetDefinition );
    }
    
    //Deletes all files
    for(int i = 0; i < filesToDelete.size(); i++){
      File fileToDelete = ( File )filesToDelete.get( i );
      boolean deleted = deleteFileOrDirectory( fileToDelete );
      assertTrue( "Failed to delete file", deleted );
    }
  }

  public void testSwitchTarget() throws CoreException {
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    ITargetHandle workspaceTargetHandle = targetPlatformService.getWorkspaceTargetHandle();
    try {
      ITargetDefinition newTarget = targetPlatformService.newTarget();
      newTarget.setName( "testTarget" );
      targetPlatformService.saveTargetDefinition( newTarget );
      assertNotSame( newTarget.getHandle(), workspaceTargetHandle );
      TargetSwitcher.switchTarget( newTarget );
      ITargetHandle workspaceTargetHandle2 = targetPlatformService.getWorkspaceTargetHandle();
      assertEquals( newTarget.getHandle(), workspaceTargetHandle2 );
    } finally {
      // Switch back
      ITargetDefinition targetDefinition = workspaceTargetHandle.getTargetDefinition();
      TargetSwitcher.switchTarget( targetDefinition );
    }
  }

  public void testInstallTargetAvailable() throws Exception {
    String p2RepoURI = createFakeRepository();
    ITargetDefinition targetDefinition = TargetSwitcher.install( p2RepoURI,
                                                                 ROOT_IU,
                                                                 targetVersion,
                                                                 false,
                                                                 new NullProgressMonitor() );
    assertNotNull( "Target shuldn't be null if no exception happened",
                   targetDefinition );
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    ITargetHandle[] availableTargets 
      = targetPlatformService.getTargets( new NullProgressMonitor() );
    List availableTargetsAsList = Arrays.asList( availableTargets );
    ITargetHandle handle = targetDefinition.getHandle();
    assertTrue( "Target wasn't install properly", availableTargetsAsList.contains( handle ) );
  }

  public void testInstall() throws Exception {
    String p2RepoURI = createFakeRepository();
    ITargetDefinition targetDefinition 
      = TargetSwitcher.install( p2RepoURI,
                                ROOT_IU,
                                targetVersion,
                                false,
                                new NullProgressMonitor() );
    assertNotNull( "Target shuldn't be null if no exception happened",
                   targetDefinition );
    boolean resolved = targetDefinition.getBundleContainers()[ 0 ].isResolved();
    assertTrue( "Target should be in resolved state", resolved );
    IResolvedBundle[] bundles 
      = targetDefinition.getBundleContainers()[ 0 ].getBundles();
    assertEquals( "Not all bundles are resolved", 1, bundles.length );
  }

  public void testWsRapSet() throws Exception {
    String p2RepoURI = createFakeRepository();
    ITargetDefinition targetDefinition = TargetSwitcher.install( p2RepoURI,
                                                                 ROOT_IU,
                                                                 targetVersion,
                                                                 false,
                                                                 new NullProgressMonitor() );
    assertNotNull( "Target shouldn't be null if no exception happened", targetDefinition );
    assertFalse( "rap".equals( targetDefinition.getWS() ) );
// TODO [rst] Re-enable when bug 338544 is fixed
//    assertEquals( "rap", targetDefinition.getWS() );
  }

  public void testInstallTwice() throws Exception {
    String p2RepoURI = createFakeRepository();
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    ITargetDefinition targetDefinition 
      = TargetSwitcher.install( p2RepoURI,
                                ROOT_IU,
                                targetVersion,
                                false,
                                new NullProgressMonitor() );
    assertNotNull( "Target shuldn't be null if no exception happened",
                   targetDefinition );
    boolean resolved = targetDefinition.getBundleContainers()[ 0 ].isResolved();
    assertTrue( "Target should be in resolved state", resolved );
    IResolvedBundle[] bundles 
      = targetDefinition.getBundleContainers()[ 0 ].getBundles();
    assertEquals( "Not all bundles are resolved", 1, bundles.length );
    ITargetHandle[] targets 
      = targetPlatformService.getTargets( new NullProgressMonitor() );
    // Second installation
    ITargetDefinition targetDefinition2 
      = TargetSwitcher.install( p2RepoURI,
                                ROOT_IU,
                                targetVersion,
                                false,
                                new NullProgressMonitor() );
    boolean resolved2 
      = targetDefinition2.getBundleContainers()[ 0 ].isResolved();
    assertTrue( "Target should be in resolved state", resolved2 );
    IResolvedBundle[] bundles2 
      = targetDefinition2.getBundleContainers()[ 0 ].getBundles();
    assertEquals( "Not all bundles are resolved", 1, bundles2.length );
    ITargetHandle[] targets2 
      = targetPlatformService.getTargets( new NullProgressMonitor() );
    assertEquals( "Targets should be equal", targets.length, targets2.length );
  }

  public void testInstallTargetConfiguration()
    throws Exception
  {
    String VM_ARGS = "-Dosgi.noShutdown=true -Declipse.ignoreApp=true"; //$NON-NLS-1$
    String PROGRAM_ARGS = "-console -consolelog"; //$NON-NLS-1$
    String p2RepoURI = createFakeRepository();
    ITargetDefinition targetDefinition = TargetSwitcher.install( p2RepoURI,
                                                                 ROOT_IU,
                                                                 targetVersion,
                                                                 false,
                                                                 new NullProgressMonitor() );
    assertNotNull( "Target shuldn't be null if no exception happened", targetDefinition );
    String targetVmArguments = targetDefinition.getVMArguments();
    assertEquals( VM_ARGS, targetVmArguments );
    String targetProgramArguments = targetDefinition.getProgramArguments();
    assertEquals( PROGRAM_ARGS, targetProgramArguments );
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
  
  private URL getResourceURL(final String resource) throws IOException {
    Bundle testsBundle = Platform.getBundle( "org.eclipse.rap.ui.tests" );
    Path resourcePath = new Path( resource );
    URL resourceURL = FileLocator.find( testsBundle, resourcePath, null );
    return FileLocator.resolve( resourceURL );
  }
  
  private String createFakeRepository() throws Exception{
    URL repositoryDest = createRepositoryDest();
    Path repositoryDestPath = new Path( repositoryDest.getPath() );
    filesToDelete.add( repositoryDestPath.toFile() );
    AntRunner runner = new AntRunner();
    URL copyRepositoryScriptURL = getResourceURL( "copy_repository.ant.xml" );
    URL repositoryContentURL = getResourceURL( "/target" );
    Path repositoryContentPath = new Path( repositoryContentURL.getPath() );
    Map scriptProperties = getProperties( repositoryContentPath.toString(), 
                                          repositoryDestPath.toString() );
    runAntTask( copyRepositoryScriptURL, runner, scriptProperties );
    return repositoryDest.toString();
  }

  private void runAntTask( final URL copyRepositoryScriptURL,
                           final AntRunner runner,
                           final Map scriptProperties ) throws CoreException
  {
    Path copyRepositoryPath = new Path( copyRepositoryScriptURL.getPath() );
    runner.setBuildFileLocation( copyRepositoryPath.toString() );
    runner.addUserProperties( scriptProperties );
    runner.run( new NullProgressMonitor() );
  }

  private Map getProperties( final String srcProp, final String destProp ) {
    Map result = new HashMap();
    result.put( "src", srcProp ); //$NON-NLS-1$
    result.put( "dest", destProp ); //$NON-NLS-1$
    return result;
  }

  private URL createRepositoryDest() throws IOException {
    File fakeRepositoryDest = File.createTempFile( "fakeRepository", "" );
    fakeRepositoryDest.delete();
    fakeRepositoryDest.mkdirs();
    return fakeRepositoryDest.toURI().toURL();
  }
  
  private boolean deleteFileOrDirectory(final File fileToDelete) {
    if( fileToDelete.exists() && fileToDelete.isDirectory()) {
      File[] files = fileToDelete.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           deleteFileOrDirectory( files[i] );
         }
         else {
           files[i].delete();
         }
      }
    }
    return( fileToDelete.delete() );
  }
}
