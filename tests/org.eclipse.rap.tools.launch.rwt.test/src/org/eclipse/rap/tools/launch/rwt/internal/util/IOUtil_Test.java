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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.eclipse.rap.tools.launch.rwt.internal.util.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class IOUtil_Test {

  private File tempDestination;
  private File tempSource;

  private File directory;

  @Before
  public void setUp() throws Exception {
    directory = new File( getTempDir(), "testdir" );
    directory.deleteOnExit();
    tempDestination = createTempFile();
    tempDestination.deleteOnExit();
    tempSource = createTempFile();
    tempSource.deleteOnExit();
  }

  @After
  public void tearDown() throws Exception {
    tempDestination.delete();
    tempSource.delete();
    IOUtil.delete( directory );
  }

  @Test
  public void testDelete_withExistingDirectory() {
    directory.mkdirs();

    IOUtil.delete( directory );

    assertFalse( directory.exists() );
  }

  @Test
  public void testDelete_withSubDirectories() {
    File subDirectory = new File( directory, "subdir" );
    subDirectory.mkdirs();

    IOUtil.delete( directory );

    assertFalse( directory.exists() );
  }

  @Test
  public void testDelete_withSubDirectoriesAndFiles() throws IOException {
    File subDirectory = new File( directory, "subdir" );
    subDirectory.mkdirs();
    File fileInDirectory = createTempFile( directory );
    File fileInSubDirectory = createTempFile( subDirectory );

    boolean deleted = IOUtil.delete( directory );

    assertTrue( deleted );
    assertFalse( directory.exists() );
    assertFalse( subDirectory.exists() );
    assertFalse( fileInDirectory.exists() );
    assertFalse( fileInSubDirectory.exists() );
  }

  @Test
  public void testDelete_withFile() throws IOException {
    directory.mkdirs();
    File file = createTempFile( directory );

    boolean deleted = IOUtil.delete( file );

    assertTrue( deleted );
    assertTrue( directory.exists() );
    assertFalse( file.exists() );
  }

  @Test
  public void testDelete_withNonExistingDirectory() {
    directory.mkdirs();
    File subDirectory = new File( directory, "non-existing" );

    boolean deleted = IOUtil.delete( subDirectory );

    assertFalse( deleted );
    assertFalse( subDirectory.exists() );
  }

  @Test
  public void testCopy_fromFileToFile() throws IOException {
    byte[] content = new byte[] { 0, 1, 2, 3, 4 };
    writeFile( tempSource, content );

    IOUtil.copy( tempSource, tempDestination );

    assertArrayEquals( content, Fixture.readBytes( tempDestination ) );
  }

  @Test
  public void testCopy_fromFileToFileInNonExistingDir() throws IOException {
    byte[] content = new byte[] { 0, 1, 2, 3, 4 };
    writeFile( tempSource, content );
    String tempDir = getTempDir();
    File nonExistingDir = new File( tempDir, "dir-" + System.currentTimeMillis() );
    File destination = new File( nonExistingDir, "rwt-test-file.tmp" );
    destination.deleteOnExit();

    IOUtil.copy( tempSource, destination );

    assertArrayEquals( content, Fixture.readBytes( destination ) );
    destination.delete();
  }

  @Test
  public void testCopy_fromNonExistingFile() {
    File nonExistingFile = new File( getTempDir(), "does/not/exist" );
    File destination = new File( getTempDir(), "rwt-test-file.tmp" );
    destination.deleteOnExit();

    try {
      IOUtil.copy( nonExistingFile, destination );
    } catch( RuntimeException expected ) {
    }
    destination.delete();
  }

  @Test
  public void testCopy_fromStreamToFile() throws IOException {
    byte[] content = new byte[] { 0, 1, 2, 3, 4 };

    IOUtil.copy( new ByteArrayInputStream( content ), tempDestination );

    assertArrayEquals( content, Fixture.readBytes( tempDestination ) );
  }

  @Test
  public void testCopy_fromStreamWithIOExceptionInRead() {
    InputStream inputStream = new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException();
      }
    };

    try {
      IOUtil.copy( inputStream, tempDestination );
    } catch( RuntimeException expected ) {
    }
  }

  @Test
  public void testReadContent() throws IOException {
    String content = "content";
    byte[] contentBytes = content.getBytes( "utf-8" );
    InputStream inputStream = new ByteArrayInputStream( contentBytes );

    String readContent = IOUtil.readContent( inputStream );

    assertEquals( content, readContent );
  }

  @Test
  public void testReadContent_withNonMatchingEncoding() throws IOException {
    String content = "content with umlauts: äüöß";
    byte[] contentBytes = content.getBytes( "iso-8859-1" );
    InputStream inputStream = new ByteArrayInputStream( contentBytes );

    String readContent = IOUtil.readContent( inputStream );

    assertFalse( content.equals( readContent ) );
  }

  @Test
  public void testCloseInputStream_withoutException() {
    CloseableInputStream inputStream = new CloseableInputStream();

    IOUtil.closeInputStream( inputStream );

    assertTrue( inputStream.isClosed );
  }

  @Test
  public void testCloseInputStream_withException() {
    InputStream inputStream = new InputStream() {
      @Override
      public int read() throws IOException {
        return 0;
      }
      @Override
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

  private String getTempDir() {
    return System.getProperty( "java.io.tmpdir" );
  }

  private static File createTempFile() throws IOException {
    return File.createTempFile( "rwt-", "temp" );
  }

  private static File createTempFile( File directory ) throws IOException {
    return File.createTempFile( "test", "tmp", directory );
  }

  private static void writeFile( File file, byte[] content )
    throws IOException
  {
    FileOutputStream outputStream = new FileOutputStream( file, false );
    try {
      for( byte element : content ) {
        outputStream.write( element );
      }
    } finally {
      outputStream.close();
    }
  }

  private static final class CloseableInputStream extends InputStream {
    boolean isClosed;

    @Override
    public int read() throws IOException {
      return 0;
    }

    @Override
    public void close() throws IOException {
      isClosed = true;
    }
  }

}
