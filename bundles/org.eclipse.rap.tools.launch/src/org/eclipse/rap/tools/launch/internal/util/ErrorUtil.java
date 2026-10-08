/*******************************************************************************
 * Copyright (c) 2007, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.internal.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.rap.tools.launch.internal.Activator;
import org.eclipse.swt.widgets.Display;


public final class ErrorUtil {

  public static void show( String message, Throwable throwable ) {
    Throwable cause = throwable;
    if( cause instanceof InvocationTargetException ) {
      cause = ( ( InvocationTargetException )cause ).getTargetException();
    }
    final IStatus status;
    if( cause instanceof CoreException ) {
      status = ( ( CoreException )cause ).getStatus();
    } else {
      String statusMessage = message;
      if( statusMessage == null ) {
        statusMessage = cause.getMessage();
      }
      if( statusMessage == null ) {
        statusMessage = cause.toString();
      }
      status = new Status( IStatus.ERROR, Activator.getPluginId(), 0, statusMessage, cause );
    }
    Activator.getDefault().getLog().log( status );
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

  private ErrorUtil() {
    // prevent instantiation
  }

}
