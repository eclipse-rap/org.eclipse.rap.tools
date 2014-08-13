/*******************************************************************************
 * Copyright (c) 2011, 2014 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.delegate;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestLaunch;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WebXmlProvider_Test {

  private NullProgressMonitor monitor;
  private ILaunchConfigurationWorkingCopy genericLaunchConfig;
  private RWTLaunchConfig launchConfig;
  private WebXmlProvider provider;

  @Before
  public void setUp() throws Exception {
    monitor = new NullProgressMonitor();
    genericLaunchConfig = Fixture.createRWTLaunchConfig();
    launchConfig = new RWTLaunchConfig( genericLaunchConfig );
    provider = new WebXmlProvider( new RWTLaunch( new TestLaunch( genericLaunchConfig ) ) );
  }

  @After
  public void tearDown() throws Exception {
    TestProject.deleteAll();
    launchConfig.getUnderlyingLaunchConfig().delete();
  }

  @Test
  public void testProvide_withProvidedWebXml() throws Exception {
    String webXmlContent = "<web.xml />";
    TestProject testProject = new TestProject();
    IContainer project = testProject.getProject();
    IFile webXml = createFile( project, "web.xml", webXmlContent );
    launchConfig.setLaunchTarget( LaunchTarget.WEB_XML );
    launchConfig.setWebXmlLocation( webXml.getFullPath().toPortableString() );

    File providedWebXml = provider.provide( monitor );

    byte[] webXmlContentBytes = webXmlContent.getBytes( "utf-8" );
    byte[] providedWebXmlBytes = Fixture.readBytes( providedWebXml );
    assertArrayEquals( webXmlContentBytes, providedWebXmlBytes );
  }

  @Test
  public void testProvide_withGeneratedWebXml() throws Exception {
    launchConfig.setLaunchTarget( LaunchTarget.ENTRY_POINT );
    launchConfig.setEntryPoint( "Foo" );

    File providedWebXml = provider.provide( monitor );

    byte[] providedWebXmlBytes = Fixture.readBytes( providedWebXml );
    assertTrue( providedWebXmlBytes.length > 0 );
  }

  @Test
  public void testProvide_withGeneratedWebXml_containsEntryPoint() throws Exception {
    launchConfig.setLaunchTarget( LaunchTarget.ENTRY_POINT );
    launchConfig.setEntryPoint( "Foo" );

    File providedWebXml = provider.provide( monitor );

    String providedWebXmlContent = new String( Fixture.readBytes( providedWebXml ) );
    assertTrue( providedWebXmlContent.indexOf( "org.eclipse.rwt.entryPoints" ) != -1 );
    assertTrue( providedWebXmlContent.indexOf( "org.eclipse.rap.applicationConfiguration" ) == -1 );
    assertTrue( providedWebXmlContent.indexOf( "Foo" ) != -1 );
  }

  @Test
  public void testProvide_withGeneratedWebXml_containsAppConfig() throws Exception {
    launchConfig.setLaunchTarget( LaunchTarget.APP_CONFIG );
    launchConfig.setAppConfig( "Foo" );

    File providedWebXml = provider.provide( monitor );

    String providedWebXmlContent = new String( Fixture.readBytes( providedWebXml ) );
    assertTrue( providedWebXmlContent.indexOf( "org.eclipse.rwt.entryPoints" ) == -1 );
    assertTrue( providedWebXmlContent.indexOf( "org.eclipse.rap.applicationConfiguration" ) != -1 );
    assertTrue( providedWebXmlContent.indexOf( "Foo" ) != -1 );
  }

  private static IFile createFile( IContainer container, String fileName, String content )
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
