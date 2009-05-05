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
package org.eclipse.rap.ui.internal.intro;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;


public final class ErrorUtil {
  
  public static void show( final String message, final Throwable throwable ) {
    final IStatus status = createErrorStatus( message, throwable );
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
    IStatus status = createErrorStatus( message, throwable );
    IntroPlugin.getDefault().getLog().log( status );
  }
  
  public static IStatus createErrorStatus( final String message, 
                                           final Throwable throwable ) 
  {
    Throwable cause = throwable;
    if( cause instanceof InvocationTargetException ) {
      cause = ( ( InvocationTargetException )cause ).getTargetException();
    }
    IStatus result;
    if( throwable instanceof CoreException ) {
      result = ( ( CoreException )throwable ).getStatus();
    } else {
      String statusMessage = message;
      if( statusMessage == null && throwable != null ) {
        if( throwable.getMessage() != null ) {
          statusMessage = throwable.getMessage();
        } else {
          statusMessage = throwable.toString();
        }
      }
      if( statusMessage == null ) {
        statusMessage = ""; //$NON-NLS-1$
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
