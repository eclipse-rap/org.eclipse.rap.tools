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
package org.eclipse.rap.tools.launch.rwt.internal.tab;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.util.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class RWTMainTab extends RWTLaunchTab {

  private ProjectSection projectSection;
  private ApplicationSection applicationSection;
  private BrowserSection  browserSection;
  private ServerSettingsSection serverSettingsSection;
  private RAPSettingsSection rapSettings;
  private ILaunchConfigurationListener launchConfigListener;

  public RWTMainTab() {
    projectSection = new ProjectSection();
    applicationSection = new ApplicationSection();
    browserSection = new BrowserSection();
    serverSettingsSection = new ServerSettingsSection();
    rapSettings = new RAPSettingsSection();
    addLaunchConfigListener();
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
    applicationSection.createControl( container );
    browserSection.createControl( container );
    serverSettingsSection.createControl( container );
    rapSettings.createControl( container );
    Dialog.applyDialogFont( container );
    setControl( container );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
  }

  public void setLaunchConfigurationDialog( ILaunchConfigurationDialog dialog ) {
    super.setLaunchConfigurationDialog( dialog );
    projectSection.setLaunchConfigurationDialog( dialog );
    applicationSection.setLaunchConfigurationDialog( dialog );
    serverSettingsSection.setLaunchConfigurationDialog( dialog );
    browserSection.setLaunchConfigurationDialog( dialog );
    rapSettings.setLaunchConfigurationDialog( dialog );
  }

  public void initializeFrom( RWTLaunchConfig launchConfig ) {
    projectSection.initializeFrom( launchConfig );
    applicationSection.initializeFrom( launchConfig );
    serverSettingsSection.initializeFrom( launchConfig );
    browserSection.initializeFrom( launchConfig );
    rapSettings.initializeFrom( launchConfig );
  }

  public void performApply( RWTLaunchConfig launchConfig ) {
    projectSection.performApply( launchConfig );
    applicationSection.performApply( launchConfig );
    serverSettingsSection.performApply( launchConfig );
    browserSection.performApply( launchConfig );
    rapSettings.performApply( launchConfig );
  }

  @Override
  public void dispose() {
    getLaunchManager().removeLaunchConfigurationListener( launchConfigListener );
    super.dispose();
  }

  private void addLaunchConfigListener() {
    launchConfigListener = getLaunchConfigListener();
    getLaunchManager().addLaunchConfigurationListener( launchConfigListener );
  }

  private ILaunchConfigurationListener getLaunchConfigListener() {
    return new ILaunchConfigurationListener() {
      public void launchConfigurationChanged( ILaunchConfiguration configuration ) {
        browserSection.updateApplicationUrl( new RWTLaunchConfig( configuration ) );
      }
      public void launchConfigurationAdded( ILaunchConfiguration configuration ) {
        // Do nothing
      }
      public void launchConfigurationRemoved( ILaunchConfiguration configuration ) {
        // Do nothing
      }
    };
  }

}
