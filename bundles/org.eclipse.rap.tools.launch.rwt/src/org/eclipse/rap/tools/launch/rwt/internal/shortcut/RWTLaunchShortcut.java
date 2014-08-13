/*******************************************************************************
 * Copyright (c) 2011, 2014 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.shortcut;

import static org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget.APP_CONFIG;
import static org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget.ENTRY_POINT;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.shortcut.LaunchConfigFinder.LaunchConfigSelector;
import org.eclipse.rap.tools.launch.rwt.internal.util.StatusUtil;


public class RWTLaunchShortcut extends JavaLaunchShortcut {

  protected ILaunchConfigurationType getConfigurationType() {
    return RWTLaunchConfig.getType();
  }

  protected ILaunchConfiguration createConfiguration( IType type ) {
    try {
      return   new TypeInspector( type ).isEntryPointType()
             ? createEntryPointLaunchConfig( type )
             : createAppConfigLaunchConfig( type );
    } catch( CoreException ce ) {
      StatusUtil.showCoreException( ce );
    }
    return null;
  }

  private ILaunchConfiguration createEntryPointLaunchConfig( IType type ) throws CoreException {
    return LaunchConfigCreator.create( type, ENTRY_POINT, null );
  }

  private ILaunchConfiguration createAppConfigLaunchConfig( IType type ) throws CoreException {
    String servletPath = askForServletPath();
    return servletPath == null ? null : LaunchConfigCreator.create( type, APP_CONFIG, servletPath );
  }

  protected String askForServletPath() {
    ServletPathInputDialog dialog = new ServletPathInputDialog( getShell() );
    return dialog.open() == Window.OK ? dialog.getValue() : null;
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
    ApplicationSearchEngine engine = new ApplicationSearchEngine( context );
    return engine.search( javaElements );
  }

  protected String getTypeSelectionTitle() {
    return "Select RWT Application";
  }

  protected String getEditorEmptyMessage() {
    return "Editor does not contain an entry point or application configuration.";
  }

  protected String getSelectionEmptyMessage() {
    return "Selection does not contain an entry point or application configuration.";
  }

}
