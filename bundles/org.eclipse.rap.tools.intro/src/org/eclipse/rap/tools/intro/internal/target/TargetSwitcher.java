/*******************************************************************************
 * Copyright (c) 2009, 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.intro.internal.target;

import java.net.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.pde.core.target.*;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.rap.tools.intro.internal.ErrorUtil;
import org.eclipse.rap.tools.intro.internal.IntroPlugin;


@SuppressWarnings( "restriction" )
public final class TargetSwitcher {

  private TargetSwitcher() {
    // prevent instantiation
  }

  /**
   * @param target to switch to.
   * @throws CoreException if fails to switch the target.
   */
  public static void switchTarget( ITargetDefinition target ) throws CoreException {
    try {
      // FIXME [rh] inlined LoadTargetDefinitionJob#load() to be able to
      // join the job. Need to cancel jobs of the same family before
      // running
      // Job.getJobManager().cancel(JOB_FAMILY_ID);
      Job job = new LoadTargetDefinitionJob( target );
      job.setUser( true );
      job.schedule();
      job.join();
    } catch( final OperationCanceledException e ) {
      // do nothing
    } catch( final InterruptedException e ) {
      String msg = IntroMessages.InstallRAPTargetHandler_SwitchTargetInterrupted;
      ErrorUtil.log( msg, e );
    }
  }

  /*
   * Installs a RAP target. If a target doesn't exist with the given target version a new target
   * definition will be created, target content will be downloaded and afterwards the target will be
   * saved.
   * @param targetDefinitionURI a URI to a remote target definition file
   * @param monitor for user feedback
   * @return the installed target definition
   * @throws CoreException if installation fails
   */
  public static ITargetDefinition install( String targetDefinitionURI,
                                           boolean switchTarget,
                                           IProgressMonitor monitor )
    throws CoreException
  {
    ITargetDefinition target = null;
    monitor.beginTask( IntroMessages.TargetProvider_Installing, IProgressMonitor.UNKNOWN );
    try {
      SubMonitor subMonitor = SubMonitor.convert( monitor, 100 );
      IProgressMonitor targetMonitor = subMonitor.newChild( 5 );
      SubMonitor downloadMonitor = subMonitor.newChild( 95 );
      final ITargetPlatformService targetPlatformService = getTargetPlatformService();
      target = createTargetDefinition( targetDefinitionURI, targetPlatformService, targetMonitor );
      IStatus downloadStatus = downloadTarget( target, downloadMonitor );
      if( downloadStatus.isOK() ) {
        saveTarget( targetPlatformService, target );
        if( switchTarget ) {
          switchTarget( target );
        }
      }
    } finally {
      monitor.done();
    }
    return target;
  }

  private static ITargetDefinition createTargetDefinition( String targetDefinitionURI,
                                                           ITargetPlatformService service,
                                                           IProgressMonitor monitor )
    throws CoreException
  {
    ITargetDefinition result = null;
    try {
      String localTargetDefinitionURI
        = TargetProvider.createLocalTargetDefinition( targetDefinitionURI, monitor );
      ITargetHandle targetHandle = service.getTarget( new URI( localTargetDefinitionURI ) );
      result = targetHandle.getTargetDefinition();
    } catch( Exception exeption ) {
      String message;
      if( exeption instanceof UnknownHostException ) {
        // Case no internet connection
        message = IntroMessages.TargetSwitcher_NoInternetConnectionAvailableErrorMsg;
      } else if( exeption instanceof SocketTimeoutException ) {
        // Case connection problem
        message = IntroMessages.TargetSwitcher_TargetRepositoryProblemErrorMsg;
      } else {
        message = IntroMessages.TargetSwitcher_TargedDefinitionErrorMessage;
      }
      IStatus status = ErrorUtil.createErrorStatus( message, exeption );
      throw new CoreException( status );
    }
    return result;
  }

  private static IStatus downloadTarget( ITargetDefinition target, IProgressMonitor monitor )
    throws CoreException
  {
    IStatus status = target.resolve( monitor );
    if( !status.isOK() ) {
      Throwable statusException = getDownloadException( status );
      if( statusException instanceof UnknownHostException ) {
        // Case no internet connection
        String message = IntroMessages.TargetSwitcher_NoInternetConnectionAvailableErrorMsg;
        status = ErrorUtil.createErrorStatus( message, statusException );
      } else if( statusException instanceof SocketTimeoutException ) {
        // Case no P2-repository problem
        String message = IntroMessages.TargetSwitcher_TargetRepositoryProblemErrorMsg;
        status = ErrorUtil.createErrorStatus( message, statusException );
      } else if( monitor.isCanceled() && status.getSeverity() != IStatus.CANCEL ) {
        // Workaround for bug 407823 (407861)
        status = new Status( IStatus.CANCEL, IntroPlugin.getPluginId(), 0, "", null );
      }
      throw new CoreException( status );
    }
    return status;
  }

  private static void saveTarget( ITargetPlatformService service, ITargetDefinition target )
    throws CoreException
  {
    service.saveTargetDefinition( target );
  }

  private static ITargetPlatformService getTargetPlatformService() {
    return PDECore.getDefault().acquireService( ITargetPlatformService.class );
  }

  private static Throwable getDownloadException( IStatus status ) {
    Throwable result = status.getException();
    IStatus[] children = status.getChildren();
    if( result == null && children != null ) {
      for( int i = 0; i < children.length && result == null; i++ ) {
        result = getDownloadException( children[ i ] );
      }
    }
    return result;
  }

}
