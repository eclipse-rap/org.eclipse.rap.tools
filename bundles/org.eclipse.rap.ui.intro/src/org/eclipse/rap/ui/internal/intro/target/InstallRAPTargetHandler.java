/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
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
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.commands.*;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.internal.core.LoadTargetOperation;
import org.eclipse.pde.internal.core.itarget.ITarget;
import org.eclipse.pde.internal.core.itarget.ITargetModel;
import org.eclipse.pde.internal.core.target.TargetModel;
import org.eclipse.rap.ui.internal.intro.ErrorUtil;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class InstallRAPTargetHandler extends AbstractHandler {

  private static final String TARGET_FILE = "target/rap.target"; //$NON-NLS-1$

  public Object execute( final ExecutionEvent event ) throws ExecutionException 
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    Shell shell = workbench.getActiveWorkbenchWindow().getShell();
    InstallTargetDialog dialog = new InstallTargetDialog( shell );
    int result = dialog.open();
    if( result == Window.OK ) {
      String targetDestination = dialog.getTargetDestination();
      installTarget( targetDestination );
      // switch target if the users wants to
      if( dialog.shouldSwitchTarget() ) {
        switchTarget( targetDestination );
      }
    }
    return null;
  }

  private static void installTarget( final String targetDestination )
    throws ExecutionException 
  {
    IRunnableWithProgress runnable = new IRunnableWithProgress() {
      public void run( final IProgressMonitor monitor )
        throws InvocationTargetException, InterruptedException
      {
        try {
          TargetProvider.install( targetDestination, monitor );
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

  private static void switchTarget( final String targetDestination ) {
    IRunnableWithProgress run = new IRunnableWithProgress() {
      public void run( final IProgressMonitor monitor )
        throws InvocationTargetException, InterruptedException
      {
        try {
          ITargetModel model = getTargetModel( targetDestination );
          if( model.isLoaded() ) {
            ITarget target = model.getTarget();
            LoadTargetOperation operation = new LoadTargetOperation( target );
            ResourcesPlugin.getWorkspace().run( operation, monitor );
          } 
        } catch( IOException e ) { 
          throw new InvocationTargetException( e );
        } catch( CoreException e ) {
          throw new InvocationTargetException( e );
        } catch( OperationCanceledException e ) {
          throw new InterruptedException( e.getMessage() );
        } finally {
          monitor.done();
        }
      }
    };
    IProgressService service = PlatformUI.getWorkbench().getProgressService();
    try {
      service.runInUI( service, run, ResourcesPlugin.getWorkspace().getRoot() );
    } catch( InvocationTargetException e ) {
      String msg = IntroMessages.InstallRAPTargetHandler_SwitchTargetFailed;
      ErrorUtil.show( msg, e ); //$NON-NLS-1$
    } catch( InterruptedException e ) {
      String msg
        = IntroMessages.InstallRAPTargetHandler_SwitchTargetInterrupted;
      ErrorUtil.log( msg, e );
    }
  }

  private static ITargetModel getTargetModel( final String targetDestination ) 
    throws IOException, CoreException 
  {
    ITargetModel targetModel = new TargetModel();
    URL entry = IntroPlugin.getDefault().getBundle().getEntry( TARGET_FILE );
    InputStream is = new BufferedInputStream( entry.openStream() );
    try {
      targetModel.load( is, true );
    } finally {
      is.close();    
    }        
    File path = new File( targetDestination, "eclipse" ); //$NON-NLS-1$
    targetModel.getTarget().getLocationInfo().setPath( path.toString() ); 
    return targetModel;
  }
}
