/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates.rap;

import java.io.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rap.internal.ui.templates.TemplateUtil;

final class ManifestModifier implements IResourceDeltaVisitor {

  private static final String NL = "\r\n"; //$NON-NLS-1$
  private static final String MANIFEST_FILE = "MANIFEST.MF"; //$NON-NLS-1$

  private String requireBundles;
  private boolean isDone = false;

  public ManifestModifier( AbstractRAPWizard wizard ) {
    requireBundles = wizard.getRequireBundles();
  }

  public boolean visit( IResourceDelta delta ) throws CoreException {
    String name = delta.getResource().getName();
    if( MANIFEST_FILE.equals( name ) && IResourceDelta.ADDED == delta.getKind() && !isDone ) {
      isDone = true;
      modifyManifest( delta.getResource() );
    }
    return !isDone;
  }

  private void modifyManifest( IResource resource ) throws CoreException {
    final IFile file = ( IFile )resource;
    try {
      BufferedReader reader = new BufferedReader( new InputStreamReader( file.getContents() ) );
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( baos ) );
        try {
          String line = reader.readLine();
          boolean inRequireBundle = false;
          while( line != null ) {
            String result = line + NL;
            if( line.startsWith( "Require-Bundle:" ) ) {
              inRequireBundle = true;
              result = null;
            } else if( inRequireBundle && line.startsWith( " " ) ) {
              result = null;
            } else {
              inRequireBundle = false;
            }
            if( result != null ) {
              writer.write( result );
            }
            line = reader.readLine();
          }
          writer.write( "Require-Bundle: " + requireBundles + NL );
          writer.write( "Import-Package: javax.servlet;version=\"2.4.0\"," + NL ); //$NON-NLS-1$
          writer.write( " javax.servlet.http;version=\"2.4.0\"," + NL ); //$NON-NLS-1$
          writer.write( " org.osgi.framework" + NL ); //$NON-NLS-1$
          String fileName = AbstractRAPWizard.SERVICE_COMPONENT_FILE;
          IFile serviceComponentXml = file.getProject().getFile( fileName );
          if( serviceComponentXml.exists() ) {
            writer.write( "Service-Component: " + fileName + NL );
          }
        } finally {
          writer.close();
        }
      } finally {
        reader.close();
      }
      scheduleJob( file, baos );
    } catch( IOException exception ) {
      IStatus status = new Status( IStatus.ERROR,
                                   TemplateUtil.PLUGIN_ID,
                                   IStatus.OK,
                                   "Could not process " + MANIFEST_FILE, //$NON-NLS-1$
                                   exception );
      throw new CoreException( status );
    }
  }

  private void scheduleJob( final IFile file, final ByteArrayOutputStream baos ) {
    IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
    ISchedulingRule rule = ruleFactory.createRule( file );
    String jobName = NLS.bind( Messages.AbstractRAPWizard_Modifying, MANIFEST_FILE );
    Job job = new WorkspaceJob( jobName ) {
      public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException {
        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        file.setContents( bais, true, false, new NullProgressMonitor() );
        return Status.OK_STATUS;
      }
    };
    job.setRule( rule );
    job.schedule( 1000 );
  }

}