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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.rap.ui.internal.launch.rwt.config.BrowserMode;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PreferencesUtil;


public class BrowserSection extends RWTLaunchTab {

  private static final String BROWSER_PREFERENCE_PAGE
    = "org.eclipse.ui.browser.preferencePage"; //$NON-NLS-1$

  private final GridDataFactory fillHorizontal;
  private Button cbOpenBrowser;
  private Button rbInternalBrowser;
  private Button rbExternalBrowser;
  private Label lblServletPath;
  private Text txtServletPath;

  public BrowserSection() {
    GridData gridData = new GridData( SWT.FILL, SWT.CENTER, true, false );
    fillHorizontal = GridDataFactory.createFrom( gridData );
  }

  public String getName() {
    return "Browser";
  }

  public void createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( "Open in Browser" );
    group.setLayout( new GridLayout() );
    createBrowserActivationPart( group );
    createServletPathPart( group );
    setControl( group );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
    updateEnablement();
  }

  private void createBrowserActivationPart( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayoutData( fillHorizontal.span( 2, 1 ).create() );
    GridLayout layout = new GridLayout( 2, false );
    layout.horizontalSpacing = 10;
    layout.verticalSpacing = 0;
    composite.setLayout( layout );
    cbOpenBrowser = new Button( composite, SWT.CHECK );
    cbOpenBrowser.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    cbOpenBrowser.setText( "&Open application in" );
    cbOpenBrowser.addSelectionListener( new OpenBrowserSelectionListener() );
    Link browserPrefsLink = createBrowserPrefsLink( composite );
    browserPrefsLink.setLayoutData( new GridData( SWT.END, SWT.CENTER, false, false ) );
    Composite modePart = createBrowserModePart( composite );
    modePart.setLayoutData( GridDataFactory.fillDefaults().span( 2, 1 ).indent( 17, 0 ).create() );
  }

  private Link createBrowserPrefsLink( Composite composite ) {
    Link link = new Link( composite, SWT.NONE );
    link.setText( "<a>Configure Browsers...</a>" );
    link.addSelectionListener( new BrowserPrefsSelectionListener() );
    return link;
  }

  private Composite createBrowserModePart( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    rbInternalBrowser = new Button( composite, SWT.RADIO );
    rbInternalBrowser.setText( "Internal web browser" );
    rbInternalBrowser.addSelectionListener( new BrowserSelectionListener() );
    rbExternalBrowser = new Button( composite, SWT.RADIO );
    rbExternalBrowser.setText( "E&xternal web browser" );
    rbExternalBrowser.addSelectionListener( new BrowserSelectionListener() );
    return composite;
  }

  private void createServletPathPart( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayoutData( fillHorizontal.create() );
    composite.setLayout( new GridLayout( 2, false ) );
    lblServletPath = new Label( composite, SWT.NONE );
    lblServletPath.setText( "&Servlet path:" );
    txtServletPath = new Text( composite, SWT.BORDER );
    txtServletPath.addModifyListener( new TextModifyListener() );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( txtServletPath );
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
