/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro.commands;

import java.text.MessageFormat;

import org.eclipse.core.commands.*;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.rap.ui.internal.intro.ErrorUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/*
 * Called from the getting-started cheat sheet via the command extension
 * org.eclipse.rap.ui.internal.intro.commands.launchRAPApplication
 */
public final class LaunchRAPApplication extends AbstractHandler {

  private static final String RUN_COMMAND
    = "org.eclipse.rap.ui.launch.RAPLaunchShortcut.run"; //$NON-NLS-1$

  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    try {
      // Workaround for bug 218881: force the activation of the debug plug-in
      Bundle bundle = Platform.getBundle( "org.eclipse.debug.ui" ); //$NON-NLS-1$
      if( bundle != null ) {
        bundle.start();
      }
      Display.getCurrent().asyncExec( new Runnable() {
        public void run() {
          runHandler( event );
        }
      } );
    } catch( BundleException e ) {
      handleException( e );
    }
    return null;
  }

  private static void runHandler( final ExecutionEvent event ) {
    IWorkbenchWindow workbenchWindow
      = HandlerUtil.getActiveWorkbenchWindow( event );
    IHandlerService handlerService
      = ( IHandlerService )workbenchWindow.getService( IHandlerService.class );
    try {
      handlerService.executeCommand( RUN_COMMAND, null );
    } catch( CommandException e ) {
      handleException( e );
    }
  }

  private static void handleException( final Exception exception ) {
    String text = "Error while executing command ''{0}''"; //$NON-NLS-1$
    String msg = MessageFormat.format( text, new Object[]{ RUN_COMMAND } );
    ErrorUtil.show( msg, exception );
  }
}
