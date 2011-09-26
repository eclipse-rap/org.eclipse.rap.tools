/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;


public final class Fixture {
  
  private static final int BUFFER = 2048;
  public static final String PLUGIN_ID = "org.eclipse.rap.ui.tests"; //$NON-NLS-1$

  private static final String RAP_LAUNCHER 
    = "org.eclipse.rap.ui.launch.RAPLauncher"; //$NON-NLS-1$

  private static int uniqueId;

  public static ILaunchConfigurationWorkingCopy createRAPLaunchConfig() 
    throws CoreException 
  {
    DebugPlugin debugPlugin = DebugPlugin.getDefault();
    ILaunchManager manager = debugPlugin.getLaunchManager();
    ILaunchConfigurationType type 
      = manager.getLaunchConfigurationType( RAP_LAUNCHER );
    String name 
      = manager.generateUniqueLaunchConfigurationNameFrom( "RAPLaunchConfig" );
    return type.newInstance( null, name );
  }

  public static File createDirectory( final String directory ) {
    String workingDir = Platform.getInstanceLocation().getURL().getPath();
    String tempDirectory;
    if( directory == null ) {
      uniqueId++;
      tempDirectory = "tempTargetDest" + uniqueId;
    } else {
      tempDirectory = directory;
    }
    return new File( workingDir, tempDirectory );
  }

  public static void deleteDirectory( final File directory ) {
    if( directory.isDirectory() ) {
      File[] files = directory.listFiles();
      for( int i = 0; i < files.length; i++ ) {
        deleteDirectory( files[ i ] );
      }
    }
    directory.delete();
  }

  public static void writeContentToFile( File fileToWrite, String content ) throws IOException {
    FileWriter fileWriter = null;
    try {
      fileWriter = new FileWriter( fileToWrite );
      BufferedWriter out = new BufferedWriter( fileWriter );
      out.write( content );
      out.flush();
    } finally {
      if( fileWriter != null ) {
        fileWriter.close();
      }
    }
  }

  public static String readContent( File file ) throws IOException {
    FileInputStream is = new FileInputStream( file );
    StringBuilder input = new StringBuilder();
    byte[] bytes = new byte[ BUFFER ];
    try {
      int read;
      while( ( read = is.read( bytes ) ) != -1 ) {
        input.append( new String( bytes, 0, read ) );
      }
    } finally {
      is.close();
    }
    return input.toString();
  }
  
  private Fixture() {
    // prevent instantiation
  }
}
