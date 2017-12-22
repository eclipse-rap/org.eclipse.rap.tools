/*******************************************************************************
 * Copyright (c) 2011, 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.intro.internal.target;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.rap.tools.internal.tests.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;


@SuppressWarnings( "restriction" )
public class TargetSwitcher_Test {

  private final static String TARGET_LOCATION = "/test_target";
  private final static String LATEST_TARGET_LOCATION = "/latest_target";
  private ITargetHandle[] initialTargets;
  private List<File> filesToDelete;

  @Before
  public void setUp() throws Exception {
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    initialTargets = targetPlatformService.getTargets( new NullProgressMonitor() );
    filesToDelete = new ArrayList<File>();
  }

  @After
  public void tearDown() throws Exception {
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    ITargetHandle[] currentTargets = targetPlatformService.getTargets( new NullProgressMonitor() );
    // Delete all available targets
    for( ITargetHandle currentTarget : currentTargets ) {
      targetPlatformService.deleteTarget( currentTarget );
    }
    // Add initial targets
    for( ITargetHandle initialTarget : initialTargets ) {
      ITargetDefinition targetDefinition = initialTarget.getTargetDefinition();
      targetPlatformService.saveTargetDefinition( targetDefinition );
    }
    // Deletes all files
    for( File file : filesToDelete ) {
      Fixture.deleteDirectory( file );
    }
  }

  @Test
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
      ITargetDefinition targetDefinition = null;
      // Note: Since Luna M7 the initial workspaceTargetHandle is null
      if( workspaceTargetHandle != null ) {
        targetDefinition = workspaceTargetHandle.getTargetDefinition();
      }
      TargetSwitcher.switchTarget( targetDefinition );
    }
  }

  @Test
  public void testInstall_whenTargetAvailable() throws Exception {
    String targetFileUri = createTargetDefinitionFile( TARGET_LOCATION );

    ITargetDefinition targetDefinition = TargetSwitcher.install( targetFileUri,
                                                                 false,
                                                                 new NullProgressMonitor() );

    assertNotNull( targetDefinition );
    List<ITargetHandle> availableTargets
      = asList( getTargetPlatformService().getTargets( new NullProgressMonitor() ) );
    assertTrue( availableTargets.contains( targetDefinition.getHandle() ) );
  }

  @Test
  public void testInstall() throws Exception {
    String targetFileUri = createTargetDefinitionFile( TARGET_LOCATION );

    ITargetDefinition targetDefinition = TargetSwitcher.install( targetFileUri,
                                                                 false,
                                                                 new NullProgressMonitor() );

    assertNotNull( targetDefinition );
    assertTrue( targetDefinition.isResolved() );
    assertEquals( 1, targetDefinition.getBundles().length );
  }

  @Test
  public void testInstall_withOldTargetAndLatestTargetFileLoaded() throws Exception {
    String targetFileUri = createTargetDefinitionFile( TARGET_LOCATION );
    createTargetDefinitionFile( LATEST_TARGET_LOCATION );

    ITargetDefinition targetDefinition = TargetSwitcher.install( targetFileUri,
                                                                 false,
                                                                 new NullProgressMonitor() );

    assertNotNull( targetDefinition );
    assertTrue( targetDefinition.isResolved() );
    assertEquals( 1, targetDefinition.getBundles().length );
  }

  @Test
  public void testInstall_twice() throws Exception {
    String p2RepoURI = createTargetDefinitionFile( TARGET_LOCATION );
    ITargetPlatformService targetPlatformService = getTargetPlatformService();
    TargetSwitcher.install( p2RepoURI, false, new NullProgressMonitor() );
    ITargetHandle[] targets1 = targetPlatformService.getTargets( new NullProgressMonitor() );

    // Second installation
    ITargetDefinition targetDefinition2 = TargetSwitcher.install( p2RepoURI,
                                                                  false,
                                                                  new NullProgressMonitor() );

    assertTrue( targetDefinition2.isResolved() );
    assertEquals( 1, targetDefinition2.getBundles().length );
    ITargetHandle[] targets2 = targetPlatformService.getTargets( new NullProgressMonitor() );
    assertEquals( targets1.length, targets2.length );
  }

  @Test
  public void testInstall_targetConfiguration() throws Exception {
    String VM_ARGS = "-Dosgi.noShutdown=true -Declipse.ignoreApp=true"; //$NON-NLS-1$
    String PROGRAM_ARGS = "-console -consolelog"; //$NON-NLS-1$
    String p2RepoURI = createTargetDefinitionFile( TARGET_LOCATION );

    ITargetDefinition targetDefinition = TargetSwitcher.install( p2RepoURI,
                                                                 false,
                                                                 new NullProgressMonitor() );

    assertNotNull( targetDefinition );
    assertEquals( VM_ARGS, targetDefinition.getVMArguments() );
    assertEquals( PROGRAM_ARGS, targetDefinition.getProgramArguments() );
  }

  private static ITargetPlatformService getTargetPlatformService() {
    return PDECore.getDefault().acquireService( ITargetPlatformService.class );
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
    File targetTemplateFile = new Path( targetTemplateFileUrl.getPath() ).toFile();
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
    Map<String, String> scriptProperties = getProperties( repositoryContentPath.toString(),
                                                          repositoryDestPath.toString() );
    runAntTask( copyRepositoryScriptURL, runner, scriptProperties );
    return repositoryDest.toString();
  }

  private void runAntTask( URL copyRepositoryScriptURL,
                           AntRunner runner,
                           Map<String, String> scriptProperties ) throws CoreException
  {
    Path copyRepositoryPath = new Path( copyRepositoryScriptURL.getPath() );
    runner.setBuildFileLocation( copyRepositoryPath.toString() );
    runner.addUserProperties( scriptProperties );
    runner.run( new NullProgressMonitor() );
  }

  private Map<String, String> getProperties( String srcProp, String destProp ) {
    Map<String, String> result = new HashMap<String, String>();
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
