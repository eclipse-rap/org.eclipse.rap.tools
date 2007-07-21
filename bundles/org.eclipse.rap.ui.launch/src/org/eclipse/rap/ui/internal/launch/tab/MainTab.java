/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.ui.internal.launch.tab;

import java.util.logging.Level;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig.BrowserMode;
import org.eclipse.rap.ui.internal.launch.util.ErrorUtil;
import org.eclipse.rap.ui.internal.launch.util.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PreferencesUtil;


final class MainTab extends AbstractLaunchConfigurationTab {

  private static final String BROWSER_PREFERENCE_PAGE 
    = "org.eclipse.ui.browser.preferencePage"; //$NON-NLS-1$

  private static final int MAX_PORT_NUMBER = 65535;

  private static final Level[] LOG_LEVELS = {
    Level.OFF,
    Level.ALL,
    Level.WARNING,
    Level.INFO,
    Level.SEVERE,
    Level.FINE,
    Level.FINER,
    Level.FINEST
  };
  
  private final GridDataFactory fillHorizontal; 
  private final ModifyListener modifyListener;
  private final SelectionListener selectionListener;
  private final Image tabImage;
  private final Image warnImage;
  private Text txtEntryPoint;
  private Button cbTerminatePrevious;
  private Button rbInternalBrowser;
  private Button rbExternalBrowser;
  private Button cbManualPort;
  private Spinner spnPort;
  private ComboViewer cmbLogLevel;


