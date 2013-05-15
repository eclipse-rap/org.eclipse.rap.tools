/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class RAPSettingsSection extends RWTLaunchTab {

  private Button cbDevelopmentMode;

  public String getName() {
    return "RAP Settings";
  }

  public void createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setText( "RAP Settings" );
    group.setLayout( new GridLayout( 2, false ) );
    cbDevelopmentMode = createCheckButton( group, "Start in &development mode" );
    cbDevelopmentMode.addSelectionListener( new UpdateConfigSelectionListener() );
    setControl( group );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
  }

  @Override
  public void initializeFrom( RWTLaunchConfig launchConfig ) {
    cbDevelopmentMode.setSelection( launchConfig.getDevelopmentMode() );
  }

  @Override
  public void performApply( RWTLaunchConfig launchConfig ) {
    launchConfig.setDevelopmentMode( cbDevelopmentMode.getSelection() );
  }

  private class UpdateConfigSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      updateLaunchConfigurationDialog();
    }
  }

}
