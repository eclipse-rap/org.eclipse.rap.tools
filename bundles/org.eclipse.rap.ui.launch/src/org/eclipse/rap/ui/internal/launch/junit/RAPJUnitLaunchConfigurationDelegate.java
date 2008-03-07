/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
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
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.ui.launcher.EquinoxLaunchConfiguration;
import org.eclipse.rap.ui.internal.launch.RAPLaunchDelegate;

public class RAPJUnitLaunchConfigurationDelegate
  extends JUnitLaunchConfigurationDelegate
{
  private JUnitLaunchConfigurationDelegate julc
    = new org.eclipse.pde.ui.launcher.JUnitLaunchConfigurationDelegate();
  private EquinoxLaunchConfigurationExtension elc
    = new EquinoxLaunchConfigurationExtension();
  private RAPLaunchDelegate rld
    = new RAPLaunchDelegate( true );

  
  private final class EquinoxLaunchConfigurationExtension
    extends EquinoxLaunchConfiguration
  {
    
    public void preLaunchCheck( final ILaunchConfiguration configuration,
                                final ILaunch launch,
                                final IProgressMonitor monitor )
    throws CoreException
    {
      super.preLaunchCheck( configuration, launch, monitor );
    }
  }
  
  public boolean finalLaunchCheck( final ILaunchConfiguration configuration,
                                   final String mode,
                                   final IProgressMonitor monitor )
    throws CoreException
  {
    rld.finalLaunchCheck( configuration, mode, monitor );
    return super.finalLaunchCheck( configuration, mode, monitor );
  }

  public void launch( final ILaunchConfiguration configuration,
                      final String mode,
                      final ILaunch launch,
                      final IProgressMonitor monitor )
    throws CoreException
  {
    SubProgressMonitor subMonitor
      = rld.doPreLaunch( configuration, launch, monitor );
    super.launch( configuration, mode, launch, subMonitor );
  }
  
  protected void preLaunchCheck( final ILaunchConfiguration configuration,
                                 final ILaunch launch,
                                 final IProgressMonitor monitor )
    throws CoreException
  {
    super.preLaunchCheck( configuration,
                          launch, 
                          new SubProgressMonitor( monitor, 2 ) );
    elc.preLaunchCheck( configuration,
                        launch,
                        new SubProgressMonitor( monitor, 2 ) );
  }
  
  public IVMRunner getVMRunner( final ILaunchConfiguration configuration,
                                final String mode )
    throws CoreException
  {
    return julc.getVMRunner( configuration, mode );
  }

  public String verifyMainTypeName( final ILaunchConfiguration configuration )
    throws CoreException
  {
    return "org.eclipse.equinox.launcher.Main";
  }

  public String[] getClasspath( final ILaunchConfiguration configuration )
    throws CoreException
  {
    return julc.getClasspath( configuration );
  }

  public String getProgramArguments( final ILaunchConfiguration configuration )
    throws CoreException
  {
    String[] programArguments = elc.getProgramArguments( configuration );
    return concatArguments( programArguments );
  }

  private String concatArguments( final String[] arguments ) {
    StringBuffer result = new StringBuffer();
    for( int i = 0; i < arguments.length; i++ ) {
      result.append( arguments[ i ] );
      result.append( " " );
    }
    return result.toString().trim();
  }
  
  public String getVMArguments( final ILaunchConfiguration configuration )
    throws CoreException
  {
    String[] arguments = rld.getVMArguments( configuration );
    return concatArguments( arguments );
  }
  
  protected void collectExecutionArguments( final ILaunchConfiguration config,
                                            final List vmArguments,
                                            final List programArguments )
    throws CoreException
  {
    super.collectExecutionArguments( config, vmArguments, programArguments );
    String name = "-loaderpluginname";
    String value = "org.eclipse.rap.junit.runtime";
    replaceArguments( programArguments, name, value );
    programArguments.add( "-testpluginname" );
    programArguments.add( getTestPluginId( config ) );

  }

  private void replaceArguments( final List list,
                                 final String name,
                                 final String value )
  {
    int pos = list.indexOf( name );
    list.remove( pos + 1 );
    list.add( pos + 1, value );
  }

  private String getTestPluginId( final ILaunchConfiguration configuration )
    throws CoreException
  {
    IJavaProject javaProject = getJavaProject( configuration );
    IProject project = javaProject.getProject();
    IPluginModelBase model = PluginRegistry.findModel( project );
    if( model == null ) {
      String msg
        = NLS.bind( PDEUIMessages.JUnitLaunchConfiguration_error_notaplugin,
                    project.getName() );
      abort( msg, null, IStatus.OK );
    }
    if( model instanceof IFragmentModel ) {
      return ( ( IFragmentModel )model ).getFragment().getPluginId();
    }
    return model.getPluginBase().getId();
  }
}
