/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Innoopract Informationssysteme GmbH - initial API and implementation
 *   EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro.target;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.runtime.*;
import org.eclipse.rap.ui.internal.intro.ErrorUtil;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;


public final class TargetProvider {
  
  private static final String COPY_ANT_XML = "copy.ant.xml"; //$NON-NLS-1$
  private static final String CHARSET_NAME = "ISO-8859-1"; //$NON-NLS-1$

  private static final String BUNDLE_VERSION = "Bundle-Version"; //$NON-NLS-1$

  private static final String DEFAULT_TARGET_DEST 
    = "org.eclipse.rap.target-" + TargetProvider.getRAPRuntimeVersion(); //$NON-NLS-1$

  public static String getRAPRuntimeVersion() {
    // For now, assume that the major and minor version numbers of runtime 
    // and tooling version are in sync
    Dictionary headers = IntroPlugin.getDefault().getBundle().getHeaders();
    String value = ( String )headers.get( BUNDLE_VERSION );
    Version version; 
    if( value != null ) {
      version = new Version( value );
    } else {
      version = new Version( "0.0.0.0" ); //$NON-NLS-1$
    }
    return version.getMajor() + "." + version.getMinor(); //$NON-NLS-1$
  }

  public static String getDefaultTargetDestination() {
    URL configLocation = Platform.getConfigurationLocation().getURL();
    File targetDest = new File( configLocation.getFile(), DEFAULT_TARGET_DEST );
    return targetDest.toString();
  }
  
  public static void install( final String targetDest,
                              final IProgressMonitor monitor ) 
    throws CoreException 
  {
    monitor.beginTask( IntroMessages.TargetProvider_Installing, 
                       IProgressMonitor.UNKNOWN );
    try {
      checkTargetDestination( targetDest );
      File scriptFile = createScriptFile();
      AntRunner runner = new AntRunner();
      runner.setBuildFileLocation( scriptFile.getAbsolutePath() );
      runner.addUserProperties( getProperties( targetDest ) );
      runner.run( new NullProgressMonitor() );
    } finally {
      monitor.done();
    }
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
      IStatus status = ErrorUtil.createErrorStatus( msg, e );
      throw new CoreException( status );
    }
    return file;
  }
  
  private static void checkTargetDestination( final String location ) 
    throws CoreException 
  {
    File file = new File( location );
    file.mkdirs();
    boolean valid = file.canWrite() && file.isDirectory();
    if( !valid ) {
      String text = IntroMessages.TargetProvider_InvalidTargetDest;
      Object[] args = new Object[] { file.toString() };
      String msg = MessageFormat.format( text, args ); 
      IStatus status = ErrorUtil.createErrorStatus( msg, null );
      throw new CoreException( status );
    }
  }

  private static Map getProperties( final String targetDest ) 
    throws CoreException 
  {
    Map result = new HashMap();
    result.put( "src", getTargetSrc() ); //$NON-NLS-1$
    result.put( "dest", targetDest ); //$NON-NLS-1$
    return result;
  }

  private static String getTargetSrc() throws CoreException {
    URL result;
    try {
      Bundle bundle = IntroPlugin.getDefault().getBundle();
      URL targetEntry = bundle.getEntry( "target/target.zip" ); //$NON-NLS-1$
      if( targetEntry == null ) {
        String msg = IntroMessages.TargetProvider_ArchiveNotFound; 
        IStatus status = ErrorUtil.createErrorStatus( msg, null );
        throw new CoreException( status );
      }
      result = FileLocator.resolve( targetEntry );
    } catch( IOException e ) {
      String msg = IntroMessages.TargetProvider_SourceNotFound; 
      IStatus status = ErrorUtil.createErrorStatus( msg, e );
      throw new CoreException( status );
    }
    return result.getFile();
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
}
