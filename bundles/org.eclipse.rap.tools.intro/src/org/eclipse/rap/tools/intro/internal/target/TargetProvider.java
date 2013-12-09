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
package org.eclipse.rap.tools.intro.internal.target;

import java.io.*;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.rap.tools.intro.internal.IntroPlugin;
import org.osgi.framework.Version;


public final class TargetProvider {

  public final static String TARGET_FILE_NAME
    = "rap-" + getVersion() + ".target"; //$NON-NLS-1$ //$NON-NLS-2$
  public final static String TARGET_REPOSITORY
    = "http://download.eclipse.org/rt/rap/targets/" + TARGET_FILE_NAME; //$NON-NLS-1$

  public static String createLocalTargetDefinition( String targetDefinitionURI,
                                                    IProgressMonitor monitor )
    throws IOException
  {
    monitor.subTask( IntroMessages.TargetProvider_Creating_Definition );
    File targetDefinitionFile
      = createLocalTargetDefinitionFile( targetDefinitionURI, TARGET_FILE_NAME );
    return targetDefinitionFile.toURI().toString();
  }

  public static String getVersion() {
    Version version = IntroPlugin.getDefault().getBundle().getVersion();
    return version.getMajor() + "." + version.getMinor(); //$NON-NLS-1$
  }

  private static File createLocalTargetDefinitionFile( String targetDefinitionURI,
                                                       String targetFileName )
    throws IOException
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
