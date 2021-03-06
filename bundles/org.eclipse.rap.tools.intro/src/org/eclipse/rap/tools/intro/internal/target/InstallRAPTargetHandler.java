/*******************************************************************************
 * Copyright (c) 2007, 2017 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.intro.internal.target;

import org.eclipse.core.commands.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.tools.intro.internal.ErrorUtil;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


public class InstallRAPTargetHandler extends AbstractHandler {

  // forces target installations to be queued
  private final static ISchedulingRule INSTALL_RULE = new ISchedulingRule() {

    public boolean contains( final ISchedulingRule rule ) {
      return rule == this;
    }

    public boolean isConflicting( final ISchedulingRule rule ) {
      return rule == this;
    }
  };

  public Object execute( ExecutionEvent event ) throws ExecutionException {
    Object trigger = event.getTrigger();
    String targetDefinitionURI = TargetProvider.TARGET_REPOSITORY;
    if( trigger instanceof Event && "e4".equals( ( ( Event )trigger ).text ) ) {
      targetDefinitionURI = TargetProvider.TARGET_E4_REPOSITORY;
    }
    IWorkbench workbench = PlatformUI.getWorkbench();
    Shell shell = workbench.getActiveWorkbenchWindow().getShell();
    InstallTargetDialog dialog = new InstallTargetDialog( shell );
    int result = dialog.open();
    if( result == Window.OK ) {
      execute( dialog.shouldSwitchTarget(), targetDefinitionURI );
    }
    return null;
  }

  public static void execute( final boolean switchTarget, final String targetDefinitionURI ) {
    Job installTargetJob = new Job( IntroMessages.TargetProvider_Installing ) {

      protected IStatus run( final IProgressMonitor monitor ) {
        IStatus result = Status.CANCEL_STATUS;
        try {
          TargetSwitcher.install( targetDefinitionURI, switchTarget, monitor );
          result = Status.OK_STATUS;
        } catch( final CoreException e ) {
          String msg = IntroMessages.InstallRAPTargetHandler_InstallFailed;
          result = ErrorUtil.createErrorStatus( msg, e );
        }
        return result;
      }
    };
    installTargetJob.setUser( true );
    installTargetJob.setRule( INSTALL_RULE );
    installTargetJob.schedule();
  }

}
