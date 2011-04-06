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
package org.eclipse.rap.ui.internal.launch.rwt.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;


public final class Fixture {
  
  public static ILaunchConfigurationWorkingCopy createRWTLaunchConfig() 
    throws CoreException 
  {
    DebugPlugin debugPlugin = DebugPlugin.getDefault();
    ILaunchManager manager = debugPlugin.getLaunchManager();
    ILaunchConfigurationType type = RWTLaunchConfig.getType();
    String name = manager.generateLaunchConfigurationName( "RWTLaunchConfig" );
    ILaunchConfigurationWorkingCopy result = type.newInstance( null, name );
    RWTLaunchConfig.setDefaults( result );
    return result;
  }
  
  public static void deleteAllRWTLaunchConfigs() throws CoreException {
    ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    ILaunchConfigurationType type = RWTLaunchConfig.getType();
    ILaunchConfiguration[] configurations = manager.getLaunchConfigurations( type );
    for( int i = 0; i < configurations.length; i++ ) {
      configurations[ i ].delete();
    }
  }
  
  public static ByteArrayInputStream toUtf8Stream( final String string ) {
    try {
      return new ByteArrayInputStream( string.getBytes( "UTF-8" ) ); //$NON-NLS-1$
    } catch( UnsupportedEncodingException uee ) {
      throw new RuntimeException( "Failed to encode string to UTF-8.", uee ); //$NON-NLS-1$
    }
  }

  public static byte[] readBytes( File file ) 
    throws IOException 
  {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      FileInputStream fileInputStream = new FileInputStream( file );
      try {
        int byteRead = fileInputStream.read();
        while( byteRead != -1 ) {
          outputStream.write( byteRead );
          byteRead = fileInputStream.read();
        }
        return outputStream.toByteArray();
      } finally {
        fileInputStream.close();
      }
    } finally {
      outputStream.close();
    }
  }

  private Fixture() {
    // prevent instantiation
  }
}
