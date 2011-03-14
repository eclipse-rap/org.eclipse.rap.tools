/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro.target;

import java.net.*;
import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.core.*;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.*;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.target.IUBundleContainer;
import org.eclipse.pde.internal.core.target.provisional.*;
import org.eclipse.rap.ui.internal.intro.ErrorUtil;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;

public final class TargetSwitcher {

  private static final String TARGET_NAME_PATTERN = "Rich Ajax Platform {0}"; //$NON-NLS-1$
  private static final String VM_ARGS = "-Dosgi.noShutdown=true -Declipse.ignoreApp=true"; //$NON-NLS-1$
  private static final String PROGRAM_ARGS = "-console -consolelog"; //$NON-NLS-1$
  // FIXME with the official WS_RAP 
  private static final String WS_RAP = "rap"; //$NON-NLS-1$
  
  private TargetSwitcher() {
    // prevent instantiation
  }

  /**
   * @param target to switch to.
   * @throws CoreException if fails to switch the target.
   */
  public static void switchTarget( final ITargetDefinition target )
    throws CoreException
  {
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
   * Installs a RAP target. If a target doesn't exist with the given target
   * version a new target definition will be created, target content will be 
   * downloaded and afterwards the target will be saved.
   * 
   * @param targetRepositoryURI a target repository URI
   * @param rootIUs an array with all root installable units
   * @param targetVersion target version
   * @param monitor for user feedback
   * @return the installed target definition
   * @throws CoreException if installation fails
   */
  public static ITargetDefinition install( final String targetRepositoryURI,
                                           final String[] rootIUs,
                                           final String targetVersion,
                                           final boolean switchTarget, 
                                           final IProgressMonitor monitor )
    throws CoreException
  {
    ITargetDefinition target = null;
    monitor.beginTask( IntroMessages.TargetProvider_Installing,
                       IProgressMonitor.UNKNOWN );
    try {
      SubMonitor subMonitor = SubMonitor.convert( monitor, 100 );
      IProgressMonitor targetMonitor = subMonitor.newChild( 5 );
      SubMonitor downloadMonitor = subMonitor.newChild( 95 );
      ITargetPlatformService service = getTargetPlatformService();
      target = createTargetDefinition( targetRepositoryURI,
                                       rootIUs,
                                       targetVersion,
                                       service, 
                                       targetMonitor);
      IStatus downloadStatus = downloadTarget( target, downloadMonitor );
      if( downloadStatus.isOK() ) {
        saveTarget( service, target );
        if( switchTarget == true ){
          switchTarget( target );
        }
      }
    } finally {
      monitor.done();
    }
    return target;
  }

  private static ITargetDefinition createTargetDefinition( 
    final String targetRepositoryURI,
    final String[] rootIUs,
    final String targetVersion,
    final ITargetPlatformService service, 
    final IProgressMonitor monitor) throws CoreException
  {
    String targetName = getTargetName( targetVersion );
    ITargetDefinition target = getExistingTarget( service, targetName );
    if( target == null ) {
      target = service.newTarget();
    }
    target.setName( targetName );
    target.setProgramArguments( PROGRAM_ARGS );
    target.setVMArguments( VM_ARGS );
//    target.setWS( WS_RAP );
    IUBundleContainer bundleContainer = getBundleContainer( service,
                                                            targetRepositoryURI,
                                                            rootIUs, 
                                                            monitor);
    target.setBundleContainers( new IBundleContainer[]{ bundleContainer } );
    return target;
  }

  private static ITargetDefinition getExistingTarget( 
    final ITargetPlatformService service,
    final String targetName ) throws CoreException
  {
    ITargetDefinition result = null;
    ITargetHandle[] targets = service.getTargets( new NullProgressMonitor() );
    if( targets != null ) {
      for( int i = 0; i < targets.length && result == null; i++ ) {
        ITargetDefinition targetDefinition = targets[ i ].getTargetDefinition();
        if( targetName.equalsIgnoreCase( targetDefinition.getName() ) ) {
          result = targetDefinition;
        }
      }
    }
    return result;
  }

  private static IStatus downloadTarget( final ITargetDefinition target,
                                         final IProgressMonitor monitor )
    throws CoreException
  {
    IStatus status = target.resolve( monitor );
    if( !status.isOK() ) {
      Throwable statusException = getDownloadException( status );
      if( statusException instanceof UnknownHostException ){
        //Case no internet connection
        String message = IntroMessages.TargetSwitcher_NoInternetConnectionAvailableErrorMsg;
        status = ErrorUtil.createErrorStatus( message, statusException );
      }else if( statusException instanceof SocketTimeoutException ){
        //Case no P2-repository problem
        String message = IntroMessages.TargetSwitcher_TargetRepositoryProblemErrorMsg;
        status = ErrorUtil.createErrorStatus( message, statusException );
      }
      throw new CoreException( status );
    }
    return status;
  }

  
  private static void saveTarget( final ITargetPlatformService service,
                                  final ITargetDefinition target )
    throws CoreException
  {
    service.saveTargetDefinition( target );
  }

  private static IUBundleContainer getBundleContainer( 
    final ITargetPlatformService service,
    final String p2RepositoryURI,
    final String[] rootIUs, 
    final IProgressMonitor monitor ) throws CoreException
  {
    IUBundleContainer container = null;
    try {
      container = createContainer( service, p2RepositoryURI, rootIUs, monitor );
    } catch( URISyntaxException e ) {
      String msg = IntroMessages.TargetSwitcher_InvalidTargetRepository;
      Object[] args = new Object[]{
        p2RepositoryURI
      };
      String fmtMsg = MessageFormat.format( msg, args );
      IStatus errorStatus = ErrorUtil.createErrorStatus( fmtMsg, e );
      throw new CoreException( errorStatus );
    } catch( final Exception e ) {
      String msg = IntroMessages.TargetSwitcher_TargetDefinitionErrorMsg;
      IStatus errorStatus = ErrorUtil.createErrorStatus( msg, e );
      throw new CoreException( errorStatus );
    }
    return container;
  }

  private static IUBundleContainer createContainer( 
    final ITargetPlatformService service,
    final String p2RepositoryURI,
    final String[] rootIUs, 
    final IProgressMonitor monitor ) throws Exception
  {
    URI[] p2Repos = new URI[]{
      new URI( p2RepositoryURI )
    };
    String[] versions = getLatestVersions( rootIUs, p2Repos, monitor );
    ContainerCreator creator = ContainerCreator.getInstance();
    IUBundleContainer container = creator.createContainer( rootIUs, versions, p2Repos, service );
    return container;
  }

  private static String[] getLatestVersions( final String[] rootIUs,
                                             final URI[] p2Repos, 
                                             final IProgressMonitor monitor )
    throws CoreException
  {
    IProvisioningAgent agent = getAgent();
    IMetadataRepositoryManager repoManager = ( IMetadataRepositoryManager )
      agent.getService( IMetadataRepositoryManager.SERVICE_NAME );
    loadRepositories( p2Repos, repoManager, monitor );
    String[] result = selectLatestVersions( rootIUs, repoManager );
    return result;
  }
  
  private static IProvisioningAgent getAgent() throws CoreException {
    IntroPlugin introPlugin = IntroPlugin.getDefault();
    IPath stateLocation = introPlugin.getStateLocation();
    URI stateLocationURI = stateLocation.toFile().toURI();
    IProvisioningAgentProvider agentProvider = ( IProvisioningAgentProvider )
    introPlugin.acquireService( IProvisioningAgentProvider.SERVICE_NAME );
    if(agentProvider == null){
      String message = "Agent provider service not available";
      IStatus status = ErrorUtil.createErrorStatus( message, null );
      throw new CoreException( status );
    }
    return agentProvider.createAgent( stateLocationURI );
  }

  private static void loadRepositories( 
    final URI[] p2Repos,
    final IMetadataRepositoryManager repoManager, 
    final IProgressMonitor monitor ) throws CoreException 
  {
    SubMonitor subMonitor = SubMonitor.convert( monitor );
    // Load available repositories
    for( int i = 0; i < p2Repos.length; i++ ) {
      try{
        SubMonitor repositoryMonitor = subMonitor.newChild( 1 );
        repoManager.loadRepository( p2Repos[i], repositoryMonitor );
      } catch (ProvisionException e) {
        String message = "Failed to load repository <{0}>";
        Object[] arguments = new Object[]{p2Repos[i]};
        String fmtMessage = MessageFormat.format( message, arguments );
        IStatus status = ErrorUtil.createErrorStatus( fmtMessage, e );
        throw new CoreException( status );
      }
    }
  }
  
  private static String[] selectLatestVersions( 
    final String[] rootIUs,
    final IMetadataRepositoryManager repoManager ) throws CoreException
  {
    String[] result = new String[rootIUs.length];
    for( int i = 0; i < rootIUs.length; i++ ) {
      String rootIuId = rootIUs[ i ];
      IQuery latestQuery = 
        QueryUtil.createLatestQuery( QueryUtil.createIUQuery( rootIuId ) );
      IQueryResult queryResult = 
        repoManager.query( latestQuery, new NullProgressMonitor() );
      if( queryResult.isEmpty() ) {
        String messag = "Feature <{0}> not found";
        Object[] arguments = new Object[]{
          rootIuId
        };
        String fmtMessage = MessageFormat.format( messag, arguments );
        IStatus status = ErrorUtil.createErrorStatus( fmtMessage, null );
        throw new CoreException( status );
      }
      Iterator iterator = queryResult.iterator();
      IInstallableUnit iu = (IInstallableUnit) iterator.next();
      result[i] = iu.getVersion().toString();
    }
    return result;
  }
  
  private static ITargetPlatformService getTargetPlatformService() 
   {
    String className = ITargetPlatformService.class.getName();
    PDECore pdeCore = PDECore.getDefault();
    return ( ITargetPlatformService )pdeCore.acquireService( className );
  }

  private static String getTargetName( final String targetVersion ) {
    String[] args = new String[]{
      targetVersion
    };
    return MessageFormat.format( TARGET_NAME_PATTERN, args );
  }
  
  private static Throwable getDownloadException( final IStatus status ) {
    Throwable result = status.getException();
    IStatus[] children = status.getChildren();
    if( result == null && children != null ){
      for(int i = 0; i < children.length && result == null ; i++){
        result = getDownloadException( children[i] );
      }
    }
    return result;
  }
}
