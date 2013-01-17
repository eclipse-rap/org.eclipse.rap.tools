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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.rap.internal.ui.templates.TemplateUtil;

final class ManifestModifier extends ResourceModifier {

  private String requireBundles;
  private boolean shouldModifyActivator;
  private String activatorName;

  public ManifestModifier( AbstractRAPWizard wizard ) {
    super( "MANIFEST.MF" ); //$NON-NLS-1$
    requireBundles = wizard.getRequireBundles();
    shouldModifyActivator = wizard.shouldModifyActivator();
    activatorName = wizard.getActivatorName();
  }

  protected void modifyResource( IResource resource ) throws CoreException {
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
            if( line.startsWith( "Require-Bundle:" ) ) { //$NON-NLS-1$
              inRequireBundle = true;
              result = null;
            } else if( inRequireBundle && line.startsWith( " " ) ) { //$NON-NLS-1$
              result = null;
            } else {
              inRequireBundle = false;
            }
            if( result != null ) {
              writer.write( result );
            }
            line = reader.readLine();
          }
          writer.write( "Require-Bundle: " + requireBundles + NL ); //$NON-NLS-1$
          if( shouldModifyActivator && activatorName != null ) {
            writer.write( "Import-Package: org.osgi.framework" + NL ); //$NON-NLS-1$
          }
          String fileName = AbstractRAPWizard.SERVICE_COMPONENT_FILE;
          IFile serviceComponentXml = file.getProject().getFile( fileName );
          if( serviceComponentXml.exists() ) {
            writer.write( "Service-Component: " + fileName + NL ); //$NON-NLS-1$
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
                                   "Could not process " + getResourceName(), //$NON-NLS-1$
                                   exception );
      throw new CoreException( status );
    }
  }

}