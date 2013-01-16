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

final class ActivatorModifier extends ResourceModifier {

  private static final String TAG_ACTIVATOR_NAME = "${activatorName}"; //$NON-NLS-1$
  private static final String CONTENT =
    // we keep the original package declaration
    NL
    + "import org.osgi.framework.BundleActivator;" + NL //$NON-NLS-1$
    + "import org.osgi.framework.BundleContext;" + NL //$NON-NLS-1$
    + NL
    + "public class ${activatorName} implements BundleActivator {" + NL //$NON-NLS-1$
    + NL
    + "\tpublic void start( BundleContext context ) throws Exception {" + NL //$NON-NLS-1$
    + "\t}" + NL //$NON-NLS-1$
    + NL
    + "\tpublic void stop( BundleContext context ) throws Exception {" + NL //$NON-NLS-1$
    + "\t}" + NL //$NON-NLS-1$
    + "}" + NL; //$NON-NLS-1$

  private final String activatorName;

  public ActivatorModifier( AbstractRAPWizard wizard ) {
    super( wizard.getActivatorName() + ".java" ); //$NON-NLS-1$
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
          while( line != null ) {
            String result = line + NL;
            if( result.startsWith( "package" ) ) { //$NON-NLS-1$
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
                                   "Could not process " + getResourceName(), //$NON-NLS-1$
                                   exception );
      throw new CoreException( status );
    }
  }

}