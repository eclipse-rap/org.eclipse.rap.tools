/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.delegate;

import java.io.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.util.IOUtil;
import org.eclipse.rap.ui.internal.launch.rwt.util.TemplateParser;


class WebXmlProvider {

  private final RWTLaunchConfig config;
  private final File destination;
  
  WebXmlProvider( RWTLaunch launch ) {
    this.config = launch.getLaunchConfig();
    this.destination = launch.getWebXmlPath();
  }

  File provide( IProgressMonitor monitor ) {
    IProgressMonitor subMonitor = new SubProgressMonitor( monitor, 1 );
    subMonitor.beginTask( "Provisioning web.xml...", 1 );
    try {
      internalProvide();
      subMonitor.worked( 1 );
    } finally {
      subMonitor.done();
    }
    return destination;
  }

  private void internalProvide() {
    if( config.getUseWebXml() ) {
      provideCustomWebXml();
    } else {
      provideGeneratedWebXml();
    }
  }
  
  private void provideCustomWebXml() {
    String source = config.getWebXmlLocation();
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IFile absoluteSourcce = root.getFile( Path.fromPortableString( source ) );
    IOUtil.copy( new File( absoluteSourcce.getLocationURI() ), destination );
  }

  private void provideGeneratedWebXml() {
    String webXmlContent = generateWebXmlContent();
    byte[] bytes = getBytes( webXmlContent );
    InputStream inputStream = new ByteArrayInputStream( bytes );
    IOUtil.copy( inputStream, destination );
  }

  private String generateWebXmlContent() {
    TemplateParser templateParser = new TemplateParser( getWebXmlTemplate() );
    templateParser.registerVariable( "webAppName", config.getName() ); //$NON-NLS-1$
    templateParser.registerVariable( "entryPoint", config.getEntryPoint() ); //$NON-NLS-1$
    return templateParser.parse();
  }
  
  private String getWebXmlTemplate() {
    InputStream inputStream = getClass().getResourceAsStream( "template-web.xml" ); //$NON-NLS-1$
    try {
      return IOUtil.readContent( inputStream );
    } finally {
      IOUtil.closeInputStream( inputStream );
    }
  }

  private static byte[] getBytes( String string ) {
    try {
      return string.getBytes( "utf-8" ); //$NON-NLS-1$
    } catch( UnsupportedEncodingException uee ) {
      throw new RuntimeException( uee );
    }
  }
}
