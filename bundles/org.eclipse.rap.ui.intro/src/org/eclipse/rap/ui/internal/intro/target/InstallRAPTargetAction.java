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
package org.eclipse.rap.ui.internal.intro.target;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.statushandlers.StatusManager;

public final class InstallRAPTargetAction extends Action {

  private static final String INSTALL_COMMAND 
    = "org.eclipse.rap.ui.intro.installTarget"; //$NON-NLS-1$

  public void run() {
    IWorkbenchWindow workbenchWindow 
      =  PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IHandlerService handlerService 
      = ( IHandlerService )workbenchWindow.getService( IHandlerService.class );
    try {
      handlerService.executeCommand( INSTALL_COMMAND, null );
    } catch( CommandException e ) {
      String msg = "Failed execute command: " + INSTALL_COMMAND;
      Status status 
        = new Status( IStatus.ERROR, IntroPlugin.PLUGIN_ID, msg, e );
      int style = StatusManager.LOG | StatusManager.SHOW;
      StatusManager.getManager().handle( status, style );
    }
  }
}
