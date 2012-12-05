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
package org.eclipse.rap.ui.internal.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.pde.ui.launcher.OSGiLaunchShortcut;
import org.eclipse.ui.IEditorPart;


public final class RAPLaunchShortcut extends OSGiLaunchShortcut {

  private static final String LAUNCH_CONFIGURATION_TYPE
    = "org.eclipse.rap.ui.launch.RAPLauncher"; //$NON-NLS-1$

  /////////////////////////////////
  // ILaunchShortcut implementation

  public void launch( final ISelection selection, final String mode ) {
    launch( mode );
  }

  public void launch( final IEditorPart editor, final String mode ) {
    launch( mode );
  }

  ///////////////////////////////////
  // AbstractLaunchShortcut overrides

  protected String getLaunchConfigurationTypeName() {
    return LAUNCH_CONFIGURATION_TYPE;
  }

  protected void initializeConfiguration(
    final ILaunchConfigurationWorkingCopy workingCopy )
  {
    super.initializeConfiguration( workingCopy );
  }

  protected boolean isGoodMatch( final ILaunchConfiguration config ) {
    return true;
  }
}
