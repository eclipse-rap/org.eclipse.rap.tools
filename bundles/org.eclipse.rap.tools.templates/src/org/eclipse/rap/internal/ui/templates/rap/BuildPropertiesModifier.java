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

final class BuildPropertiesModifier extends ResourceModifier {

  public BuildPropertiesModifier( AbstractRAPWizard wizard ) {
    super( "build.properties" ); //$NON-NLS-1$
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
          while( line != null ) {
            String result = line + NL;
            if( !line.startsWith( "source.." ) && !line.startsWith( "output.." ) ) { //$NON-NLS-1$ //$NON-NLS-2$
              result = null;
            }
            if( result != null ) {
              writer.write( result );
            }
            line = reader.readLine();
          }
          writer.write( "bin.includes = META-INF/,\\" + NL ); //$NON-NLS-1$
          writer.write( "               OSGI-INF/,\\" + NL ); //$NON-NLS-1$
          writer.write( "               ." + NL ); //$NON-NLS-1$
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