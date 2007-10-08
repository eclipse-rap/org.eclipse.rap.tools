/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates.rap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;
import org.eclipse.rap.internal.ui.templates.Activator;
import org.eclipse.rap.internal.ui.templates.TemplateUtil;

/**
 * <p>
 * Abstract RAP template wizard. Subclasses must implemenet
 * {@link #init(org.eclipse.pde.ui.IFieldData)} and
 * {@link #createTemplateSections()}.
 * </p>
 * <p>
 * This class is responsible for modying the manifest.mf after it has been
 * created, in order to make it work for RAP.
 * </p>
 */
abstract class AbstractRAPWizard extends NewPluginTemplateWizard {

  public boolean performFinish( final IProject project,
                                final IPluginModelBase model,
                                final IProgressMonitor monitor )
  {
    boolean result = super.performFinish( project, model, monitor );
    IResourceChangeListener listener = new ManifestListener();
    copyLaunchConfig(project);
    ResourcesPlugin.getWorkspace().addResourceChangeListener( listener );
    return result;
  }

  // helping classes
  /////////////////////
  
  private void copyLaunchConfig( final IProject project ) {
    IFile launch = project.getFile( project.getName() + ".launch" ); //$NON-NLS-1$
    if( !launch.exists() ) {
      try {
        InputStream stream = readLaunchConfig( project );
        launch.create( stream, true, new NullProgressMonitor() );
      } catch( final CoreException ce ) {
        Activator.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private ByteArrayInputStream readLaunchConfig( final IProject project )
    throws CoreException
  {
    String resource = "launch.template"; //$NON-NLS-1$
    InputStream tmpl = getClass().getResourceAsStream( resource );
    StringBuffer buffer = new StringBuffer();
    try {
      InputStreamReader reader = new InputStreamReader( tmpl, "ISO-8859-1" ); //$NON-NLS-1$
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
      String pluginId = Activator.getPluginId();
      String msg = "Could not read launch template"; //$NON-NLS-1$
      throw new CoreException( new Status( IStatus.ERROR, pluginId, msg, ex ) );
    }
    // TODO [rh] find the method that PDE uses to translate project names into
    // bundle names and replace the lines below (-> PluginId!)
    String escapedProjectName = project.getName().replace( ' ', '_' );
    escapedProjectName = escapedProjectName.replace( '-', '_' );
    replacePlaceholder( buffer, "${projectName}", escapedProjectName ); //$NON-NLS-1$
    replacePlaceholder( buffer, "${entryPoint}", getEntryPointName() ); //$NON-NLS-1$
    byte[] bytes = buffer.toString().getBytes();
    return new ByteArrayInputStream( bytes );
  }

  protected abstract String getEntryPointName();

  private static void replacePlaceholder( final StringBuffer buffer, 
                                          final String placeHolder, 
                                          final String replacement ) 
  {
    int index;
    index = buffer.indexOf( placeHolder );
    while( index != -1 ) {
      buffer.replace( index, index + placeHolder.length(), replacement );
      index = buffer.indexOf( placeHolder );
     }
  }

  private class ManifestListener implements IResourceChangeListener {

    public void resourceChanged( IResourceChangeEvent event ) {
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

    public boolean visit( IResourceDelta delta ) throws CoreException {
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

    private void scheduleJob( final IFile file, final ByteArrayOutputStream baos )
    {
      IResourceRuleFactory ruleFactory 
        = ResourcesPlugin.getWorkspace().getRuleFactory();
      ISchedulingRule rule = ruleFactory.createRule( file );
      Job job = new WorkspaceJob( "Modifing " + MANIFEST_FILE ) { //$NON-NLS-1$

        public IStatus runInWorkspace( IProgressMonitor monitor )
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
