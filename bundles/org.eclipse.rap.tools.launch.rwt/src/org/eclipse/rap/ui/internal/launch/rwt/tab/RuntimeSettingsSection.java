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

import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class RuntimeSettingsSection extends RWTLaunchTab {

  private Button cbManualPort;
  private Spinner spnPort;

  public String getName() {
    return "Runtime Settings";
  }

  public void createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setText( "Runtime Settings" );
    group.setLayout( new GridLayout( 2, false ) );
    cbManualPort = createCheckButton( group, "Manual port configuration" );
    cbManualPort.addSelectionListener( new ManualPortSelectionListener() );
    spnPort = new Spinner( group, SWT.BORDER );
    spnPort.setLayoutData( new GridData( 60, SWT.DEFAULT ) );
    spnPort.setMinimum( RWTLaunchConfig.MIN_PORT_NUMBER );
    spnPort.setMaximum( RWTLaunchConfig.MAX_PORT_NUMBER );
    spnPort.addModifyListener( new TextModifyListener() );
    updateEnablement();
    setControl( group );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
  }

  public void initializeFrom( RWTLaunchConfig launchConfig ) {
    cbManualPort.setSelection( launchConfig.getUseManualPort() );
    spnPort.setSelection( launchConfig.getPort() );
    updateEnablement();
  }

  public void performApply( RWTLaunchConfig launchConfig ) {
    launchConfig.setUseManualPort( cbManualPort.getSelection() );
    launchConfig.setPort( spnPort.getSelection() );
  }

  private void updateEnablement() {
    spnPort.setEnabled( cbManualPort.getSelection() );
  }
  
  private class ManualPortSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      updateEnablement();
      updateLaunchConfigurationDialog();
    }
  }

  private class TextModifyListener implements ModifyListener {
    public void modifyText( ModifyEvent event ) {
      updateLaunchConfigurationDialog();
    }
  }
}
