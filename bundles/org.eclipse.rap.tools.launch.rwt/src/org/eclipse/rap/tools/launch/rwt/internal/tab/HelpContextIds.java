/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.tab;

import org.eclipse.rap.tools.launch.rwt.internal.RWTLaunchActivator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;


class HelpContextIds {

  private static final String PREFIX = RWTLaunchActivator.getPluginId() + ".";

  static final String MAIN_TAB = PREFIX + "launch_configuration_dialog_main_tab";

  static void assign( Control control, String contextId ) {
    IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
    helpSystem.setHelp( control, contextId );
  }

  private HelpContextIds() {
    // prevent instantiation
  }

}
