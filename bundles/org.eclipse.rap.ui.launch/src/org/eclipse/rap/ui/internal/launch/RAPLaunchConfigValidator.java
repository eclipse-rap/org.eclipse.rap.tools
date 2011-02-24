/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.rap.ui.internal.launch.tab.*;
import org.eclipse.rap.ui.internal.launch.util.LauncherSerializationUtil;


public final class RAPLaunchConfigValidator {

  private static final String DEFAULT_BRAINDING = "rap"; //$NON-NLS-1$
  public static final int ERR_SERVLET_NAME = 6001;
  public static final int ERR_PORT = 6004;
  public static final int ERR_URL = 6005;
  public static final int ERR_LOG_LEVEL = 6006;
  public static final int ERR_TIMEOUT = 6007;
  public static final int ERR_ENTRY_POINT = 6008;
  public static final int ERR_SERVLET_BUNDLE = 6009;
  public static final int WARN_OSGI_FRAMEWORK = 7002;
  private static final String RAP_LAUNCH_CONFIG_TYPE
    = "org.eclipse.rap.ui.launch.RAPLauncher"; //$NON-NLS-1$
  private static final String EMPTY = ""; //$NON-NLS-1$
  private static final String WORKSPACE_BUNDLES_KEY 
    = "workspace_bundles"; //$NON-NLS-1$
  private static final String TARGET_BUNDLES_KEY 
    = "target_bundles"; //$NON-NLS-1$

  private final RAPLaunchConfig config;

  public RAPLaunchConfigValidator( final RAPLaunchConfig config ) {
    this.config = config;
  }

  public IStatus[] validate() {
    List states = new ArrayList();
    try {
      addNonOKState( states, validateServletName() );
      addNonOKState( states, validatePort() );
      addNonOKState( states, validateUniquePort() );
      addNonOKState( states, validateURL() );
      addNonOKState( states, validateLogLevel() );
      addNonOKState( states, validateSessionTimeout() );
      addNonOKState( states, validateEntryPoint() );
    } catch( final CoreException e ) {
      String text
        = LaunchMessages.RAPLaunchConfigValidator_ErrorWhileValidating;
      Object[] args = new Object[] { e.getLocalizedMessage() };
      String msg = MessageFormat.format( text, args );
      states.add( createError( msg, 0, e ) );
    }
    IStatus[] result = new IStatus[ states.size() ];
    states.toArray( result );
    return result;
  }

  /////////////////////
  // Validation methods

  private IStatus validateServletName() throws CoreException {
    IStatus result = Status.OK_STATUS;
    String servletName = config.getServletName();
    if( servletName == null || EMPTY.equals( servletName ) ) {
      String msg = LaunchMessages.RAPLaunchConfigValidator_ServletNameEmpty;
      result = createError( msg, ERR_SERVLET_NAME, null );
    } else if( !servletName.equals( DEFAULT_BRAINDING ) ) { //$NON-NLS-1$
      ILaunchConfiguration launchConfig 
        = config.getUnderlyingLaunchConfig();
      String[] selectedBundleIds = getSelectedBundleIds( launchConfig );
      BrandingExtension[] selBrandingExtensions 
        = BrandingExtension.findInActivePlugins( selectedBundleIds, 
                                                 new NullProgressMonitor() );
      boolean isValid = isValidBranding( servletName, selBrandingExtensions );
      if( !isValid ) {
        String unformatedMsg 
          = LaunchMessages.RAPLaunchConfigValidator_BrandingMissing;
        String msg = MessageFormat.format( unformatedMsg, 
                                           new Object[] { servletName } );
        result = createError( msg, ERR_SERVLET_BUNDLE, null );
      }
    }
    return result;
  }

  private IStatus validatePort() throws CoreException {
    IStatus result = Status.OK_STATUS;
    if( config.getUseManualPort() ) {
      int port = config.getPort();
      if(    port < RAPLaunchConfig.MIN_PORT_NUMBER
          || port > RAPLaunchConfig.MAX_PORT_NUMBER )
      {
        String text = LaunchMessages.RAPLaunchConfigValidator_PortNumberInvalid;
        Object[] args = new Object[] {
          new Integer( RAPLaunchConfig.MIN_PORT_NUMBER ),
          new Integer( RAPLaunchConfig.MAX_PORT_NUMBER )
        };
        String msg = MessageFormat.format( text, args );
        result = createError( msg, ERR_PORT, null );
      }
    }
    return result;
  }

