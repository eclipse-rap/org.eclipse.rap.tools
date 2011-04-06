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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rap.ui.internal.launch.rwt.tests.AssertUtil;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;


public class IOUtil_Test extends TestCase {
  
  private File tempDestination;
  private File tempSource;

  public void testCopyFromFileToFile() throws IOException {
    byte[] content = new byte[] { 0, 1, 2, 3, 4 };
    writeFile( tempSource, content );
    
    IOUtil.copy( tempSource, tempDestination );
    
    AssertUtil.assertEquals( content, Fixture.readBytes( tempDestination ) );
  }

  public void testCopyFromFileToFileInNonExistingDir() throws IOException {
    byte[] content = new byte[] { 0, 1, 2, 3, 4 };
    writeFile( tempSource, content );
    String tempDir = getTempDir();
    File nonExistingDir = new File( tempDir, "dir-" + System.currentTimeMillis() );
    File destination = new File( nonExistingDir, "rwt-test-file.tmp" );
    destination.deleteOnExit();
    
    IOUtil.copy( tempSource, destination );
    
    AssertUtil.assertEquals( content, Fixture.readBytes( destination ) );
    destination.delete();
  }
  
  public void testCopyFromNonExistingFile() {
    File nonExistingFile = new File( getTempDir(), "does/not/exist" );
    File destination = new File( getTempDir(), "rwt-test-file.tmp" );
    destination.deleteOnExit();

    try {
      IOUtil.copy( nonExistingFile, destination );
    } catch( RuntimeException expected ) {
    }
    destination.delete();
  }

  public void testCopyFromStreamToFile() throws IOException {
    byte[] content = new byte[] { 0, 1, 2, 3, 4 };
    
    IOUtil.copy( new ByteArrayInputStream( content ), tempDestination );
    
    AssertUtil.assertEquals( content, Fixture.readBytes( tempDestination ) );
  }
  
  public void testCopyStreamWithIOExceptionInRead() {
    InputStream inputStream = new InputStream() {
      public int read() throws IOException {
        throw new IOException();
      }
    };
    
    try {
      IOUtil.copy( inputStream, tempDestination );
    } catch( RuntimeException expected ) {
    }
  }
  
  public void testReadContent() throws IOException {
    String content = "content";
    byte[] contentBytes = content.getBytes( "utf-8" );
    InputStream inputStream = new ByteArrayInputStream( contentBytes );
    
    String readContent = IOUtil.readContent( inputStream );
    
    assertEquals( content, readContent );
  }
  
  public void testReadContentWithNonMatchingEncoding() throws IOException {
    String content = "content with umlauts: äüöß";
    byte[] contentBytes = content.getBytes( "iso-8859-1" );
    InputStream inputStream = new ByteArrayInputStream( contentBytes );
    
    String readContent = IOUtil.readContent( inputStream );
    
    assertFalse( content.equals( readContent ) );
  }
  
  public void testCloseInputStreamWithoutException() {
    CloseableInputStream inputStream = new CloseableInputStream();
    
    IOUtil.closeInputStream( inputStream );
    
    assertTrue( inputStream.isClosed );
  }
  
  public void testCloseInputStreamWithException() {
    InputStream inputStream = new InputStream() {
      public int read() throws IOException {
        return 0;
      }
      public void close() throws IOException {
        throw new IOException( "Could not close..." );
      }
    };
    
    try {
      IOUtil.closeInputStream( inputStream );
      fail();
    } catch( RuntimeException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    tempDestination = createTempFile();
    tempDestination.deleteOnExit();
    tempSource = createTempFile();
    tempSource.deleteOnExit();
  }

  protected void tearDown() throws Exception {
    tempDestination.delete();
    tempSource.delete();
  }
  
  private String getTempDir() {
    return System.getProperty( "java.io.tmpdir" );
  }

  private static File createTempFile() throws IOException {
    return File.createTempFile( "rwt-", "temp" );
  }

  private static void writeFile( File file, byte[] content )
    throws IOException
  {
    FileOutputStream outputStream = new FileOutputStream( file, false );
    try {
      for( int i = 0; i < content.length; i++ ) {
        outputStream.write( content[ i ] );
      }
    } finally {
      outputStream.close();
    }
  }

  private static final class CloseableInputStream extends InputStream {
    boolean isClosed;
  
    public int read() throws IOException {
      return 0;
    }
  
    public void close() throws IOException {
      isClosed = true;
    }
  }
}
