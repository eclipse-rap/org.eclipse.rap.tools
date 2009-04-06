/*******************************************************************************
 * Copyright (c) 2009 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.tests;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.*;


public final class Fixture {
  
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

  private Fixture() {
    // prevent instantiation
  }
}
