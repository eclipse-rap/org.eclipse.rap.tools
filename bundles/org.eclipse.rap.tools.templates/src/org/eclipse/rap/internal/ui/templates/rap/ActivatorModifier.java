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

final class ActivatorModifier implements IResourceDeltaVisitor {

  private static final String NL = "\r\n"; //$NON-NLS-1$
  private static final String TAG_ACTIVATOR_NAME = "${activatorName}"; //$NON-NLS-1$
  private static final String CONTENT =
    // we keep the original package declaration
    NL
    + "import org.osgi.framework.BundleActivator;" + NL
    + "import org.osgi.framework.BundleContext;" + NL
    + NL
    + "public class ${activatorName} implements BundleActivator {" + NL
    + NL
    + "\tpublic void start( BundleContext context ) throws Exception {" + NL
    + "\t}" + NL
    + NL
    + "\tpublic void stop( BundleContext context ) throws Exception {" + NL
    + "\t}" + NL
    + "}" + NL;

  private final String activatorName;
  private final String activatorFile;
  private boolean isDone = false;

  public ActivatorModifier( AbstractRAPWizard wizard ) {
    activatorName = wizard.getActivatorName();
    activatorFile = activatorName + ".java";
  }

  public boolean visit( IResourceDelta delta ) throws CoreException {
    String name = delta.getResource().getName();
    if( !isDone && activatorFile.equals( name ) && IResourceDelta.ADDED == delta.getKind() ) {
      isDone = true;
      modifyActivator( delta.getResource() );
    }
    return !isDone;
  }

  private void modifyActivator( IResource resource ) throws CoreException {
    final IFile file = ( IFile )resource;
    try {
      BufferedReader reader = new BufferedReader( new InputStreamReader( file.getContents() ) );
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( baos ) );
        try {
          String line = reader.readLine();
          while( line != null ) {
            String result = line + NL;
            if( result.startsWith( "package" ) ) {
              writer.write( result );
            }
            line = reader.readLine();
          }
          writer.write( CONTENT.replace( TAG_ACTIVATOR_NAME, activatorName ) );
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
                                   "Could not process " + activatorFile, //$NON-NLS-1$
                                   exception );
      throw new CoreException( status );
    }
  }

  private void scheduleJob( final IFile file, final ByteArrayOutputStream baos ) {
    IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
    ISchedulingRule rule = ruleFactory.createRule( file );
    String jobName = NLS.bind( Messages.AbstractRAPWizard_Modifying, activatorFile );
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