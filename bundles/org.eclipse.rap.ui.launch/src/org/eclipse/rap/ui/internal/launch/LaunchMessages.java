/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import org.eclipse.osgi.util.NLS;

public class LaunchMessages extends NLS {

  private static final String BUNDLE_NAME
    = "org.eclipse.rap.ui.internal.launch.messages"; //$NON-NLS-1$
  
  public static String PortBusyStatusHandler_PortInUseMessage;
  public static String PortBusyStatusHandler_PortInUseTitle;
  
  public static String RAPLaunchConfigValidator_EntryPointEmpty;
  public static String RAPLaunchConfigValidator_EquinoxOnly;
  public static String RAPLaunchConfigValidator_ErrorWhileValidating;
  public static String RAPLaunchConfigValidator_LogLevelInvalid;
  public static String RAPLaunchConfigValidator_MalformedUrl;
  public static String RAPLaunchConfigValidator_PortInUse;
  public static String RAPLaunchConfigValidator_PortNumberInvalid;
  public static String RAPLaunchConfigValidator_ServletNameEmpty;

  public static String RAPLaunchDelegate_CheckPortTaskName;
  public static String RAPLaunchDelegate_DeterminePortTaskName;
  public static String RAPLaunchDelegate_OpenBrowserFailed;
  public static String RAPLaunchDelegate_OpenUrlFailed;
  public static String RAPLaunchDelegate_PortInUse;
  public static String RAPLaunchDelegate_StartClientTaskName;
  public static String RAPLaunchDelegate_TerminatePreviousTaskName;
  public static String RAPLaunchDelegate_WaitForHTTPTaskName;

  public static String SelectionDialogUtil_Unknown;
  public static String SelectionDialogUtil_UnknownProject;

  public static String EntryPointSelectionDialog_Message;
  public static String EntryPointSelectionDialog_Searching;
  public static String EntryPointSelectionDialog_Title;

  public static String MainTab_BrowseEntryPoint;

  public static String MainTab_Browser;

  public static String MainTab_BrowseServletName;

  public static String MainTab_ClientLibraryVariant;

  public static String MainTab_ClientLogLevel;

  public static String MainTab_ConfigureBrowsers;

  public static String MainTab_EntryPoint;

  public static String MainTab_ExternalBrowser;

  public static String MainTab_InternalBrowser;

  public static String MainTab_LibraryVariantDebug;

  public static String MainTab_LibraryVariantStandard;

  public static String MainTab_LogLevelAll;

  public static String MainTab_LogLevelConfig;

  public static String MainTab_LogLevelFine;

  public static String MainTab_LogLevelFiner;

  public static String MainTab_LogLevelFinest;

  public static String MainTab_LogLevelInfo;

  public static String MainTab_LogLevelOff;

  public static String MainTab_LogLevelSevere;

  public static String MainTab_LogLevelWarning;

  public static String MainTab_ManualPortConfig;

  public static String MainTab_Name;

  public static String MainTab_ObtainDefaultEntryPointForBrandingFailed;

  public static String MainTab_OpenApplicationIn;

  public static String MainTab_RuntimeSettings;

  public static String MainTab_ServletAndEntryPoint;

  public static String MainTab_ServletName;

  public static String MainTab_TerminatePrevious;

  public static String ServletNameSelectionDialog_Message;
  public static String ServletNameSelectionDialog_Searching;
  public static String ServletNameSelectionDialog_Title;

  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, LaunchMessages.class );
  }

  private LaunchMessages() {
  }
}
