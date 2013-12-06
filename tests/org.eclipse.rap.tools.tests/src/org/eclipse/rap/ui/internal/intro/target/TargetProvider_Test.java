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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.rap.ui.tests.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TargetProvider_Test {

  private List<File> filesToDelete;

  @Before
  public void setUp() throws Exception {
    filesToDelete = new ArrayList<File>();
  }

  @After
  public void tearDown() throws Exception {
    for( File fileToDelete : filesToDelete ) {
      Fixture.deleteDirectory( fileToDelete );
    }
  }

  @Test
  public void testCreateLocalTargetDefinition() throws Exception {
    File tempFile = File.createTempFile( "test.target", "" );
    String tempFileURI = tempFile.toURI().toString();
    filesToDelete.add( tempFile );

    String targetDefURI = TargetProvider.createLocalTargetDefinition( tempFileURI,
                                                                      new NullProgressMonitor() );

    File targetDefFile = new Path( new URL( targetDefURI ).getFile() ).toFile();
    filesToDelete.add( targetDefFile );
    assertTrue( targetDefFile.exists() );
  }

  @Test( expected = FileNotFoundException.class )
  public void testCreateLocalTargetDefinition_withMissingRemoteFile() throws Exception {
    File tempFile = File.createTempFile( "test.target", "" );
    String tempFileURI = tempFile.toURI().toString();
    assertTrue( tempFile.delete() );

    TargetProvider.createLocalTargetDefinition( tempFileURI, new NullProgressMonitor() );
  }

  @Test
  public void testTargetDefinitionContent() throws Exception {
    String testContent = "Target Definition Test";
    File tempFile = File.createTempFile( "test.target", "" );
    Fixture.writeContentToFile( tempFile, testContent );
    String tempFileURI = tempFile.toURI().toString();

    String targetDefURI = TargetProvider.createLocalTargetDefinition( tempFileURI,
                                                                      new NullProgressMonitor() );

    File targetDefFile = new Path( new URL( targetDefURI ).getFile() ).toFile();
    String targetDefinitionContent = Fixture.readContent( targetDefFile );
    filesToDelete.add( targetDefFile );
    assertTrue( testContent.equals( targetDefinitionContent ) );
  }

  @Test( expected = MalformedURLException.class )
  public void testNullParameter() throws Exception {
    TargetProvider.createLocalTargetDefinition( null, new NullProgressMonitor() );
  }

}
