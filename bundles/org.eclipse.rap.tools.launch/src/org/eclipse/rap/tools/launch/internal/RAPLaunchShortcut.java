/*******************************************************************************
 * Copyright (c) 2007, 2013 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.internal;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.pde.ui.launcher.OSGiLaunchShortcut;
import org.eclipse.ui.IEditorPart;


public final class RAPLaunchShortcut extends OSGiLaunchShortcut {

  private static final String LAUNCH_CONFIGURATION_TYPE
    = "org.eclipse.rap.ui.launch.RAPLauncher"; //$NON-NLS-1$

  public void launch( ISelection selection, String mode ) {
    launch( mode );
  }

  public void launch( IEditorPart editor, String mode ) {
    launch( mode );
  }

  protected String getLaunchConfigurationTypeName() {
    return LAUNCH_CONFIGURATION_TYPE;
  }

  protected boolean isGoodMatch( ILaunchConfiguration config ) {
    return true;
  }

}
