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

import java.text.MessageFormat;
import java.util.logging.Level;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;


public final class RAPLaunchConfig {
  
  public static final class BrowserMode {
    
    public static final BrowserMode INTERNAL 
      = new BrowserMode( "INTERNAL" ); //$NON-NLS-1$
    public static final BrowserMode EXTERNAL 
      = new BrowserMode( "EXTERNAL" ); //$NON-NLS-1$

    public static BrowserMode[] values() {
      return new BrowserMode[] { INTERNAL, EXTERNAL };
    }

    public static BrowserMode parse( final String name ) {
      BrowserMode result = null;
      BrowserMode[] knownValues = values();
      for( int i = 0; result == null && i < knownValues.length; i++ ) {
        if( knownValues[ i ].getName().equalsIgnoreCase( name ) ) {
          result = knownValues[ i ];
        }
      }
      if( result == null ) {
        String text = "Unknown BrowserMode ''{0}''."; //$NON-NLS-1$
        String msg = MessageFormat.format( text, new Object[] { name } );
        throw new IllegalArgumentException( msg );  
      }
      return result;
    }
    
    private final String name;

    private BrowserMode( final String name ) {
      this.name = name;
    }
    
    public String getName() {
      return name;
    }
    
    public String toString() {
      return name;
    }
  }
  
  private static final String PREFIX = "org.eclipse.rap.launch."; //$NON-NLS-1$
  
  // Launch configuration attribute names
  public static final String ENRY_POINT = "entryPoint"; //$NON-NLS-1$
  public static final String LOG_LEVEL = PREFIX + "logLevel"; //$NON-NLS-1$
  public static final String PORT = PREFIX + "port"; //$NON-NLS-1$
  public static final String BROWSER_MODE = "browserMode"; //$NON-NLS-1$
  public static final String TERMINATE_PREVIOUS 
    = "terminatePrevious"; //$NON-NLS-1$
  
  // Default values for launch configuration attribute names
  public static final String DEFAULT_ENTRY_POINT = ""; //$NON-NLS-1$
  public static final BrowserMode DEFAULT_BROWSER_MODE = BrowserMode.INTERNAL;
  public static final String DEFAULT_LOG_LEVEL = Level.OFF.getName();
  public static final int DEFAULT_PORT = 10080;
  public static final boolean DEFAULT_TERMINATE_PREVIOUS = true;
  
  

  public static void setDefaults( final ILaunchConfigurationWorkingCopy config ) {
    config.setAttribute( ENRY_POINT, DEFAULT_ENTRY_POINT );
    config.setAttribute( BROWSER_MODE, DEFAULT_BROWSER_MODE.getName() );
    config.setAttribute( PORT, DEFAULT_PORT );
    config.setAttribute( LOG_LEVEL, DEFAULT_LOG_LEVEL );
    config.setAttribute( TERMINATE_PREVIOUS, DEFAULT_TERMINATE_PREVIOUS );
  }
  
  private final ILaunchConfiguration config;
  private final ILaunchConfigurationWorkingCopy workingCopy;

  public RAPLaunchConfig( final ILaunchConfiguration config ) {
    this.config = config;
    if( config instanceof ILaunchConfigurationWorkingCopy ) {
      workingCopy = ( ILaunchConfigurationWorkingCopy )config;
    } else {
      workingCopy = null;
    }
  }
  
  public String getName() {
    return config.getName();
  }
  
  //////////////////////////////////////////////////////////
  // Accessor and mutator methods for wrapped launch config
  
  public String getEntryPoint() throws CoreException {
    return config.getAttribute( ENRY_POINT, 
                                DEFAULT_ENTRY_POINT );
  }
  
  public void setEntryPoint( final String entryPoint ) {
    checkWorkingCopy();
    workingCopy.setAttribute( ENRY_POINT, entryPoint );
  }
  
  public BrowserMode getBrowserMode() throws CoreException {
    String value = config.getAttribute( BROWSER_MODE, 
                                        BrowserMode.INTERNAL.getName() );
    return BrowserMode.parse( value );
  }
  
  public void setBrowserMode( final BrowserMode browserMode ) {
    checkWorkingCopy();
    workingCopy.setAttribute( BROWSER_MODE, browserMode.getName() );
  }
  
  public int getPort() throws CoreException {
    return config.getAttribute( PORT, DEFAULT_PORT );
  }
  
  public void setPort( final int port ) {
    checkWorkingCopy();
    workingCopy.setAttribute( PORT, port );
  }
  
  public Level getLogLevel() throws CoreException {
    String value = config.getAttribute( LOG_LEVEL, 
                                        DEFAULT_LOG_LEVEL );
    return Level.parse( value );
  }
  
  public void setLogLevel( final Level logLevel ) {
    checkWorkingCopy();
    workingCopy.setAttribute( LOG_LEVEL, logLevel.getName() );
  }
  
  public boolean getTerminatePrevious() throws CoreException {
    return config.getAttribute( TERMINATE_PREVIOUS, 
                                DEFAULT_TERMINATE_PREVIOUS );
  }
  
  public void setTerminatePrevious( final boolean terminatePrevious ) {
    checkWorkingCopy();
    workingCopy.setAttribute( TERMINATE_PREVIOUS, terminatePrevious );
  }
  
  /////////////////
  // Helping method
  
  private void checkWorkingCopy() {
    if( workingCopy == null ) {
      String msg 
        = "Launch configuration cannot be modified, no working copy available";
      throw new IllegalStateException( msg );
    }
  }
}
