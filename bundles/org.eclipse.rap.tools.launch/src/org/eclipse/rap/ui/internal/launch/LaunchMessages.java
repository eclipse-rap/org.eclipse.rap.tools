/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import org.eclipse.osgi.util.NLS;

public class LaunchMessages extends NLS {

  private static final String BUNDLE_NAME
    = "org.eclipse.rap.ui.internal.launch.messages"; //$NON-NLS-1$
  public static String PortBusyStatusHandler_PortInUseMessage;
  public static String PortBusyStatusHandler_PortInUseTitle;
  public static String RAPLaunchConfigValidator_BrandingMissing;
  public static String RAPLaunchConfigValidator_DataLocationErrorMsg;
  public static String RAPLaunchConfigValidator_EquinoxOnly;
  public static String RAPLaunchConfigValidator_ErrorWhileValidating;
  public static String RAPLaunchConfigValidator_MalformedUrl;
  public static String RAPLaunchConfigValidator_PortInUse;
  public static String RAPLaunchConfigValidator_PortNumberInvalid;
  public static String RAPLaunchConfigValidator_ServletPathEmpty;
  public static String RAPLaunchConfigValidator_ServletPathLeadingSlash;
  public static String RAPLaunchConfigValidator_ServletPathInvalid;
  public static String RAPLaunchConfigValidator_ContextPathLeadingSlash;
  public static String RAPLaunchConfigValidator_InvalidContextPath;
  public static String RAPLaunchConfigValidator_TimeoutInvalid;
  public static String RAPLaunchConfigValidator_WsEmpty;
  public static String RAPLaunchConfigValidator_WsWrong;
  public static String RAPLaunchDelegate_CheckPortTaskName;
  public static String RAPLaunchDelegate_DeterminePortTaskName;
  public static String RAPLaunchDelegate_OpenBrowserFailed;
  public static String RAPLaunchDelegate_OpenUrlFailed;
  public static String RAPLaunchDelegate_PortInUse;
  public static String RAPLaunchDelegate_StartClientTaskName;
  public static String RAPLaunchDelegate_TerminatePreviousTaskName;
  public static String RAPLaunchDelegate_WaitForHTTPTaskName;
  public static String RAPLaunchDelegate_Error_NotAPlugin;
  public static String SelectionDialogUtil_Unknown;
  public static String SelectionDialogUtil_UnknownProject;
  public static String MainTab_Browser;
  public static String MainTab_ConfigureBrowsers;
  public static String MainTab_ExternalBrowser;
  public static String MainTab_InternalBrowser;
  public static String MainTab_ApplicationUrl;
  public static String MainTab_ManualPortConfig;
  public static String MainTab_ManualContextPath;
  public static String MainTab_ManualTimeoutConfig;
  public static String MainTab_Name;
  public static String MainTab_OpenApplicationIn;
  public static String MainTab_ServerSettings;
  public static String MainTab_RAPSettings;
  public static String MainTab_DevelopmentMode;
  public static String MainTab_ServletPath;

  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, LaunchMessages.class );
  }

  private LaunchMessages() {
  }
}
