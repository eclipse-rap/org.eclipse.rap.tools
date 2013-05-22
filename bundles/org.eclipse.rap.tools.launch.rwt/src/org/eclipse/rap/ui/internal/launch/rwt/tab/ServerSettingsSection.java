/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class ServerSettingsSection extends RWTLaunchTab {

  private Button cbManualPort;
  private Spinner spnPort;
  private Button cbSessionTimeout;
  private Spinner spnSessionTimeout;
  private Button cbContextPath;
  private Text txtContextPath;

  public String getName() {
    return "Server Settings";
  }

  public void createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setText( "Server Settings" );
    group.setLayout( new GridLayout( 4, false ) );
    cbManualPort = createCheckButton( group, "Use a fixed &port:" );
    cbManualPort.addSelectionListener( new UpdateConfigSelectionListener() );
    spnPort = new Spinner( group, SWT.BORDER );
    spnPort.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    spnPort.setMinimum( RWTLaunchConfig.MIN_PORT_NUMBER );
    spnPort.setMaximum( RWTLaunchConfig.MAX_PORT_NUMBER );
    spnPort.addModifyListener( new UpdateConfigModifyListener() );
    cbSessionTimeout = new Button( group, SWT.CHECK );
    GridDataFactory.fillDefaults().indent( 25, 0 ).applyTo( cbSessionTimeout );
    cbSessionTimeout.setText( "Session &timeout [min]:" );
    cbSessionTimeout.addSelectionListener( new UpdateConfigSelectionListener() );
    spnSessionTimeout = new Spinner( group, SWT.BORDER );
    spnSessionTimeout.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    spnSessionTimeout.setMinimum( RWTLaunchConfig.MIN_SESSION_TIMEOUT );
    spnSessionTimeout.setMaximum( RWTLaunchConfig.MAX_SESSION_TIMEOUT );
    spnSessionTimeout.addModifyListener( new UpdateConfigModifyListener() );
    cbContextPath = new Button( group, SWT.CHECK );
    cbContextPath.setText( "Context pat&h:" );
    cbContextPath.addSelectionListener( new UpdateConfigSelectionListener() );
    txtContextPath = new Text( group, SWT.BORDER | SWT.SINGLE );
    GridDataFactory.fillDefaults().grab( true, false ).span( 3, 1 ).applyTo( txtContextPath );
    txtContextPath.addModifyListener( new UpdateConfigModifyListener() );
    updateEnablement();
    setControl( group );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
  }

  public void initializeFrom( RWTLaunchConfig launchConfig ) {
    cbManualPort.setSelection( launchConfig.getUseManualPort() );
    spnPort.setSelection( launchConfig.getPort() );
    cbSessionTimeout.setSelection( launchConfig.getUseSessionTimeout() );
    spnSessionTimeout.setSelection( launchConfig.getSessionTimeout() );
    cbContextPath.setSelection( launchConfig.getUseManualContextPath() );
    txtContextPath.setText( launchConfig.getContextPath() );
    updateEnablement();
  }

  public void performApply( RWTLaunchConfig launchConfig ) {
    launchConfig.setUseManualPort( cbManualPort.getSelection() );
    launchConfig.setPort( spnPort.getSelection() );
    launchConfig.setUseSessionTimeout( cbSessionTimeout.getSelection() );
    launchConfig.setSessionTimeout( spnSessionTimeout.getSelection() );
    launchConfig.setUseManualContextPath( cbContextPath.getSelection() );
    launchConfig.setContextPath( txtContextPath.getText().trim() );
  }

  private void updateEnablement() {
    spnPort.setEnabled( cbManualPort.getSelection() );
    spnSessionTimeout.setEnabled( cbSessionTimeout.getSelection() );
    txtContextPath.setEnabled( cbContextPath.getSelection() );
  }

  private class UpdateConfigSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      updateEnablement();
      updateLaunchConfigurationDialog();
    }
  }

  private class UpdateConfigModifyListener implements ModifyListener {
    public void modifyText( ModifyEvent event ) {
      updateLaunchConfigurationDialog();
    }
  }
}
