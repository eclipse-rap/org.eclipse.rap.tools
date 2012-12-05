/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
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
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.rap.ui.tests.Fixture;


public class TargetProviderTest extends TestCase {

  private List< File > filesToDelete;

  @Override
  protected void setUp() throws Exception {
    filesToDelete = new ArrayList< File >();
  }

  @Override
  protected void tearDown() throws Exception {
    for( File fileToDelete : filesToDelete ) {
      Fixture.deleteDirectory( fileToDelete );
    }
  }

  public void testLocalTargetDefinitionCreated() throws Exception {
    final File tempFile = File.createTempFile( "test.target", "" );
    String tempFileURI = tempFile.toURI().toString();
    filesToDelete.add( tempFile );
    
    String targetDefinitionURI
      = TargetProvider.createLocalTargetDefinition( tempFileURI, new NullProgressMonitor() );
    File targetDefinitionFile = new Path( new URL( targetDefinitionURI ).getFile() ).toFile();
    filesToDelete.add( targetDefinitionFile );
    
    assertTrue( targetDefinitionFile.exists() );
  }

  public void testMissingRemoteFile() throws Exception {
    boolean exceptionThrown = false;
    final File tempFile = File.createTempFile( "test.target", "" );
    String tempFileURI = tempFile.toURI().toString();
    
    assertTrue( tempFile.delete() );
    try {
      TargetProvider.createLocalTargetDefinition( tempFileURI, new NullProgressMonitor() );
    } catch( FileNotFoundException e ) {
      exceptionThrown = true;
    }
    
    assertTrue( exceptionThrown );
  }

  public void testTargetDefinitionContent() throws Exception {
    String testContent = "Target Definition Test";
    final File tempFile = File.createTempFile( "test.target", "" );
    Fixture.writeContentToFile( tempFile, testContent );
    String tempFileURI = tempFile.toURI().toString();
    
    String targetDefinitionURI 
      = TargetProvider.createLocalTargetDefinition( tempFileURI, new NullProgressMonitor() );
    File targetDefinitionFile = new Path( new URL( targetDefinitionURI ).getFile() ).toFile();
    String targetDefinitionContent = Fixture.readContent( targetDefinitionFile );
    filesToDelete.add( targetDefinitionFile );
    
    assertTrue( testContent.equals( targetDefinitionContent ) );
  }
  
  public void testNullParameter() throws Exception {
    boolean exceptionThrown = false;
    
    try {
      TargetProvider.createLocalTargetDefinition( null, new NullProgressMonitor() );
    } catch( MalformedURLException e ) {
      exceptionThrown = true;
    }
    
    assertTrue( exceptionThrown );
  }
}
