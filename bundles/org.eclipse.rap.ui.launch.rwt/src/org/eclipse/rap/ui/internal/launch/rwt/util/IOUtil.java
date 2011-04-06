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

import java.io.*;
import java.nio.charset.Charset;
import java.text.MessageFormat;


public final class IOUtil {
  private static final int BUFFER_SIZE = 8192;
  private static final Charset UTF_8 = Charset.forName( "utf-8" ); //$NON-NLS-1$

  public static void copy( File source, File destination ) {
    try {
      InputStream inputStream = new FileInputStream( source );
      try {
        copy( inputStream, destination );
      } finally {
        inputStream.close();
      }
    } catch( IOException ioe ) {
      handleCopyException( source, destination, ioe );
    }
  }

  public static void copy( InputStream source, File destination ) {
    byte[] buffer = new byte[ 8192 ];
    destination.getParentFile().mkdirs();
    try {
      OutputStream outputStream = new FileOutputStream( destination, false );
      try {
        int bytesRead = source.read( buffer );
        while( bytesRead != -1 ) {
          outputStream.write( buffer, 0, bytesRead );
          bytesRead = source.read( buffer );
        }
      } finally {
        outputStream.close();
      }
    } catch( IOException ioe ) {
      handleCopyException( destination, ioe );
    }
  }
  
  public static String readContent( InputStream inputStream ) {
    StringBuffer result = new StringBuffer();
    InputStreamReader reader = new InputStreamReader( inputStream, UTF_8 );
    char[] buffer = new char[ BUFFER_SIZE ];
    try {
      int bytesRead = reader.read( buffer );
      while( bytesRead != -1 ) {
        result.append( buffer, 0, bytesRead );
        bytesRead = reader.read( buffer );
      }
    } catch( IOException ioe ) {
      handleReadException( ioe );
    }
    return result.toString();
  }

  public static void closeInputStream( InputStream inputStream ) {
    try {
      inputStream.close();
    } catch( IOException shouldNotHappen ) {
      String msg = "Failed to close input stream."; //$NON-NLS-1$
      throw new RuntimeException( msg, shouldNotHappen );
    }
  }

  private static void handleCopyException( File destination, IOException ioe ) {
    String text = "Failed to copy content to ''{0}''."; //$NON-NLS-1$
    String msg = MessageFormat.format( text, new Object[] { destination } );
    throw new RuntimeException( msg, ioe );
  }
  
  private static void handleCopyException( File source, 
                                           File destination, 
                                           IOException ioe ) 
  {
    String text = "Failed to copy file from ''{0}'' to ''{1}''."; //$NON-NLS-1$
    Object[] args = new Object[] { source, destination };
    String msg = MessageFormat.format( text, args );
    throw new RuntimeException( msg, ioe );
  }
  
  private static void handleReadException( IOException ioe ) {
    String msg = "Failed to read from input stream."; //$NON-NLS-1$
    throw new RuntimeException( msg, ioe );
  }
  
  private IOUtil() {
    // prevent instantiation
  }
}
