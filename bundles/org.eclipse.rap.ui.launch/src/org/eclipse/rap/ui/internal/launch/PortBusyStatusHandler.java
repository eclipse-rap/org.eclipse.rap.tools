/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import java.text.MessageFormat;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;

public final class PortBusyStatusHandler implements IStatusHandler {

  static final IStatus STATUS
    = new Status( IStatus.INFO, Activator.PLUGIN_ID, 601, "", null );


  public Object handleStatus( final IStatus status, final Object source )
    throws CoreException
  {
    RAPLaunchConfig config = ( RAPLaunchConfig )source;
    String title = "Port in use";
    String text 
      = "Port {0,number,#} is currently in use.\n" 
      + "Proceed with launching ''{1}''?";
    Object[] args = new Object[] {
      new Integer( config.getPort() ),
      config.getName()
    };
    String msg = MessageFormat.format( text, args );
    String[] buttons = new String[] {
      IDialogConstants.PROCEED_LABEL,
      IDialogConstants.CANCEL_LABEL
    };
    MessageDialog dialog = new MessageDialog( getShell(),
                                              title,
                                              null,
                                              msg,
                                              MessageDialog.QUESTION,
                                              buttons,
                                              0 );
    Boolean result;
    if( dialog.open() == IDialogConstants.OK_ID ) {
      result = Boolean.TRUE;
    } else {
      result = Boolean.FALSE;
    }
    return result;
  }

  public static Shell getShell() {
    Shell result = null;
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    if( window == null ) {
      IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
      if( windows.length > 0 ) {
        result = windows[ 0 ].getShell();
      }
    } else {
      result = window.getShell();
    }
    return result;
  }
}