  private IStatus validateUniquePort() throws CoreException {
    IStatus result = Status.OK_STATUS;
    if( config.getUseManualPort() ) {
      RAPLaunchConfig duplicate = null;
      ILaunchConfiguration[] launchConfigs = getLaunchConfigs();
      for( int i = 0; duplicate == null && i < launchConfigs.length; i++ ) {
        RAPLaunchConfig otherConfig = new RAPLaunchConfig( launchConfigs[ i ] );
        if( hasSamePort( otherConfig ) ) {
          duplicate = otherConfig;
        }
      }
      if( duplicate != null ) {
        String text = LaunchMessages.RAPLaunchConfigValidator_PortInUse;
        Object[] args = new Object[] {
          new Integer( config.getPort() ),
          duplicate.getName()
        };
        String msg = MessageFormat.format( text, args );
        result = createWarning( msg, 0, null );
      }
    }
    return result;
  }

  private IStatus validateURL() throws CoreException {
    IStatus result = Status.OK_STATUS;
    try {
      URLBuilder.fromLaunchConfig( config, 80, false );
    } catch( MalformedURLException e ) {
      String text = LaunchMessages.RAPLaunchConfigValidator_MalformedUrl;
      result = createWarning( text, ERR_URL, e );
    }
    return result;
  }

  private IStatus validateLogLevel() throws CoreException {
    IStatus result = Status.OK_STATUS;
    boolean isValid = false;
    Level logLevel = config.getLogLevel();
    for( int i = 0; !isValid && i < RAPLaunchConfig.LOG_LEVELS.length; i++ ) {
      if( RAPLaunchConfig.LOG_LEVELS[ i ].equals( logLevel ) ) {
        isValid = true;
      }
    }
    if( !isValid ) {
      Object[] args = new Object[] { logLevel.getName() };
      String msg = LaunchMessages.RAPLaunchConfigValidator_LogLevelInvalid;
      String msgFmt = MessageFormat.format( msg, args );
      result = createError( msgFmt, ERR_LOG_LEVEL, null );
    }
    return result;
  }
  
  private IStatus validateSessionTimeout() throws CoreException {
    IStatus result = Status.OK_STATUS;
    boolean isValid
      = config.getSessionTimeout() >= RAPLaunchConfig.MIN_SESSION_TIMEOUT;
    if( !isValid ) {
      String msg = LaunchMessages.RAPLaunchConfigValidator_TimeoutInvalid;
      result = createError( msg, ERR_TIMEOUT, null );
    }
    return result;
  }

  private IStatus validateEntryPoint() throws CoreException {
    IStatus result = Status.OK_STATUS;
    String entryPoint = config.getEntryPoint();
    if( entryPoint != null && !EMPTY.equals( entryPoint ) ) {
      ILaunchConfiguration underlyingLaunchConfig 
        = config.getUnderlyingLaunchConfig();
      String[] selectedBundleIds 
        = getSelectedBundleIds( underlyingLaunchConfig );
      EntryPointExtension[] selEntryPointExtensions 
        = EntryPointExtension.findInActivePlugins( selectedBundleIds, 
                                                   new NullProgressMonitor() );
      ApplicationExtension[] selApplicationExtensions 
        = ApplicationExtension.findInActivePlugins( selectedBundleIds, 
                                                    new NullProgressMonitor() );
      boolean validEntryPoint 
        = isValidEntryPoint( entryPoint, selEntryPointExtensions );
      boolean validApplication 
        = isValidApplication( entryPoint, selApplicationExtensions );
      if( !validApplication && !validEntryPoint ) {
        String unformatedMsg 
          = LaunchMessages.RAPLaunchConfigValidator_EntryPointMissing;
        String msg = MessageFormat.format( unformatedMsg, 
                                           new Object[] { entryPoint } );
        result = createError( msg, ERR_ENTRY_POINT, null );
      }
    }
    return result;
  }
  
  /////////////////////////
  // Status creation helper

  private void addNonOKState( final List states, final IStatus state ) {
    if( state != null && !state.isOK() ) {
      states.add( state );
    }
  }

