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

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.WorkingDirectoryBlock;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.util.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class ArgumentsTab extends RWTLaunchTab {
  
  WorkingDirectoryBlock workingDirectorySection;
  private final VMArgumentsSection vmArgumentsSection;
  
  public ArgumentsTab() {
    vmArgumentsSection = new VMArgumentsSection();
    workingDirectorySection = new WorkingDirectorySection();
  }
  
  public void createControl( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    container.setLayout( new GridLayout( 1, false ) );
    vmArgumentsSection.createControl( container );
    workingDirectorySection.createControl( container );
    Dialog.applyDialogFont( container );
    setControl( container );
  }
  
  public void setDefaults( ILaunchConfigurationWorkingCopy config ) {
    super.setDefaults( config );
    workingDirectorySection.setDefaults( config );
  }
  
  public void setLaunchConfigurationDialog( ILaunchConfigurationDialog dialog ) {
    super.setLaunchConfigurationDialog( dialog );
    vmArgumentsSection.setLaunchConfigurationDialog( dialog );
    workingDirectorySection.setLaunchConfigurationDialog( dialog );
  }
  
  public void initializeFrom( RWTLaunchConfig launchConfig ) {
    ILaunchConfiguration config = launchConfig.getUnderlyingLaunchConfig();
    vmArgumentsSection.initializeFrom( config );
    workingDirectorySection.initializeFrom( config );
  }
  
  public void performApply( RWTLaunchConfig launchConfig ) {
    ILaunchConfigurationWorkingCopy config = getWorkingCopy( launchConfig );
    vmArgumentsSection.performApply( config );
    workingDirectorySection.performApply( config );
  }
  
  public boolean isValid( ILaunchConfiguration config ) {
    boolean vmArgumentsValid = vmArgumentsSection.isValid( config );
    boolean workingDirectoryValid = workingDirectorySection.isValid( config );
    return vmArgumentsValid && workingDirectoryValid && super.isValid( config );
  }

  public void activated( ILaunchConfigurationWorkingCopy workingCopy ) {
    workingDirectorySection.initializeFrom( workingCopy );
  }
  
  public String getErrorMessage() {
    String result = workingDirectorySection.getErrorMessage();
    if( result == null ) {
      result = super.getErrorMessage();
    }
    return result;
  }

  public String getMessage() {
    String result = workingDirectorySection.getMessage();
    if( result == null ) {
      result = super.getMessage();
    }
    return result;
  }
  
  private static ILaunchConfigurationWorkingCopy getWorkingCopy( RWTLaunchConfig launchConfig ) {
    ILaunchConfiguration result = launchConfig.getUnderlyingLaunchConfig();
    return ( ILaunchConfigurationWorkingCopy )result;
  }
  
  public String getName() {
    return "Arguments";
  }
  
  public Image getImage() {
    return Images.getImage( Images.VIEW_ARGUMENTS_TAB );
  }
}
