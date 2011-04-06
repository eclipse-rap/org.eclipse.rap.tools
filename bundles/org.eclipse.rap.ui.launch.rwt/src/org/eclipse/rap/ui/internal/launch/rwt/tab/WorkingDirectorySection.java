/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.WorkingDirectoryBlock;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;


public class WorkingDirectorySection extends WorkingDirectoryBlock {

  protected WorkingDirectorySection() {
    super( RWTLaunchConfig.WORKING_DIRECTORY );
  }

  protected IProject getProject( ILaunchConfiguration configuration ) throws CoreException {
    RWTLaunchConfig config = new RWTLaunchConfig( configuration );
    IProject result = null;
    IProject project = findProject( config.getProjectName() );
    if( project != null && project.exists() && project.isOpen() ) {
      result = project;
    }
    return result;
  }
  
  protected void log( CoreException exception ) {
    setErrorMessage( exception.getMessage() );
  }

  private static IProject findProject( String projectName ) {
    IProject result;
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    try {
      result = workspaceRoot.getProject( projectName );
    } catch( IllegalArgumentException iae ) {
      result = null;
    }
    return result;
  }
}