  private IStatus createWarning( final String msg,
                                 final int code,
                                 final Throwable thr )
  {
    String pluginId = Activator.getPluginId();
    return new Status( IStatus.WARNING, pluginId, code, msg, thr );
  }

  private IStatus createError( final String msg,
                               final int code,
                               final Throwable thr )
  {
    return new Status( IStatus.ERROR, Activator.getPluginId(), code, msg, thr );
  }

  /////////////////////////////////////////
  // Helping methods for validateUniquePort

  private static ILaunchConfiguration[] getLaunchConfigs() throws CoreException
  {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    ILaunchConfigurationType type
      = launchManager.getLaunchConfigurationType( RAP_LAUNCH_CONFIG_TYPE );
    return launchManager.getLaunchConfigurations( type );
  }

  private boolean hasSamePort( final RAPLaunchConfig otherConfig )
    throws CoreException
  {
    return    otherConfig.getUseManualPort()
           && !config.getName().equals( otherConfig.getName() )
           && config.getPort() == otherConfig.getPort();
  }
  
  /////////////////////////////////////////
  // Helping methods for validateEntryPoint
  
  private static String[] getSelectedBundleIds(
    final ILaunchConfiguration launchConfiguration) throws CoreException
  {
    String selectedWorkspaceBundles 
      = launchConfiguration.getAttribute( WORKSPACE_BUNDLES_KEY, EMPTY );
    String selectedTargetBundles 
      = launchConfiguration.getAttribute( TARGET_BUNDLES_KEY, EMPTY );
    List selectedBundleIds = new ArrayList();
    addBundleIds( selectedBundleIds, selectedWorkspaceBundles );
    addBundleIds( selectedBundleIds, selectedTargetBundles );
    List resultAsList = filterNotActiveSelectedBundleIds( selectedBundleIds );
    String[] result = new String[ resultAsList.size() ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = ( String )resultAsList.get( i );
    }
    return result;
  }

  private static void addBundleIds( final List selectedBundles,
                                    final String bundlesInCsvFormat )
  {
    if( bundlesInCsvFormat != null && !EMPTY.equals( bundlesInCsvFormat ) ) {
      String[] bundles = bundlesInCsvFormat.split( "," ); //$NON-NLS-1$
      for( int i = 0; i < bundles.length; i++ ) {
        int indexOf = bundles[ i ].indexOf( "@" ); //$NON-NLS-1$
        if( indexOf != -1 ) {
         String bundleId = bundles[ i ].substring( 0, indexOf );
         selectedBundles.add( bundleId );
        }
      }
    }
  }
  
  private static List filterNotActiveSelectedBundleIds( 
    final List selectedBundleIds ) 
  {
    IPluginModelBase[] activeModels = PluginRegistry.getActiveModels();
    List resultAsList = new ArrayList();
    for( int i = 0; i < activeModels.length; i++ ) {
      BundleDescription description = activeModels[ i ].getBundleDescription();
      String bundleId = description.getSymbolicName();
      if( selectedBundleIds.contains( bundleId ) ) {
        resultAsList.add( bundleId );
      }
    }
    return resultAsList;
  }
  
  private boolean isValidEntryPoint( final String entryPoint, 
                                     final EntryPointExtension[] extensions )
  {
    boolean result = false;
    for( int i = 0; i < extensions.length && result == false; i++ ) {
      EntryPointExtension extension = extensions[ i ];
      String availableEntryPoint
        = LauncherSerializationUtil.serializeEntryPointExntesion( extension );
      result = entryPoint.equals( availableEntryPoint );
    }
    return result;
  }

  private boolean isValidApplication( final String application, 
                                      final ApplicationExtension[] extensions )
  {
    boolean result = false;
    for( int i = 0; i < extensions.length && !result; i++ ) {
      ApplicationExtension extension = extensions[ i ];
      String availableApplication
        = LauncherSerializationUtil.serializeApplicationExtension( extension );
      result = application.equals( availableApplication );
    }
    return result;
  }
  
  private boolean isValidBranding( final String brandingt, 
                                   final BrandingExtension[] extensions )
  {
    boolean result = false;
    for( int i = 0; i < extensions.length && !result; i++ ) {
      BrandingExtension extension = extensions[ i ];
      String availableBranding
        = LauncherSerializationUtil.serializeBrandingExtension( extension );
      result = brandingt.equals( availableBranding );
    }
    return result;
  }

}
