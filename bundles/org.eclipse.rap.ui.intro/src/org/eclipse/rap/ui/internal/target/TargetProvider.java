/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.target;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;
import org.osgi.framework.Bundle;


public final class TargetProvider {
  
  private static final String COPY_ANT_XML = "copy.ant.xml"; //$NON-NLS-1$
  private static final String CHARSET_NAME = "ISO-8859-1"; //$NON-NLS-1$

  private static String targetDestination;
  
  public static void install( final IProgressMonitor monitor ) 
    throws CoreException 
  {
    File scriptFile = createScriptFile();
    AntRunner runner = new AntRunner();
    runner.setBuildFileLocation( scriptFile.getAbsolutePath() );
    runner.addUserProperties( getProperties() );
    runner.run( monitor );
  }
  
  private static File createScriptFile() throws CoreException {
    String path = IntroPlugin.getDefault().getStateLocation().toOSString();
    File file = new File( path, COPY_ANT_XML );
    if( file.exists() ) {
      file.delete();
    }
    try {
      FileOutputStream stream = new FileOutputStream( file );
      try {
        Charset charset = Charset.forName( CHARSET_NAME );
        OutputStreamWriter writer = new OutputStreamWriter( stream, charset );
        try {
          writer.write( loadContent() );
        } finally {
          writer.close();
        }
      } finally {
        stream.close();
      }
    } catch( IOException e ) {
      String msg = IntroMessages.TargetProvider_FailureCreateScript;
      Status status = new Status( IStatus.ERROR, IntroPlugin.PLUGIN_ID, msg, e );
      throw new CoreException( status );
    }
    return file;
  }
  
  private static Map getProperties() throws CoreException {
    Map result = new HashMap();
    result.put( "src", getTargetSrc() ); //$NON-NLS-1$
    result.put( "dest", getTargetDest() ); //$NON-NLS-1$
    return result;
  }

  private static String getTargetSrc() throws CoreException {
    URL result;
    try {
      Bundle bundle = IntroPlugin.getDefault().getBundle();
      URL targetEntry = bundle.getEntry( "target/target.zip" ); //$NON-NLS-1$
      if( targetEntry == null ) {
        String msg = IntroMessages.TargetProvider_ArchiveNotFound; 
        Status status = new Status( IStatus.ERROR, IntroPlugin.PLUGIN_ID, msg, null );
        throw new CoreException( status );
      }
      result = FileLocator.resolve( targetEntry );
    } catch( IOException e ) {
      String msg = IntroMessages.TargetProvider_SourceNotFound; 
      Status status = new Status( IStatus.ERROR, IntroPlugin.PLUGIN_ID, msg, e );
      throw new CoreException( status );
    }
    return result.getFile();
  }

  public static String getTargetDest() {
    if( targetDestination == null ) {
      VariablesPlugin variablesPlugin = VariablesPlugin.getDefault();
      IStringVariableManager manager = variablesPlugin.getStringVariableManager();
      String variableName = RAPTargetDestVariableInitializer.VARIABLE_NAME;
      IValueVariable targetDest = manager.getValueVariable( variableName );
      targetDestination = targetDest.getValue();
    }
    return targetDestination;
  }
  
  private static String loadContent() throws IOException {
    InputStream stream 
      = TargetProvider.class.getResourceAsStream( COPY_ANT_XML );
    StringBuffer buffer = new StringBuffer();
    Charset charset = Charset.forName( CHARSET_NAME );
    InputStreamReader reader = new InputStreamReader( stream, charset );
    BufferedReader br = new BufferedReader( reader );
    try {
      int character = br.read();    
      while( character != -1 ) {
        buffer.append( ( char )character );
        character = br.read();
      }
    } finally {
      br.close();
    }
    return buffer.toString(); 
  }

  private TargetProvider() {
    // prevent instantiation
  }

  public static void setTargetDestination( String targetDest ) {
    targetDestination = targetDest;
  }
}
