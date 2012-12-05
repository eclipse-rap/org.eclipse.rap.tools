/*******************************************************************************
 * Copyright (c) 2011, 2012 Rüdiger Herrmann and others.
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

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.rap.ui.internal.launch.rwt.config.BrowserMode;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PreferencesUtil;


public class BrowserSection extends RWTLaunchTab {

  private static final String BROWSER_PREFERENCE_PAGE
    = "org.eclipse.ui.browser.preferencePage"; //$NON-NLS-1$

  private static final int MARGIN = 17;

  private Button cbOpenBrowser;
  private Button rbInternalBrowser;
  private Button rbExternalBrowser;
  private Label lblServletPath;
  private Text txtServletPath;

  public String getName() {
    return "Browser";
  }

  public void createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setText( "Browser" );
    group.setLayout( new GridLayout( 2, false ) );
    cbOpenBrowser = createCheckButton( group, "Open application in" );
    cbOpenBrowser.setLayoutData( newGridData( SWT.BEGINNING, true ) );
    cbOpenBrowser.addSelectionListener( new OpenBrowserSelectionListener() );
    Link lnkBrowserPrefs = new Link( group, SWT.NONE );
    lnkBrowserPrefs.setText( "<a>Configure browsers...</a>" );
    lnkBrowserPrefs.setLayoutData( newGridData( SWT.END, false ) );
    lnkBrowserPrefs.addSelectionListener( new BrowserPrefsSelectionListener() );
    rbInternalBrowser = createRadioButton( group, "Internal browser" );
    rbInternalBrowser.setLayoutData( newGridData( MARGIN, SWT.BEGINNING, false, 2 ) );
    rbInternalBrowser.addSelectionListener( new BrowserSelectionListener() );
    rbExternalBrowser = createRadioButton( group, "External browser" );
    rbExternalBrowser.setLayoutData( newGridData( MARGIN, SWT.BEGINNING, false, 2 ) );
    rbExternalBrowser.addSelectionListener( new BrowserSelectionListener() );
    Composite cmpServletPath = new Composite( group, SWT.NONE );
    cmpServletPath.setLayout( new GridLayout( 2, false ) );
    cmpServletPath.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    lblServletPath = createLabel( cmpServletPath, "Servlet path" );
    lblServletPath.setLayoutData( newGridData( SWT.BEGINNING, false ) );
    txtServletPath = new Text( cmpServletPath, SWT.BORDER );
    txtServletPath.setLayoutData( newGridData( SWT.FILL, true ) );
    txtServletPath.addModifyListener( new TextModifyListener() );
    setControl( group );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
    updateEnablement();
  }

  public void initializeFrom( RWTLaunchConfig config ) {
    BrowserMode browserMode = config.getBrowserMode();
    cbOpenBrowser.setSelection( config.getOpenBrowser() );
    rbInternalBrowser.setSelection( browserMode == BrowserMode.INTERNAL );
    rbExternalBrowser.setSelection( browserMode == BrowserMode.EXTERNAL );
    txtServletPath.setText( config.getServletPath() );
    updateEnablement();
  }

  public void performApply( RWTLaunchConfig config ) {
    config.setOpenBrowser( cbOpenBrowser.getSelection() );
    if( rbExternalBrowser.getSelection() ) {
      config.setBrowserMode( BrowserMode.EXTERNAL );
    } else {
      config.setBrowserMode( BrowserMode.INTERNAL );
    }
    config.setServletPath( txtServletPath.getText().trim() );
  }

  private void updateEnablement() {
    boolean openBrowser = cbOpenBrowser.getSelection();
    rbInternalBrowser.setEnabled( openBrowser );
    rbExternalBrowser.setEnabled( openBrowser );
    lblServletPath.setEnabled( openBrowser );
    txtServletPath.setEnabled( openBrowser );
  }

  private void handleBrowserPrefsLink() {
    PreferenceDialog dialog
      = PreferencesUtil.createPreferenceDialogOn( getShell(), BROWSER_PREFERENCE_PAGE, null, null );
    dialog.open();
    dialog.close();
  }

  private static GridData newGridData( int horizontalAlign, boolean grapHorizontal ) {
    return newGridData( 0, horizontalAlign, grapHorizontal, 1 );
  }

  private static GridData newGridData( int indent,
                                       int horizontalAlign,
                                       boolean grapHorizontal,
                                       int span )
  {
    GridData result = new GridData( horizontalAlign, SWT.CENTER, grapHorizontal, false );
    result.horizontalIndent = indent;
    result.horizontalSpan = span;
    return result;
  }

  private class OpenBrowserSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      updateLaunchConfigurationDialog();
      updateEnablement();
    }
  }

  private class BrowserSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      updateLaunchConfigurationDialog();
    }
  }

  private class BrowserPrefsSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      handleBrowserPrefsLink();
    }
  }

  private class TextModifyListener implements ModifyListener {
    public void modifyText( ModifyEvent event ) {
      updateLaunchConfigurationDialog();
    }
  }
}