  MainTab() {
    tabImage = Images.DESC_MAIN_TAB.createImage();
    warnImage = Images.WARNING.createImage();
    GridData gridData = new GridData( SWT.FILL, SWT.CENTER, true, false );
    fillHorizontal = GridDataFactory.createFrom( gridData );
    modifyListener = new ModifyListener() {
      public void modifyText( final ModifyEvent e ) {
        updateLaunchConfigurationDialog();
      }
    };
    selectionListener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        updateLaunchConfigurationDialog();
      }
    };
  }
  
  public void dispose() {
    tabImage.dispose();
    warnImage.dispose();
    super.dispose();
  }

  public void createControl( final Composite parent ) {
    // Create container that holds all sections
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayout( new GridLayout() );
    container.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    // Create sections
    createEntryPointSection( container );
    createBrowserModeSection( container );
    createRuntimeSettingsSection( container );
    createInfoSection( container );
    // Set container for this tab page
    Dialog.applyDialogFont( container );
    setControl( container );
  }

  public String getName() {
    return "&Main";
  }

  public Image getImage() {
    return tabImage;
  }
  
  public void initializeFrom( final ILaunchConfiguration config ) {
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    try {
      // EntryPoint
      txtEntryPoint.setText( rapConfig.getEntryPoint() );
      // TerminatePrevious
      cbTerminatePrevious.setSelection( rapConfig.getTerminatePrevious() );
      // Port
      cbManualPort.setSelection( rapConfig.getUseManualPort() );
      spnPort.setSelection( rapConfig.getPort() );
      // LogLevel
      Level logLevel = rapConfig.getLogLevel();
      StructuredSelection selection = new StructuredSelection( logLevel );
      cmbLogLevel.setSelection( selection );
      // BrowserMode
      if( BrowserMode.EXTERNAL.equals( rapConfig.getBrowserMode() ) ) {
        rbExternalBrowser.setSelection( true );
        rbInternalBrowser.setSelection( false );
      } else {
        rbExternalBrowser.setSelection( false );
        rbInternalBrowser.setSelection( true );
      }
    } catch( CoreException e ) {
      ErrorUtil.show( null, e );
    }
  }

  public void performApply( final ILaunchConfigurationWorkingCopy config ) {
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    // EntryPoint
    rapConfig.setEntryPoint( txtEntryPoint.getText() );
    // TerminatePrevious
    rapConfig.setTerminatePrevious( cbTerminatePrevious.getSelection() );
    // Port
    rapConfig.setUseManualPort( cbManualPort.getSelection() );
    rapConfig.setPort( spnPort.getSelection() );
    // LogLevel
    Level logLevel = Level.OFF;
    ISelection selection = cmbLogLevel.getSelection();
    if( !selection.isEmpty() ) {
      IStructuredSelection ssel = ( IStructuredSelection )selection;
      logLevel = ( Level )ssel.getFirstElement();
    }
    rapConfig.setLogLevel( logLevel );
    // BrowserMode
    if( rbExternalBrowser.getSelection() ) {
      rapConfig.setBrowserMode( BrowserMode.EXTERNAL );
    } else {
      rapConfig.setBrowserMode( BrowserMode.INTERNAL );
    }
    // Bring widget states up to date
    spnPort.setEnabled( cbManualPort.getSelection() );
    setDirty( true );
  }

  public void setDefaults( final ILaunchConfigurationWorkingCopy config ) {
    RAPLaunchConfig.setDefaults( config );
  }
  
  ///////////////////////////////////
  // Helping methods to create the UI
  
  private void createEntryPointSection( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayout( new GridLayout( 2, false ) );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( "&Entry Point to Run" );
    group.setLayout( new GridLayout( 2, false ) );
    txtEntryPoint = new Text( group, SWT.BORDER );
    txtEntryPoint.setLayoutData( fillHorizontal.create() );
    txtEntryPoint.addModifyListener( modifyListener );
    Button btnBrowse = new Button( group, SWT.PUSH );
    btnBrowse.setText( "&Browse..." );
    btnBrowse.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        handleEntryPointBrowseButton();
      }
    } );
    cbTerminatePrevious = new Button( group, SWT.CHECK );
    String text = "&Terminate possibly running previous launch";
    cbTerminatePrevious.setText( text );
    cbTerminatePrevious.addSelectionListener( selectionListener );
  }

  private void createBrowserModeSection( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( "Browser" );
    group.setLayout( new GridLayout( 2, false ) );
    Label lblBrowserMode = new Label( group, SWT.NONE );
    GridDataFactory grab = GridDataFactory.swtDefaults();
    grab.grab( true, false );
    lblBrowserMode.setLayoutData( grab.create() );
    lblBrowserMode.setText( "Run in" );
    Link lnkBrowserPrefs = new Link( group, SWT.NONE );
    lnkBrowserPrefs.setText( "<a>Configure Browsers...</a>" );
    lnkBrowserPrefs.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        handleBrowserPrefsLink();
      }
    } );
    GridDataFactory span2Cols = GridDataFactory.swtDefaults();
    span2Cols.span( 2, SWT.DEFAULT );
    rbInternalBrowser = new Button( group, SWT.RADIO );
    rbInternalBrowser.setLayoutData( span2Cols.create() );
    rbInternalBrowser.setText( "Intern&al Browser" );
    rbInternalBrowser.addSelectionListener( selectionListener );
    rbExternalBrowser = new Button( group, SWT.RADIO );
    rbExternalBrowser.setLayoutData( span2Cols.create() );
    rbExternalBrowser.setText( "E&xternal Browser" );
    rbExternalBrowser.addSelectionListener( selectionListener );
  }
  
  private void createRuntimeSettingsSection( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( "Runtime Settings" );
    group.setLayout( new GridLayout( 2, false ) );
    cbManualPort = new Button( group, SWT.CHECK );
    cbManualPort.setText( "Manual &Port configuration" );
    cbManualPort.addSelectionListener( selectionListener );
    spnPort = new Spinner( group, SWT.BORDER );
    spnPort.setLayoutData( new GridData( 60, SWT.DEFAULT ) );
    spnPort.setMaximum( MAX_PORT_NUMBER );
    spnPort.addModifyListener( modifyListener );
    Label lblLogLevel = new Label( group, SWT.NONE );
    lblLogLevel.setText( "Client-side &Log Level" );
    cmbLogLevel = new ComboViewer( group, SWT.DROP_DOWN | SWT.READ_ONLY );
    cmbLogLevel.getCombo().setVisibleItemCount( LOG_LEVELS.length );
    cmbLogLevel.setLabelProvider( new LabelProvider() );
    cmbLogLevel.setContentProvider( new LogLevelContentProvider() );
    cmbLogLevel.setInput( LOG_LEVELS );
    cmbLogLevel.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( final SelectionChangedEvent event ) {
        updateLaunchConfigurationDialog();
      }
    } );
  }

  // TODO [rh] This could be omitted if we could figure out which OSGi  
  //      framework is currently set. But my ingestigations so far showed that
  //      it would involve internal API of org.eclipse.pde.ui to obtain the 
  //      default OSGi framework
  private void createInfoSection( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( "Important Information" );
    group.setLayout( new FillLayout() );
    CLabel lblInfo = new CLabel( group, SWT.LEFT );
    lblInfo.setImage( warnImage );
    String text
      = "Please note, that the RAP Application Launcher only works with the "
      + "Equinox OSGi Framework (default setting on page 'Bundles').";
    lblInfo.setText( text );
  }
  
  ////////////////
  // Handle events

  private void handleEntryPointBrowseButton() {
    EntryPointSelectionDialog dialog 
      = new EntryPointSelectionDialog( getShell() );
    if( dialog.open() == Window.OK ) {
      Object[] selection = dialog.getResult();
      EntryPointExtension entryPoint = ( EntryPointExtension )selection[ 0 ];
      txtEntryPoint.setText( entryPoint.getParameter() );
    }
  }

  private void handleBrowserPrefsLink() {
    PreferenceDialog dialog 
      = PreferencesUtil.createPreferenceDialogOn( getShell(), 
                                                  BROWSER_PREFERENCE_PAGE, 
                                                  null,
                                                  null );
    dialog.open();
  }

  ////////////////
  // Inner classes
  
  private static final class LogLevelContentProvider
    implements IStructuredContentProvider
  {

    public Object[] getElements( final Object inputElement ) {
      return ( Object[] )inputElement;
    }

    public void dispose() {
      // do nothing
    }

    public void inputChanged( final Viewer viewer,
                              final Object oldInput,
                              final Object newInput )
    {
      // do nothing
    }
  }
}
