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
import java.io.IOException;

import junit.framework.TestCase;


public class FileUtil_Test extends TestCase {
  
  private File directory;
  
  public void testDeleteWithExistingDirectory() {
    directory.mkdirs();
    
    FileUtil.delete( directory );
    
    assertFalse( directory.exists() );
  }
  
  public void testDeleteWithSubDirectories() {
    File subDirectory = new File( directory, "subdir" );
    subDirectory.mkdirs();
    
    FileUtil.delete( directory );
    
    assertFalse( directory.exists() );
  }
  
  public void testDeleteWithSubDirectoriesAndFiles() throws IOException {
    File subDirectory = new File( directory, "subdir" );
    subDirectory.mkdirs();
    File fileInDirectory = createTempFile( directory );
    File fileInSubDirectory = createTempFile( subDirectory );
    
    boolean deleted = FileUtil.delete( directory );
    
    assertTrue( deleted );
    assertFalse( directory.exists() );
    assertFalse( subDirectory.exists() );
    assertFalse( fileInDirectory.exists() );
    assertFalse( fileInSubDirectory.exists() );
  }
  
  public void testDeleteWithFile() throws IOException {
    directory.mkdirs();
    File file = createTempFile( directory );
    
    boolean deleted = FileUtil.delete( file );
    
    assertTrue( deleted );
    assertTrue( directory.exists() );
    assertFalse( file.exists() );
  }

  public void testDeleteWithNonExistingDirectory() {
    directory.mkdirs();
    File subDirectory = new File( directory, "non-existing" );
    
    boolean deleted = FileUtil.delete( subDirectory );
    
    assertFalse( deleted );
    assertFalse( subDirectory.exists() );
  }
  
  protected void setUp() throws Exception {
    File tempDirectory = new File( System.getProperty( "java.io.tmpdir" ) );
    directory = new File( tempDirectory, "testdir" );
  }
  
  protected void tearDown() throws Exception {
    FileUtil.delete( directory );
  }

  private static File createTempFile( File directory ) throws IOException {
    return File.createTempFile( "test", "tmp", directory );
  }
}
