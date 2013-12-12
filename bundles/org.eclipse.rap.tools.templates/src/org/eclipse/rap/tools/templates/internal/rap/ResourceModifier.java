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
package org.eclipse.rap.tools.templates.internal.rap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;


abstract class ResourceModifier implements IResourceDeltaVisitor {

  protected static final String NL = "\r\n"; //$NON-NLS-1$

  private final String resourceName;
  private boolean isDone = false;

  public ResourceModifier( String resourceName ) {
    this.resourceName = resourceName;
  }

  public boolean visit( IResourceDelta delta ) throws CoreException {
    String name = delta.getResource().getName();
    if( resourceName.equals( name ) && IResourceDelta.ADDED == delta.getKind() && !isDone ) {
      isDone = true;
      modifyResource( delta.getResource() );
    }
    return !isDone;
  }

  protected String getResourceName() {
    return resourceName;
  }

  protected abstract void modifyResource( IResource resource ) throws CoreException;

  protected void scheduleJob( final IFile file, final ByteArrayOutputStream baos ) {
    IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
    ISchedulingRule rule = ruleFactory.createRule( file );
    String jobName = NLS.bind( Messages.AbstractRAPWizard_Modifying, resourceName );
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
