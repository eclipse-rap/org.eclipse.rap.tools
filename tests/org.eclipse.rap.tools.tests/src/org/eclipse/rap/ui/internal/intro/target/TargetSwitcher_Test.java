/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.core.target.TargetBundle;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.rap.ui.tests.Fixture;
import org.osgi.framework.Bundle;


@SuppressWarnings( "restriction" )
public class TargetSwitcher_Test extends TestCase {

  private final static String TARGET_LOCATION = "/test_target";
  private final static String LATEST_TARGET_LOCATION = "/latest_target";
  private ITargetHandle[] initialTargets;
  private List< File > filesToDelete;

  protected void setUp() throws Exception {
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    initialTargets = targetPlatformService.getTargets( new NullProgressMonitor() );
    filesToDelete = new ArrayList< File >();
  }

  protected void tearDown() throws Exception {
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    ITargetHandle[] currentTargets = targetPlatformService.getTargets( new NullProgressMonitor() );
    // Delete all available targets
    for( int i = 0; i < currentTargets.length; i++ ) {
      targetPlatformService.deleteTarget( currentTargets[ i ] );
    }
    // Add initial targets
    for( int i = 0; i < initialTargets.length; i++ ) {
      ITargetDefinition targetDefinition = initialTargets[ i ].getTargetDefinition();
      targetPlatformService.saveTargetDefinition( targetDefinition );
    }
    // Deletes all files
    for( int i = 0; i < filesToDelete.size(); i++ ) {
      Fixture.deleteDirectory( filesToDelete.get( i ) );
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
    String targetFileUri = createTargetDefinitionFile( TARGET_LOCATION );
    ITargetDefinition targetDefinition = TargetSwitcher.install( targetFileUri,
                                                                 false,
                                                                 new NullProgressMonitor() );
    assertNotNull( "Target shuldn't be null if no exception happened", targetDefinition );
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    ITargetHandle[] availableTargets = targetPlatformService.getTargets( new NullProgressMonitor() );
    List< ITargetHandle > availableTargetsAsList = Arrays.asList( availableTargets );
    ITargetHandle handle = targetDefinition.getHandle();
    assertTrue( "Target wasn't install properly", availableTargetsAsList.contains( handle ) );
  }

  public void testInstall() throws Exception {
    String targetFileUri = createTargetDefinitionFile( TARGET_LOCATION );
    ITargetDefinition targetDefinition = TargetSwitcher.install( targetFileUri,
                                                                 false,
                                                                 new NullProgressMonitor() );
    assertNotNull( "Target shuldn't be null if no exception happened", targetDefinition );
    boolean resolved = targetDefinition.isResolved();
    assertTrue( "Target should be in resolved state", resolved );
    TargetBundle[] bundles = targetDefinition.getBundles();
    assertEquals( "Not all bundles are resolved", 1, bundles.length );
  }

  public void testInstallOldTargetAndLatestTargetFileLoaded() throws Exception {
    String targetFileUri = createTargetDefinitionFile( TARGET_LOCATION );
    createTargetDefinitionFile( LATEST_TARGET_LOCATION );
    ITargetDefinition targetDefinition = TargetSwitcher.install( targetFileUri,
                                                                 false,
                                                                 new NullProgressMonitor() );
    assertNotNull( "Target shuldn't be null if no exception happened", targetDefinition );
    boolean resolved = targetDefinition.isResolved();
    assertTrue( "Target should be in resolved state", resolved );
    TargetBundle[] bundles = targetDefinition.getBundles();
    assertEquals( "Not all bundles are resolved", 1, bundles.length );
  }

  public void testInstallTwice() throws Exception {
    String p2RepoURI = createTargetDefinitionFile( TARGET_LOCATION );
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    ITargetDefinition targetDefinition = TargetSwitcher.install( p2RepoURI,
                                                                 false,
                                                                 new NullProgressMonitor() );
    assertNotNull( "Target shuldn't be null if no exception happened", targetDefinition );
    boolean resolved = targetDefinition.isResolved();
    assertTrue( "Target should be in resolved state", resolved );
    TargetBundle[] bundles = targetDefinition.getBundles();
    assertEquals( "Not all bundles are resolved", 1, bundles.length );
    ITargetHandle[] targets = targetPlatformService.getTargets( new NullProgressMonitor() );
    // Second installation
    ITargetDefinition targetDefinition2 = TargetSwitcher.install( p2RepoURI,
                                                                  false,
                                                                  new NullProgressMonitor() );
    boolean resolved2 = targetDefinition2.isResolved();
    assertTrue( "Target should be in resolved state", resolved2 );
    TargetBundle[] bundles2 = targetDefinition2.getBundles();
    assertEquals( "Not all bundles are resolved", 1, bundles2.length );
    ITargetHandle[] targets2 = targetPlatformService.getTargets( new NullProgressMonitor() );
    assertEquals( "Targets should be equal", targets.length, targets2.length );
  }

  public void testInstallTargetConfiguration() throws Exception {
    String VM_ARGS = "-Dosgi.noShutdown=true -Declipse.ignoreApp=true"; //$NON-NLS-1$
    String PROGRAM_ARGS = "-console -consolelog"; //$NON-NLS-1$
    String p2RepoURI = createTargetDefinitionFile( TARGET_LOCATION );
    ITargetDefinition targetDefinition = TargetSwitcher.install( p2RepoURI,
                                                                 false,
                                                                 new NullProgressMonitor() );
    assertNotNull( "Target shuldn't be null if no exception happened", targetDefinition );
    String targetVmArguments = targetDefinition.getVMArguments();
    assertEquals( VM_ARGS, targetVmArguments );
    String targetProgramArguments = targetDefinition.getProgramArguments();
    assertEquals( PROGRAM_ARGS, targetProgramArguments );
  }

  private static ITargetPlatformService getTargetPlatformService() {
    String className = ITargetPlatformService.class.getName();
    PDECore pdeCore = PDECore.getDefault();
    return ( ITargetPlatformService )pdeCore.acquireService( className );
  }

  private URL getResourceURL( String resource ) throws IOException {
    Bundle testsBundle = Platform.getBundle( "org.eclipse.rap.tools.tests" );
    Path resourcePath = new Path( resource );
    URL resourceURL = FileLocator.find( testsBundle, resourcePath, null );
    return FileLocator.resolve( resourceURL );
  }

  private String createTargetDefinitionFile( String targetLocation ) throws Exception {
    String fakeRepositoryUri = createFakeRepository( targetLocation );
    Bundle bundle = Platform.getBundle( "org.eclipse.rap.tools.tests" );
    IPath targetTemplateFiletPath = new Path( targetLocation ).append( "target_template" );
    URL unresolvedFileUrl = FileLocator.find( bundle, targetTemplateFiletPath, null );
    URL targetTemplateFileUrl = FileLocator.resolve( unresolvedFileUrl );
    final File targetTemplateFile = new Path( targetTemplateFileUrl.getPath() ).toFile();
    String targetTemplateFileContent = Fixture.readContent( targetTemplateFile );
    String targetContent = targetTemplateFileContent.replace( "<template_uri>", fakeRepositoryUri );
    File targetFile = File.createTempFile( "test_target", "" );
    filesToDelete.add( targetFile );
    Fixture.writeContentToFile( targetFile, targetContent );
    return targetFile.toURI().toString();
  }

  private String createFakeRepository( String targetLocation ) throws Exception {
    URL repositoryDest = createRepositoryDest();
    Path repositoryDestPath = new Path( repositoryDest.getPath() );
    filesToDelete.add( repositoryDestPath.toFile() );
    AntRunner runner = new AntRunner();
    URL copyRepositoryScriptURL = getResourceURL( "copy_repository.ant.xml" );
    URL repositoryContentURL = getResourceURL( targetLocation );
    Path repositoryContentPath = new Path( repositoryContentURL.getPath() );
    Map< String, String > scriptProperties = getProperties( repositoryContentPath.toString(),
                                          repositoryDestPath.toString() );
    runAntTask( copyRepositoryScriptURL, runner, scriptProperties );
    return repositoryDest.toString();
  }

  private void runAntTask( URL copyRepositoryScriptURL,
                           AntRunner runner,
                           Map< String, String > scriptProperties ) throws CoreException
  {
    Path copyRepositoryPath = new Path( copyRepositoryScriptURL.getPath() );
    runner.setBuildFileLocation( copyRepositoryPath.toString() );
    runner.addUserProperties( scriptProperties );
    runner.run( new NullProgressMonitor() );
  }

  private Map< String, String > getProperties( String srcProp, String destProp ) {
    Map< String, String > result = new HashMap< String, String >();
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
}
