/*******************************************************************************
 * Copyright (c) 2009 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro.target;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.target.provisional.*;
import org.eclipse.rap.ui.internal.intro.ErrorUtil;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;


public final class TargetSwitcher35 {
  
  private static final String TARGET_NAME_PATTERN
    = "Rich Ajax Platform {0}"; //$NON-NLS-1$
  private static final String VM_ARGS
    = "-Dosgi.noShutdown=true -Declipse.ignoreApp=true"; //$NON-NLS-1$
  private static final String PROGRAM_ARGS
    = "-console -consolelog"; //$NON-NLS-1$
  private static final String ECLIPSE = "eclipse"; //$NON-NLS-1$

  public static void switchTarget( final String targetDestination,
                                   final IProgressMonitor monitor ) 
    throws CoreException 
  {
    try {
      ITargetDefinition target = createTargetDefinition( targetDestination );
      // FIXME [rh] inlined LoadTargetDefinitionJob#load() to be able to
      //       join the job. Need to cancel jobs of the same family before
      //       running
//          Job.getJobManager().cancel(JOB_FAMILY_ID);
      Job job = new LoadTargetDefinitionJob( target );
      job.setUser( true );
      job.schedule();
      job.join();
    } catch( OperationCanceledException e ) {
      // do nothing
    } catch( InterruptedException e ) {
      String msg
        = IntroMessages.InstallRAPTargetHandler_SwitchTargetInterrupted;
      ErrorUtil.log( msg, e );
    } 
  }

  private static ITargetDefinition createTargetDefinition( final String dest ) 
    throws CoreException
  {
    ITargetPlatformService service = getTargetPlatformService();
    ITargetDefinition target = service.newTarget();
    target.setName( getTargetName() );
    target.setProgramArguments( PROGRAM_ARGS );
    target.setVMArguments( VM_ARGS );
    String eclipse = appendPath( dest, ECLIPSE ); 
    IBundleContainer bundleContainer = service.newDirectoryContainer( eclipse );
    target.setBundleContainers( new IBundleContainer[] { bundleContainer } );
    service.saveTargetDefinition( target );
    return target;
  }

  private static ITargetPlatformService getTargetPlatformService() {
    String className = ITargetPlatformService.class.getName();
    PDECore pdeCore = PDECore.getDefault();
    return ( ITargetPlatformService )pdeCore.acquireService( className );
  }
  
  private static String getTargetName() {
    String[] args = new String[] { TargetProvider.getRAPRuntimeVersion() };
    return MessageFormat.format( TARGET_NAME_PATTERN, args );
  }

  private static String appendPath( final String path, final String append ) {
    File filePath = new File( path );
    File result = new File( filePath, append );
    return result.toString();
  }
  
  private TargetSwitcher35() {
    // prevent instantiation
  }
}
