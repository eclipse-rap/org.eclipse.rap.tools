/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates.rap;

import java.io.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;
import org.eclipse.rap.internal.ui.templates.TemplateUtil;

/**
 * <p>
 * Abstract RAP template wizard. Subclasses must implement
 * {@link #init(org.eclipse.pde.ui.IFieldData)} and
 * {@link #createTemplateSections()}.
 * </p>
 * <p>
 * This class is responsible for modying the MANIFEST.MF after it has been
 * created, in order to make it work for RAP.
 * </p>
 */
abstract class AbstractRAPWizard extends NewPluginTemplateWizard {

  private static final String LAUNCH_TEMPLATE = "launch.template"; //$NON-NLS-1$
  private static final String CHARSET = "ISO-8859-1"; //$NON-NLS-1$

  private static final String TAG_ENTRY_POINT = "${entryPoint}"; //$NON-NLS-1$
  private static final String TAG_PLUGIN_ID = "${pluginId}"; //$NON-NLS-1$
  private static final String TAG_PROJECT_NAME = "${projectName}"; //$NON-NLS-1$

  public boolean performFinish( final IProject project,
                                final IPluginModelBase model,
                                final IProgressMonitor monitor )
  {
    boolean result = super.performFinish( project, model, monitor );
    if( result ) {
      copyLaunchConfig( project, model );
      IResourceChangeListener listener = new ManifestListener();
      ResourcesPlugin.getWorkspace().addResourceChangeListener( listener );
    }
    return result;
  }

  protected abstract String getEntryPointName();

  ////////////////////////////
  // Copy launch config helper

  private void copyLaunchConfig( final IProject project,
                                 final IPluginModelBase model )
  {
    String name = project.getName() + ".launch"; //$NON-NLS-1$
    IFile launchConfig = project.getFile( name );
    if( !launchConfig.exists() ) {
      try {
        InputStream stream = readLaunchConfig( project, model );
        launchConfig.create( stream, true, new NullProgressMonitor() );
      } catch( final CoreException ce ) {
        TemplateUtil.log( ce.getStatus() );
      }
    }
  }

  private InputStream readLaunchConfig( final IProject project,
                                        final IPluginModelBase model )
    throws CoreException
  {
    InputStream template
      = AbstractRAPWizard.class.getResourceAsStream( LAUNCH_TEMPLATE );
    StringBuffer buffer = new StringBuffer();
    try {
      InputStreamReader reader = new InputStreamReader( template, CHARSET );
      BufferedReader br = new BufferedReader( reader );
      try {
        int character = br.read();
        while( character != -1 ) {
          buffer.append( ( char )character );
          character = br.read();
        }
      } finally {
        br.close();
      }
    } catch( final Exception ex ) {
      String pluginId = TemplateUtil.PLUGIN_ID;
      String msg = "Could not read launch template"; //$NON-NLS-1$
      throw new CoreException( new Status( IStatus.ERROR, pluginId, msg, ex ) );
    }
    // Replace $-placeholder with actual values
    replacePlaceholder( buffer, TAG_PROJECT_NAME, project.getName() );
    String pluginId = model.getPluginBase().getId();
    replacePlaceholder( buffer, TAG_PLUGIN_ID, pluginId );
    replacePlaceholder( buffer, TAG_ENTRY_POINT, getEntryPointName() );
    return new ByteArrayInputStream( buffer.toString().getBytes() );
  }

  private static void replacePlaceholder( final StringBuffer buffer,
                                          final String placeholder,
                                          final String replacement )
  {
    int index = buffer.indexOf( placeholder );
    while( index != -1 ) {
      buffer.replace( index, index + placeholder.length(), replacement );
      index = buffer.indexOf( placeholder );
     }
  }

  ///////////////////
  // helping classes

  private class ManifestListener implements IResourceChangeListener {

    public void resourceChanged( final IResourceChangeEvent event ) {
      try {
        event.getDelta().accept( new ManifestModifier( this ) );
      } catch( CoreException cex ) {
        TemplateUtil.log( cex.getStatus() );
      }
    }
  }

  private static final class ManifestModifier implements IResourceDeltaVisitor {

    private static final String MANIFEST_FILE = "MANIFEST.MF"; //$NON-NLS-1$
    private static final String NL = "\r\n"; //$NON-NLS-1$

    private final ManifestListener listener;
    private boolean isDone = false;

    public ManifestModifier( final ManifestListener manifestListener ) {
      listener = manifestListener;
    }

    public boolean visit( final IResourceDelta delta ) throws CoreException {
      String name = delta.getResource().getName();
      if( MANIFEST_FILE.equals( name )
          && IResourceDelta.ADDED == delta.getKind()
          && !isDone )
      {
        isDone = true;
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        ws.removeResourceChangeListener( listener );
        modifyManifest( delta.getResource() );
      }
      return !isDone;
    }

    private void modifyManifest( final IResource resource )
      throws CoreException
    {
      final IFile file = ( IFile )resource;
      try {
        BufferedReader reader
          = new BufferedReader( new InputStreamReader( file.getContents() ) );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          BufferedWriter writer
            = new BufferedWriter( new OutputStreamWriter( baos ) );
          try {
            String line = reader.readLine();
            while( line != null ) {
              String result = line + NL;
              if( "Require-Bundle: org.eclipse.ui,".equals( line ) ) { //$NON-NLS-1$
                result = "Require-Bundle: org.eclipse.rap.ui" + NL; //$NON-NLS-1$
              } else if(    " org.eclipse.core.runtime,".equals( line ) //$NON-NLS-1$
                         || " org.eclipse.rap.ui".equals( line ) ) { //$NON-NLS-1$
                result = null;
              }
              if( result != null ) {
                writer.write( result );
              }
              line = reader.readLine();
            }
            writer.write( "Import-Package: javax.servlet;version=\"2.4.0\"," + NL ); //$NON-NLS-1$
            writer.write( " javax.servlet.http;version=\"2.4.0\"" + NL ); //$NON-NLS-1$
          } finally {
            writer.close();
          }
        } finally {
          reader.close();
        }
        scheduleJob( file, baos );
      } catch( IOException ioe ) {
        IStatus status = new Status( IStatus.ERROR,
                                     TemplateUtil.PLUGIN_ID,
                                     IStatus.OK,
                                     "Could not process " + MANIFEST_FILE, //$NON-NLS-1$
                                     ioe );
        throw new CoreException( status );
      }
    }

    private void scheduleJob( final IFile file,
                              final ByteArrayOutputStream baos )
    {
      IResourceRuleFactory ruleFactory
        = ResourcesPlugin.getWorkspace().getRuleFactory();
      ISchedulingRule rule = ruleFactory.createRule( file );
      String jobName = NLS.bind( TemplateMessages.AbstractRAPWizard_Modifying,
                                 MANIFEST_FILE );
      Job job = new WorkspaceJob( jobName ) {
        public IStatus runInWorkspace( final IProgressMonitor monitor )
          throws CoreException
        {
          ByteArrayInputStream bais
            = new ByteArrayInputStream( baos.toByteArray() );
          file.setContents( bais, true, false, new NullProgressMonitor() );
          return Status.OK_STATUS;
        }
      };
      job.setRule( rule );
      job.schedule( 1000 );
    }
  }
}
