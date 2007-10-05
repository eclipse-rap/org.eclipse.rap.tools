/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.target;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.pde.internal.core.ICoreConstants;
import org.eclipse.pde.internal.core.LoadTargetOperation;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.itarget.ILocationInfo;
import org.eclipse.pde.internal.core.itarget.ITargetModel;
import org.eclipse.pde.internal.core.target.TargetModel;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.statushandlers.StatusManager;

// ERROR HANDLING !!!!!
public class InstallRAPTargetHandler extends AbstractHandler {

  final String TARGET_FILE = "target/rap.target"; //$NON-NLS-1$

  public Object execute( ExecutionEvent event ) throws ExecutionException {
    Shell workbenchShell = PlatformUI.getWorkbench()
      .getActiveWorkbenchWindow()
      .getShell();
    InstallTargetDialog installDialog = new InstallTargetDialog( workbenchShell );
    int result = installDialog.open();
    if( result == Dialog.OK ) {
      installTarget( installDialog.getTargetDestination() );
      // switch target if the users wants to
      if( installDialog.shouldSwitchTarget() ) {
        switchTarget();
      }
    }
    return null;
  }

  private void installTarget( final String targetDestination ) {
    IRunnableWithProgress run = new IRunnableWithProgress() {

      public void run( IProgressMonitor monitor )
        throws InvocationTargetException, InterruptedException
      {
        try {
          TargetProvider.setTargetDestination( targetDestination );
          TargetProvider.install( monitor );
        } catch( CoreException e ) {
          IStatus s = new Status( IStatus.ERROR,
                                  IntroPlugin.PLUGIN_ID,
                                  e.getMessage(),
                                  e );
          StatusManager.getManager().handle( s,
                                             StatusManager.SHOW
                                                 | StatusManager.LOG );
        }
      }
    };
    IProgressService service = PlatformUI.getWorkbench().getProgressService();
    try {
      service.busyCursorWhile( run );
    } catch( InvocationTargetException e ) {
    } catch( InterruptedException e ) {
    }
  }

  private void switchTarget() {
    Preferences fPreferences = PDECore.getDefault().getPluginPreferences();
    fPreferences.setValue( ICoreConstants.TARGET_PROFILE,
                           "id:org.eclipse.rap.target" ); //$NON-NLS-1$
    doLoadTarget();
  }

  private void doLoadTarget() {
    IRunnableWithProgress run = new IRunnableWithProgress() {

      public void run( IProgressMonitor monitor )
        throws InvocationTargetException, InterruptedException
      {
        try {
          ITargetModel model = getTargetModel();
          if( !model.isLoaded() ) {
            return;
          }
          LoadTargetOperation op = new LoadTargetOperation( model.getTarget() );
          ResourcesPlugin.getWorkspace().run( op, monitor );
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

  protected ITargetModel getTargetModel() {
    ITargetModel t = new TargetModel();
    URL entry = IntroPlugin.getDefault().getBundle().getEntry( TARGET_FILE );
    InputStream is;
    try {
      is = new BufferedInputStream( entry.openStream() );
      t.load( is, true );
    } catch( IOException e ) {
      e.printStackTrace();
    } catch( CoreException e ) {
      e.printStackTrace();
    }
    t.getTarget().getLocationInfo().setPath( TargetProvider.getTargetDest()
                                             + File.separatorChar
                                             + "eclipse" ); //$NON-NLS-1$
    return t;
  }
}
