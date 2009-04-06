/*******************************************************************************
 * Copyright (c) 2009 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro.target;

import java.io.*;
import java.net.URL;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.pde.internal.core.LoadTargetOperation;
import org.eclipse.pde.internal.core.itarget.ITarget;
import org.eclipse.pde.internal.core.itarget.ITargetModel;
import org.eclipse.pde.internal.core.target.TargetModel;
import org.eclipse.rap.ui.internal.intro.ErrorUtil;
import org.eclipse.rap.ui.internal.intro.IntroPlugin;


public final class TargetSwitcher34 {

  private static final String TARGET_FILE = "target/rap.target"; //$NON-NLS-1$
  private static final String ECLIPSE = "eclipse"; //$NON-NLS-1$

  public static void switchTarget( final String targetDestination,
                                   final IProgressMonitor monitor )
    throws CoreException
  {
    ITargetModel targetModel = getTargetModel34( targetDestination );
    if( targetModel.isLoaded() ) {
      ITarget target = targetModel.getTarget();
      LoadTargetOperation operation = new LoadTargetOperation( target );
      ResourcesPlugin.getWorkspace().run( operation, monitor );
    }
  }

  private static ITargetModel getTargetModel34( final String targetDestination )
    throws CoreException
  {
    ITargetModel result = new TargetModel();
    URL entry = IntroPlugin.getDefault().getBundle().getEntry( TARGET_FILE );
    try {
      InputStream inputStream = new BufferedInputStream( entry.openStream() );
      try {
        result.load( inputStream, true );
      } finally {
        inputStream.close();
      }
    } catch( IOException e ) {
      String msg = IntroMessages.InstallRAPTargetHandler_SwitchTargetFailed;
      IStatus status = ErrorUtil.createErrorStatus( msg, e );
      throw new CoreException( status );
    }
    File path = new File( targetDestination, ECLIPSE );
    result.getTarget().getLocationInfo().setPath( path.toString() );
    return result;
  }

  private TargetSwitcher34() {
    // prevent instantiation
  }
}
