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
package org.eclipse.rap.ui.internal.launch.rwt.shortcut;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.shortcut.LaunchConfigFinder.LaunchConfigSelector;
import org.eclipse.rap.ui.internal.launch.rwt.util.StatusUtil;


public class RWTLaunchShortcut extends JavaLaunchShortcut {

  protected ILaunchConfigurationType getConfigurationType() {
    return RWTLaunchConfig.getType();
  }

  protected ILaunchConfiguration createConfiguration( IType type ) {
    ILaunchConfiguration result = null;
    try {
      result = LaunchConfigCreator.fromType( type );
    } catch( CoreException ce ) {
      StatusUtil.showCoreException( ce );
    }
    return result;
  }
  
  protected ILaunchConfiguration findLaunchConfiguration( IType type,
                                                          ILaunchConfigurationType configType )
  {
    LaunchConfigSelector launchConfigSelector = new LaunchConfigSelector() {
      public ILaunchConfiguration select( ILaunchConfiguration[] launchConfigs ) {
        return chooseConfiguration( Arrays.asList( launchConfigs ) );
      }
    };
    LaunchConfigFinder launchConfigFinder = new LaunchConfigFinder( launchConfigSelector );
    ILaunchConfiguration result = null;
    try {
      result = launchConfigFinder.forType( type );
    } catch( CoreException ce ) {
      StatusUtil.showCoreException( ce );
    }
    return result;
  }

  protected IType[] findTypes( Object[] elements, IRunnableContext context )
    throws InterruptedException, CoreException
  {
    IJavaElement[] javaElements = JavaElementUtil.adapt( elements );
    EntryPointSearchEngine engine = new EntryPointSearchEngine( context );
    return engine.search( javaElements );
  }

  protected String getTypeSelectionTitle() {
    return "Select RWT Application";
  }

  protected String getEditorEmptyMessage() {
    return "Editor does not contain an entry point.";
  }

  protected String getSelectionEmptyMessage() {
    return "Selection does not contain an entry point.";
  }
}
