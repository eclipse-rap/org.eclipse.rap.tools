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

import java.io.*;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;


public final class TargetProvider {

  public static String createLocalTargetDefinition( String targetDefinitionURI,
                                                    IProgressMonitor monitor ) throws IOException
  {
    String result = null;
    monitor.subTask( IntroMessages.TargetProvider_Creating_Definition );
    String targetFileName = InstallTargetDialog.getTargetFileName();
    File targetDefinitionFile
      = createLocalTargetDefinitionFile( targetDefinitionURI, targetFileName );
    result = targetDefinitionFile.toURI().toString();
    return result;
  }

  private static File createLocalTargetDefinitionFile( String targetDefinitionURI,
                                                       String targetFileName ) throws IOException
  {
    File file = getLocalTargetDefinitionFile( targetFileName );
    copyRemoteToLocal( targetDefinitionURI, file );
    return file;
  }

  private static File getLocalTargetDefinitionFile( String targetFileName ) throws IOException {
    String stateLocationPath = IntroPlugin.getDefault().getStateLocation().toOSString();
    File file = new Path( stateLocationPath ).append( targetFileName ).toFile();
    cleanOldFile( file );
    file.createNewFile();
    return file;
  }

  private static void cleanOldFile( File file ) {
    if( file.exists() ) {
      file.delete();
    }
  }

  private static void copyRemoteToLocal( String targetDefinitionURI, File file )
    throws FileNotFoundException, IOException
  {
    FileOutputStream stream = null;
    OutputStreamWriter writer = null;
    try {
      stream = new FileOutputStream( file );
      writer = new OutputStreamWriter( stream );
      writer.write( loadRemoteContent( targetDefinitionURI ) );
      writer.flush();
    } finally {
      if( writer != null ) {
        writer.close();
      }
      if( stream != null ) {
        stream.close();
      }
    }
  }

  private static String loadRemoteContent( String targetDefinitionURI ) throws IOException {
    StringBuilder result = new StringBuilder();
    URL uri = new URL( targetDefinitionURI );
    InputStreamReader is = new InputStreamReader( uri.openStream() );
    BufferedReader bufferedReader = new BufferedReader( is );
    try {
      String line = bufferedReader.readLine();
      while( line != null ) {
        result.append( line );
        line = bufferedReader.readLine();
      }
    } finally {
      bufferedReader.close();
    }
    return result.toString();
  }
}
