/*******************************************************************************
 * Copyright (c) 2007, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.intro.internal.target;

import org.eclipse.osgi.util.NLS;

public class IntroMessages extends NLS {

  private static final String BUNDLE_NAME
    = "org.eclipse.rap.tools.intro.internal.target.messages"; //$NON-NLS-1$

  public static String InstallDialog_ShellTitle;
  public static String InstallDialog_switchTarget;
  public static String InstallDialog_TargetDescription;

  public static String InstallRAPTargetAction_FailedExecuteCommand;

  public static String InstallRAPTargetHandler_InstallFailed;
  public static String InstallRAPTargetHandler_SwitchTargetInterrupted;

  public static String InstallTargetDialog_TargetDescriptionMsg;

  public static String TargetProvider_Installing;
  public static String TargetProvider_Creating_Definition;

  public static String TargetSwitcher_NoInternetConnectionAvailableErrorMsg;
  public static String TargetSwitcher_TargedDefinitionErrorMessage;
  public static String TargetSwitcher_TargetRepositoryProblemErrorMsg;

  static {
    // initialize resource bundle
    NLS.initializeMessages( BUNDLE_NAME, IntroMessages.class );
  }

  private IntroMessages() {
  }

}
