/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.ui.internal.intro;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;


public final class ErrorUtil {
  
  public static void show( final String message, final Throwable throwable ) {
    final IStatus status = createStatus( message, throwable );
    IntroPlugin.getDefault().getLog().log( status );
    Display display = Display.getCurrent();
    if( display == null ) {
      display = Display.getDefault();
    }
    display.asyncExec( new Runnable() {
      public void run() {
        ErrorDialog.openError( null, null, null, status );
      }
    } );
  }

  public static void log( final String message, final Throwable throwable ) {
    IStatus status = createStatus( message, throwable );
    IntroPlugin.getDefault().getLog().log( status );
  }
  
  private static IStatus createStatus( final String message, 
                                       final Throwable throwable ) 
  {
    Throwable cause = throwable;
    if( cause instanceof InvocationTargetException ) {
      cause = ( ( InvocationTargetException )cause ).getTargetException();
    }
    final IStatus result;
    if( throwable instanceof CoreException ) {
      result = ( ( CoreException )throwable ).getStatus();
    } else {
      String statusMessage = message;
      if( statusMessage == null ) {
        statusMessage = throwable.getMessage();
      }
      if( statusMessage == null ) {
        statusMessage = throwable.toString();
      }
      result = new Status( IStatus.ERROR, 
                           IntroPlugin.getPluginId(),
                           0,
                           statusMessage, 
                           throwable );
    }
    return result;
  }

  private ErrorUtil() {
    // prevent instantiation
  }
}
