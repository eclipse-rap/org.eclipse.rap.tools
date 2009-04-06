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

import java.lang.reflect.InvocationTargetException;
import java.util.Dictionary;

import org.eclipse.core.commands.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.osgi.framework.Bundle;

public class InstallRAPTargetHandler extends AbstractHandler {

  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    Shell shell = workbench.getActiveWorkbenchWindow().getShell();
    InstallTargetDialog dialog = new InstallTargetDialog( shell );
    int result = dialog.open();
    if( result == Window.OK ) {
      execute( dialog.getTargetDestination(), dialog.shouldSwitchTarget() );
    }
    return null;
  }

  public static void execute( final String targetDestination, 
                              final boolean switchTarget )
    throws ExecutionException
  {
// FIXME [rh] revise usage of ProgressMonitors here    
    IRunnableWithProgress runnable = new IRunnableWithProgress() {
      public void run( final IProgressMonitor monitor )
        throws InvocationTargetException, InterruptedException
      {
        try {
          TargetProvider.install( targetDestination, monitor );
          if( switchTarget ) {
            switchTarget( targetDestination, monitor );
          }
        } catch( CoreException e ) {
          throw new InvocationTargetException( e );
        }
      }
    };
    IProgressService service = PlatformUI.getWorkbench().getProgressService();
    try {
      service.busyCursorWhile( runnable );
    } catch( InvocationTargetException e ) {
      Throwable cause = e.getCause() == null ? e : e.getCause();
      String msg = IntroMessages.InstallRAPTargetHandler_InstallFailed;
      throw new ExecutionException( msg, cause );
    } catch( InterruptedException e ) {
      String msg = IntroMessages.InstallRAPTargetHandler_InstallInterrupted;
      throw new ExecutionException( msg );
    }
  }
  
  private static void switchTarget( final String targetDestination,
                                    final IProgressMonitor monitor ) 
    throws CoreException 
  {
    if( isPDECore35() ) {
      TargetSwitcher35.switchTarget( targetDestination, monitor );
    } else {
      TargetSwitcher34.switchTarget( targetDestination, monitor );
    }
  }

  private static boolean isPDECore35() {
    Bundle bundle = Platform.getBundle( "org.eclipse.pde.core" ); //$NON-NLS-1$
    Dictionary headers = bundle.getHeaders();
    String version = ( String )headers.get( "Bundle-Version" ); //$NON-NLS-1$
    return version.startsWith( "3.5" ); //$NON-NLS-1$
  }
}
