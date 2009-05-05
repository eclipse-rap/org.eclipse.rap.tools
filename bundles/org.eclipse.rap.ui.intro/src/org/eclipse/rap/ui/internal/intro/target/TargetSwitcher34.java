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
package org.eclipse.rap.ui.internal.intro.target;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.rap.ui.internal.intro.ErrorUtil;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;


public final class TargetSwitcher34 {

  private static final String TARGET_MODEL
    = "org.eclipse.pde.internal.core.target.TargetModel"; //$NON-NLS-1$
  private static final String LOAD_TARGET_OPERATION
    = "org.eclipse.pde.internal.core.LoadTargetOperation"; //$NON-NLS-1$
  private static final String I_TARGET
   = "org.eclipse.pde.internal.core.itarget.ITarget"; //$NON-NLS-1$

  private static final String TARGET_FILE = "target/rap.target"; //$NON-NLS-1$
  private static final String ECLIPSE = "eclipse"; //$NON-NLS-1$

  public static void switchTarget( final String targetDestination,
                                   final IProgressMonitor monitor )
    throws CoreException
  {
    Object targetModel = getTargetModel34( targetDestination );
    Boolean isLoaded
      = ( Boolean )invoke( targetModel, "isLoaded", new Object[ 0 ] ); //$NON-NLS-1$
    if( isLoaded.booleanValue() ) {
      // ITarget target = targetModel.getTarget();
      Object target = invoke( targetModel, "getTarget", new Object[ 0 ] ); //$NON-NLS-1$
      // LoadTargetOperation operation = new LoadTargetOperation( target );
      IWorkspaceRunnable operation
        = ( IWorkspaceRunnable )construct( LOAD_TARGET_OPERATION,
                                           new Object[] { target } );
      ResourcesPlugin.getWorkspace().run( operation, monitor );
    }
  }

  private static Object getTargetModel34( final String targetDestination )
    throws CoreException
  {
    // new TargetModel();
    Object result = construct( TARGET_MODEL, null );
    URL entry = IntroPlugin.getDefault().getBundle().getEntry( TARGET_FILE );
    try {
      InputStream inputStream = new BufferedInputStream( entry.openStream() );
      try {
        // result.load( inputStream, true );
        invoke( result, "load", new Object[] { inputStream, Boolean.TRUE } ); //$NON-NLS-1$
      } finally {
        inputStream.close();
      }
    } catch( IOException e ) {
      String msg = IntroMessages.InstallRAPTargetHandler_SwitchTargetFailed;
      IStatus status = ErrorUtil.createErrorStatus( msg, e );
      throw new CoreException( status );
    }
    File path = new File( targetDestination, ECLIPSE );
    // result.getTarget().getLocationInfo().setPath( path.toString() );
    Object target = invoke( result, "getTarget", new Object[ 0 ] ); //$NON-NLS-1$
    Object locationInfo = invoke( target, "getLocationInfo", new Object[ 0 ] ); //$NON-NLS-1$
    invoke( locationInfo, "setPath", new Object[] { path.toString() } ); //$NON-NLS-1$
    return result;
  }

  private static Object construct( final String className, Object[] params ) {
    try {
      Object result;
      ClassLoader loader = TargetSwitcher34.class.getClassLoader();
      Class clazz = loader.loadClass( className );
      if( params == null ) {
        result = clazz.newInstance();        
      } else {
        Class[] types = getParamTypes( params );
        Constructor constructor = clazz.getConstructor( types );
        result = constructor.newInstance( params );
      }
      return result;
    } catch( Exception e ) {
      throw new RuntimeException( e );
    } 
  }

  private static Object invoke( final Object object, 
                                final String methodName, 
                                final Object[] params )
  {
    Class[] types = getParamTypes( params );
    try {
      Method method = object.getClass().getMethod( methodName, types );
      return method.invoke( object, params );
    } catch( Exception e ) {
      throw new RuntimeException( e );
    }
  }

  private static Class[] getParamTypes( final Object[] params ) {
    Class[] types = new Class[ params.length ];
    ClassLoader loader = TargetSwitcher34.class.getClassLoader();
    Class iTargetClass;
    try {
      iTargetClass = loader.loadClass( I_TARGET );
    } catch( ClassNotFoundException e ) {
      throw new RuntimeException( e );
    }
    for( int i = 0; i < types.length; i++ ) {
      types[ i ] = params[ i ].getClass();
      if( types[ i ].equals( Boolean.class ) ) {
        types[ i ] = Boolean.TYPE;
      } else if( InputStream.class.isAssignableFrom( types[ i ] ) ) {
        types[ i ] = InputStream.class;
      } else if( iTargetClass.isAssignableFrom( types[ i ] ) ) {
        types[ i ] = iTargetClass;
      }
    }
    return types;
  }

  private TargetSwitcher34() {
    // prevent instantiation
  }
}
