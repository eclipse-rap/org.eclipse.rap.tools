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

import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.AssertUtil;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestLaunch;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProject;


public class WebXmlProvider_Test extends TestCase {

  private NullProgressMonitor monitor;
  private ILaunchConfigurationWorkingCopy genericLaunchConfig;
  private RWTLaunchConfig launchConfig;
  private WebXmlProvider provider;

  public void testProvideWithGeneratedWebXml() throws Exception {
    launchConfig.setUseWebXml( false );
    launchConfig.setEntryPoint( "EntryPoint" );
    
    File providedWebXml = provider.provide( monitor );
    
    byte[] providedWebXmlBytes = Fixture.readBytes( providedWebXml );
    assertTrue( providedWebXmlBytes.length > 0 );
  }
  
  public void testProvideWithProvidedWebXml() throws Exception {
    String webXmlContent = "<web.xml />";
    TestProject testProject = new TestProject();
    IContainer project = testProject.getProject();
    IFile webXml = createFile( project, "web.xml", webXmlContent );
    launchConfig.setUseWebXml( true );
    launchConfig.setWebXmlLocation( webXml.getFullPath().toPortableString() );

    File providedWebXml = provider.provide( monitor );
    
    byte[] webXmlContentBytes = webXmlContent.getBytes( "utf-8" );
    byte[] providedWebXmlBytes = Fixture.readBytes( providedWebXml );
    AssertUtil.assertEquals( webXmlContentBytes, providedWebXmlBytes );
  }

  protected void setUp() throws Exception {
    monitor = new NullProgressMonitor();
    genericLaunchConfig = Fixture.createRWTLaunchConfig();
    launchConfig = new RWTLaunchConfig( genericLaunchConfig );
    provider = new WebXmlProvider( new RWTLaunch( new TestLaunch( genericLaunchConfig ) ) );
  }
  
  protected void tearDown() throws Exception {
    TestProject.deleteAll();
    launchConfig.getUnderlyingLaunchConfig().delete();
  }

  private static IFile createFile( IContainer container, 
                                   String fileName, 
                                   String content ) 
    throws CoreException 
  {
    IFile result = container.getFile( new Path( fileName ) );
    InputStream stream = Fixture.toUtf8Stream( content );
    if( result.exists() ) {
      result.setContents( stream, false, false, new NullProgressMonitor() );
    } else {
      result.create( stream, true, new NullProgressMonitor() );
    }
    return result;
  }
}
