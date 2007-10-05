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
package org.eclipse.rap.ui.internal.target;


import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;


public final class RAPTargetDestVariableInitializer
  implements IValueVariableInitializer
{
  public static final String VARIABLE_NAME = "rap_target_dest"; //$NON-NLS-1$

  // TODO [bm] generic approach for version handling?
  private static final String VERSION_FOLDER = "org.eclipse.rap.target-1.0.0"; //$NON-NLS-1$

  public void initialize( final IValueVariable variable ) {
    URL configLocation = Platform.getConfigurationLocation().getURL();
    File targetDest = new File( configLocation.getFile(), VERSION_FOLDER );
    variable.setValue( targetDest.toString() );
  }
}
