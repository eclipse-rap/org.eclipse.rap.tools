/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.target;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.commands.*;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.pde.internal.core.*;
import org.eclipse.pde.internal.core.itarget.ITarget;
import org.eclipse.pde.internal.core.itarget.ITargetModel;
import org.eclipse.pde.internal.core.target.TargetModel;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

// ERROR HANDLING !!!!!
public class InstallRAPTargetHandler extends AbstractHandler {

  private static final String TARGET_FILE = "target/rap.target"; //$NON-NLS-1$

  public Object execute( final ExecutionEvent event ) throws ExecutionException 
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    Shell shell = workbench.getActiveWorkbenchWindow().getShell();
    InstallTargetDialog dialog = new InstallTargetDialog( shell );
    int result = dialog.open();
    if( result == Dialog.OK ) {
      installTarget( dialog.getTargetDestination() );
      // switch target if the users wants to
      if( dialog.shouldSwitchTarget() ) {
        switchTarget();
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
          TargetProvider.setTargetDestination( targetDestination );
          TargetProvider.install( monitor );
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
      String msg = "Failed to install target platform.";
      throw new ExecutionException( msg, cause );
    } catch( InterruptedException e ) {
      // TODO [rh] exception handling
    }
  }

  private static void switchTarget() {
    IRunnableWithProgress run = new IRunnableWithProgress() {
    
      public void run( final IProgressMonitor monitor )
        throws InvocationTargetException, InterruptedException
      {
        try {
          ITargetModel model = getTargetModel();
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
    } catch( InterruptedException e ) {
    }
  }

  private static ITargetModel getTargetModel() 
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
    String path = TargetProvider.getTargetDestination()
                + File.separatorChar
                + "eclipse"; //$NON-NLS-1$
    targetModel.getTarget().getLocationInfo().setPath( path ); 
    return targetModel;
  }
}
