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

import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.util.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class RWTMainTab extends RWTLaunchTab {

  private ProjectSection projectSection;
  private EntryPointSection entryPointSection;
  private RuntimeSettingsSection runtimeSettingsSection;
  private BrowserSection  browserSection;
  
  public RWTMainTab() {
    projectSection = new ProjectSection();
    entryPointSection = new EntryPointSection();
    runtimeSettingsSection = new RuntimeSettingsSection();
    browserSection = new BrowserSection();
  }
  
  public String getName() {
    return "Main";
  }
  
  public Image getImage() {
    return Images.getImage( Images.VIEW_MAIN_TAB );
  }

  public void createControl( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    container.setLayout( new GridLayout( 1, false ) );
    projectSection.createControl( container );
    entryPointSection.createControl( container );
    runtimeSettingsSection.createControl( container );
    browserSection.createControl( container );
    Dialog.applyDialogFont( container );
    setControl( container );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
  }

  public void setLaunchConfigurationDialog( ILaunchConfigurationDialog dialog ) {
    super.setLaunchConfigurationDialog( dialog );
    
    entryPointSection.setLaunchConfigurationDialog( dialog );
    runtimeSettingsSection.setLaunchConfigurationDialog( dialog );
    browserSection.setLaunchConfigurationDialog( dialog );
  }

  public void initializeFrom( RWTLaunchConfig launchConfig ) {
    projectSection.initializeFrom( launchConfig );
    entryPointSection.initializeFrom( launchConfig );
    runtimeSettingsSection.initializeFrom( launchConfig );
    browserSection.initializeFrom( launchConfig );
  }

  public void performApply( RWTLaunchConfig launchConfig ) {
    projectSection.performApply( launchConfig );
    entryPointSection.performApply( launchConfig );
    runtimeSettingsSection.performApply( launchConfig );
    browserSection.performApply( launchConfig );
  }
}
