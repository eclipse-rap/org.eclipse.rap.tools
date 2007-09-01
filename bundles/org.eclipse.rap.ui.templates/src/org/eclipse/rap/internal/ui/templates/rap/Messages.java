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
package org.eclipse.rap.internal.ui.templates.rap;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
  public static String helloRAPTemplate_appClass;
  public static String helloRAPTemplate_appWindowTitle;
  public static String helloRAPTemplate_packageNmae;
  public static String helloRAPTemplate_pageDescr;
  public static String helloRAPTemplate_pageTitle;
  public static String helloRAPTemplate_perspectiveName;
  public static String helloRAPTemplate_windowTitle;
  public static String helloRAPWizard_windowTitle;

  public static String mailRAPTemplate_appClass;
  public static String mailRAPTemplate_appWindowTitle;
  public static String mailRAPTemplate_categoryName;
  public static String mailRAPTemplate_mailboxCmdDescr;
  public static String mailRAPTemplate_mailboxCmdName;
  public static String mailRAPTemplate_mailboxViewName;
  public static String mailRAPTemplate_messageViewName;
  public static String mailRAPTemplate_openMsgCmdDescr;
  public static String mailRAPTemplate_openMsgCmdName;
  public static String mailRAPTemplate_packageName;
  public static String mailRAPTemplate_pageDescr;
  public static String mailRAPTemplate_pageTitle;
  public static String mailRAPTemplate_windowTitle;
  public static String mailRAPWizard_windowTitle;

  public static String viewRAPTemplate_appClass;
  public static String viewRAPTemplate_appWindowTitle;
  public static String viewRAPTemplate_packageName;
  public static String viewRAPTemplate_pageDescr;
  public static String viewRAPTemplate_pageTitle;
  public static String viewRAPTemplate_perspectiveName;
  public static String viewRAPTemplate_viewName;
  public static String viewRAPTemplate_windowTitle;
  public static String viewRAPWizard_windowTitle;

  private static final String BUNDLE_NAME 
    = "org.eclipse.rap.internal.ui.templates.rap.messages"; //$NON-NLS-1$

  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
    // avoid instantiation
  }
}
