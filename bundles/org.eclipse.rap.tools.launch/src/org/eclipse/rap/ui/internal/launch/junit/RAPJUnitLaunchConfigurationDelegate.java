/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.junit;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.ui.launcher.EquinoxLaunchConfiguration;
import org.eclipse.rap.ui.internal.launch.LaunchMessages;
import org.eclipse.rap.ui.internal.launch.RAPLaunchDelegate;


public class RAPJUnitLaunchConfigurationDelegate extends JUnitLaunchConfigurationDelegate {

  private JUnitLaunchConfigurationDelegate julc
    = new org.eclipse.pde.ui.launcher.JUnitLaunchConfigurationDelegate();
  private EquinoxLaunchConfigurationExtension elc = new EquinoxLaunchConfigurationExtension();
  private RAPLaunchDelegate rld = new RAPLaunchDelegate( true );

  private static final class EquinoxLaunchConfigurationExtension extends EquinoxLaunchConfiguration
  {

    public void preLaunchCheck( ILaunchConfiguration configuration,
                                ILaunch launch,
                                IProgressMonitor monitor )
    throws CoreException
    {
      super.preLaunchCheck( configuration, launch, monitor );
    }
  }

  public boolean finalLaunchCheck( ILaunchConfiguration configuration,
                                   String mode,
                                   IProgressMonitor monitor )
    throws CoreException
  {
    rld.finalLaunchCheck( configuration, mode, monitor );
    return super.finalLaunchCheck( configuration, mode, monitor );
  }

  public void launch( ILaunchConfiguration configuration,
                      String mode,
                      ILaunch launch,
                      IProgressMonitor monitor )
    throws CoreException
  {
    SubProgressMonitor subMonitor = rld.doPreLaunch( configuration, launch, monitor );
    super.launch( configuration, mode, launch, subMonitor );
  }

  protected void preLaunchCheck( ILaunchConfiguration configuration,
                                 ILaunch launch,
                                 IProgressMonitor monitor )
    throws CoreException
  {
    super.preLaunchCheck( configuration, launch, new SubProgressMonitor( monitor, 2 ) );
    elc.preLaunchCheck( configuration, launch, new SubProgressMonitor( monitor, 2 ) );
  }

  public IVMRunner getVMRunner( ILaunchConfiguration configuration, String mode )
    throws CoreException
  {
    return julc.getVMRunner( configuration, mode );
  }

  public String verifyMainTypeName( ILaunchConfiguration configuration ) throws CoreException {
    return "org.eclipse.equinox.launcher.Main"; //$NON-NLS-1$
  }

  public String[] getClasspath( ILaunchConfiguration configuration ) throws CoreException {
    return julc.getClasspath( configuration );
  }

  public String getProgramArguments( ILaunchConfiguration configuration ) throws CoreException {
    String[] programArguments = elc.getProgramArguments( configuration );
    return concatArguments( programArguments );
  }

  private String concatArguments( String[] arguments ) {
    StringBuffer result = new StringBuffer();
    for( int i = 0; i < arguments.length; i++ ) {
      result.append( arguments[ i ] );
      result.append( " " ); //$NON-NLS-1$
    }
    return result.toString().trim();
  }

  public String getVMArguments( ILaunchConfiguration configuration ) throws CoreException {
    String[] arguments = rld.getVMArguments( configuration );
    return concatArguments( arguments );
  }

  protected void collectExecutionArguments( ILaunchConfiguration config,
                                            List vmArguments,
                                            List programArguments )
    throws CoreException
  {
    super.collectExecutionArguments( config, vmArguments, programArguments );
    String name = "-loaderpluginname"; //$NON-NLS-1$
    String value = "org.eclipse.rap.junit.runtime"; //$NON-NLS-1$
    replaceArguments( programArguments, name, value );
    programArguments.add( "-testpluginname" ); //$NON-NLS-1$
    programArguments.add( getTestPluginId( config ) );

  }

  private void replaceArguments( List list,
                                 String name,
                                 String value )
  {
    int pos = list.indexOf( name );
    list.remove( pos + 1 );
    list.add( pos + 1, value );
  }

  private String getTestPluginId( ILaunchConfiguration configuration ) throws CoreException {
    IJavaProject javaProject = getJavaProject( configuration );
    IProject project = javaProject.getProject();
    IPluginModelBase model = PluginRegistry.findModel( project );
    if( model == null ) {
      String msg = NLS.bind( LaunchMessages.RAPLaunchDelegate_Error_NotAPlugin, project.getName() );
      abort( msg, null, IStatus.OK );
    }
    if( model instanceof IFragmentModel ) {
      return ( ( IFragmentModel )model ).getFragment().getPluginId();
    }
    return model.getPluginBase().getId();
  }

}
