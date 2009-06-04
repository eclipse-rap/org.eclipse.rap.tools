/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro.target;

import org.eclipse.osgi.util.NLS;

public class IntroMessages extends NLS {

  private static final String BUNDLE_NAME 
    = "org.eclipse.rap.ui.internal.intro.target.messages"; //$NON-NLS-1$
  
  public static String InstallDialog_Browse;
  public static String InstallDialog_DialogTitle;
  public static String InstallDialog_Message_selectDirMsg;
  public static String InstallDialog_Message_selectDirText;
  public static String InstallDialog_Message_selectLocation;
  public static String InstallDialog_Location;
  public static String InstallDialog_ShellTitle;
  public static String InstallDialog_switchTarget;
  public static String InstallDialog_TargetDescription;
  public static String InstallDialog_TargetGroup;
  public static String InstallDialog_validPath;

  public static String InstallRAPTargetAction_FailedExecuteCommand;

  public static String InstallRAPTargetHandler_InstallFailed;
  public static String InstallRAPTargetHandler_InstallInterrupted;
  public static String InstallRAPTargetHandler_SwitchTargetFailed;
  public static String InstallRAPTargetHandler_SwitchTargetInterrupted;

  public static String TargetProvider_ArchiveNotFound;
  public static String TargetProvider_FailureCreateScript;
  public static String TargetProvider_Installing;

  public static String TargetProvider_InvalidTargetDest;
  public static String TargetProvider_SourceNotFound;
  
  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, IntroMessages.class );
  }

  private IntroMessages() {
  }
}
