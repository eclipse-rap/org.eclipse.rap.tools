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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

public class InstallRAPTargetAction extends Action {

  private static final String INSTALL_COMMAND = "org.eclipse.rap.ui.intro.installTarget"; //$NON-NLS-1$

  public void run() {
    IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
      .getActiveWorkbenchWindow();
    IHandlerService handlerService = ( IHandlerService )activeWorkbenchWindow
        .getService( IHandlerService.class );
    try {
      handlerService.executeCommand( INSTALL_COMMAND, null );
    } catch( ExecutionException e ) {
      e.printStackTrace();
    } catch( NotDefinedException e ) {
      e.printStackTrace();
    } catch( NotEnabledException e ) {
      e.printStackTrace();
    } catch( NotHandledException e ) {
      e.printStackTrace();
    }
  }
}
