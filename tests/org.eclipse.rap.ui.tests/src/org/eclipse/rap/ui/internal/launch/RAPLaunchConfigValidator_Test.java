/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import java.util.logging.Level;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.eclipse.rap.ui.tests.Fixture;


public class RAPLaunchConfigValidator_Test extends TestCase {
  
  private static final class MyLevel extends Level {
    private static final long serialVersionUID = 1L;
    protected MyLevel( final String name, final int value ) {
      super( name, value );
    }
  }
  
  private ILaunchConfigurationWorkingCopy config;
  private RAPLaunchConfig rapConfig;

  protected void setUp() throws Exception {
    config = Fixture.createRAPLaunchConfig();
    rapConfig = new RAPLaunchConfig( config );
  }
  
  protected void tearDown() throws Exception {
    config.delete();
  }
  
  public void testValidateServletName() {
    // servlet name has a default value, clear this
    rapConfig.setServletName( "" );
    RAPLaunchConfigValidator val = new RAPLaunchConfigValidator( rapConfig );
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_SERVLET_NAME;
    assertTrue( findStatusCode( states, code ) );
  }
  
  public void testEntryPointEmpty() {
    RAPLaunchConfigValidator val = rapConfig.getValidator();
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_ENTRY_POINT_EMPTY;
    assertTrue( findStatusCode( states, code ) );
  }
  
  public void testPort() {
    rapConfig.setUseManualPort( true );
    rapConfig.setPort( -1 );
    RAPLaunchConfigValidator val = rapConfig.getValidator();
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_PORT;
    assertTrue( findStatusCode( states, code ) );
    // Fix valiation error and retry
    rapConfig.setPort( 8080 );
    states = val.validate();
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testUrl() {
    rapConfig.setEntryPoint( "entryPoint" );
    RAPLaunchConfigValidator val = rapConfig.getValidator();
    IStatus[] states = val.validate();
    int code = RAPLaunchConfigValidator.ERR_PORT;
    assertFalse( findStatusCode( states, code ) );
  }
  
  public void testLogLevel() {
    rapConfig.setLogLevel( new MyLevel( "custom", 1234 ) );
    IStatus[] states = rapConfig.getValidator().validate();
    int code = RAPLaunchConfigValidator.ERR_LOG_LEVEL;
    assertTrue( findStatusCode( states, code ) );
  }
  
  public void testValidateOSGiFramework() {
    IStatus[] states;
    int code = RAPLaunchConfigValidator.WARN_OSGI_FRAMEWORK;
    config.setAttribute( IPDELauncherConstants.OSGI_FRAMEWORK_ID, "new.id" );
    states = rapConfig.getValidator().validate();
    assertTrue( findStatusCode( states, code ) );
  }
  
  //////////////////
  // helping methods

  private static boolean findStatusCode( final IStatus[] states, 
                                         final int code ) 
  {
    boolean result = false;
    for( int i = 0; !result && i < states.length; i++ ) {
      if( states[ i ].getCode() == code ) {
        result = true;
      }
    }
    return result;
  }
}
