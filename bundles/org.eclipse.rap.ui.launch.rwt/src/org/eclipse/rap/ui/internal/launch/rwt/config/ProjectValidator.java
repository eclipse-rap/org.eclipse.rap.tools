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
package org.eclipse.rap.ui.internal.launch.rwt.config;

import java.text.MessageFormat;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;


class ProjectValidator extends Validator {

  static final int ERR_PROJECT_NOT_JAVA = 8001;

  ProjectValidator( RWTLaunchConfig config, ValidationResult validationResult ) {
    super( config, validationResult );
  }

  void validate() {
    String projectName = config.getProjectName();
    if(    isProjectNameSpecified( projectName ) 
        && isValidProjectName( projectName ) 
        && !isExistingJavaProject( projectName ) ) 
    {
      String text = "The project {0} does not exist.";
      String msg = MessageFormat.format( text, new Object[] { projectName } );
      addError( msg, ERR_PROJECT_NOT_JAVA );
    }
  }

  private boolean isProjectNameSpecified( String projectName ) {
    return projectName.length() > 0;
  }

  private static boolean isExistingJavaProject( String projectName ) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return JavaCore.create( root ).getJavaProject( projectName ).exists();
  }

  private static boolean isValidProjectName( String projectName ) {
    IPath projectPath = new Path( null, projectName ).makeAbsolute();
    return projectPath.segmentCount() == 1;
  }
}
