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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.ui.launcher.BundlesTab;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfigValidator;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig.BrowserMode;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig.LibraryVariant;
import org.eclipse.rap.ui.internal.launch.util.ErrorUtil;
import org.eclipse.rap.ui.internal.launch.util.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PreferencesUtil;


public final class MainTab extends AbstractLaunchConfigurationTab {

  
  private static final String BROWSER_PREFERENCE_PAGE 
    = "org.eclipse.ui.browser.preferencePage"; //$NON-NLS-1$

  private final GridDataFactory fillHorizontal; 
  private final ModifyListener modifyListener;
  private final SelectionListener selectionListener;
  private final Image tabImage;
  private final Image warnImage;
  private Text txtServletName;
  private Text txtEntryPoint;
  private Button cbTerminatePrevious;
  private Button rbInternalBrowser;
  private Button rbExternalBrowser;
  private Button cbManualPort;
  private Spinner spnPort;
  private ComboViewer cmbLogLevel;
  private ComboViewer cmbLibVariant;

  public MainTab() {
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
  
  ////////////
  // Overrides
  
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
    createServletNameAndEntryPointSection( container );
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
      // ServletName
      txtServletName.setText( rapConfig.getServletName() );
      // EntryPoint
      txtEntryPoint.setText( rapConfig.getEntryPoint() );
      // TerminatePrevious
      cbTerminatePrevious.setSelection( rapConfig.getTerminatePrevious() );
      // Port
      cbManualPort.setSelection( rapConfig.getUseManualPort() );
      spnPort.setSelection( rapConfig.getPort() );
      // BrowserMode
      if( BrowserMode.EXTERNAL.equals( rapConfig.getBrowserMode() ) ) {
        rbExternalBrowser.setSelection( true );
        rbInternalBrowser.setSelection( false );
      } else {
        rbExternalBrowser.setSelection( false );
        rbInternalBrowser.setSelection( true );
      }
      // LogLevel
      Level logLevel = rapConfig.getLogLevel();
      StructuredSelection logSelection = new StructuredSelection( logLevel );
      cmbLogLevel.setSelection( logSelection );
      // LibraryVariant
      LibraryVariant libVariant = rapConfig.getLibraryVariant();
      StructuredSelection libSelection = new StructuredSelection( libVariant );
      cmbLibVariant.setSelection( libSelection );
    } catch( CoreException e ) {
      ErrorUtil.show( null, e );
    }
  }

  public void performApply( final ILaunchConfigurationWorkingCopy config ) {
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    // ServletName
    rapConfig.setServletName( txtServletName.getText() );
    // EntryPoint
    rapConfig.setEntryPoint( txtEntryPoint.getText() );
    // TerminatePrevious
    rapConfig.setTerminatePrevious( cbTerminatePrevious.getSelection() );
    // BrowserMode
    rapConfig.setBrowserMode( getBrowserMode() );
    // Manual Port
    spnPort.setEnabled( cbManualPort.getSelection() );
    // Port Number
    rapConfig.setUseManualPort( cbManualPort.getSelection() );
    rapConfig.setPort( spnPort.getSelection() );
    // Client-side log level
    rapConfig.setLogLevel( getLogLevel() );
    rapConfig.setLibraryVariant( getLibraryVariant() );
    validate( rapConfig );
    setDirty( true );
  }

  public void setDefaults( final ILaunchConfigurationWorkingCopy config ) {
    RAPLaunchConfig.setDefaults( config );
  }
  
  public boolean isValid( final ILaunchConfiguration launchConfig ) {
    return getErrorMessage() == null;
  }
  
  ///////////////////////////////////
  // Helping methods to create the UI
  
  private void createServletNameAndEntryPointSection( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayout( new GridLayout( 3, false ) );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( "Servlet and Entry Point to Run" );
    Label lblServletName = new Label( group, SWT.NONE );
    lblServletName.setText( "Ser&vlet Name" );
    txtServletName = new Text( group, SWT.BORDER );
    txtServletName.setLayoutData( fillHorizontal.create() );
    txtServletName.addModifyListener( modifyListener );
    Button btnBrowseServletName = new Button( group, SWT.PUSH );
    btnBrowseServletName.setText( "Bro&wse..." );
    btnBrowseServletName.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        handleBrowseServletName();
      }
    } );
    Label lblEntryPoint = new Label( group, SWT.NONE );
    lblEntryPoint.setText( "&Entry Point" );
    txtEntryPoint = new Text( group, SWT.BORDER );
    txtEntryPoint.setLayoutData( fillHorizontal.create() );
    txtEntryPoint.addModifyListener( modifyListener );
    Button btnBrowseEntryPoint = new Button( group, SWT.PUSH );
    btnBrowseEntryPoint.setText( "&Browse..." );
    btnBrowseEntryPoint.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        handleBrowseEntryPoint();
      }
    } );
    cbTerminatePrevious = new Button( group, SWT.CHECK );
    cbTerminatePrevious.setLayoutData( spanHorizontal( 3 ) );
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
    rbInternalBrowser = new Button( group, SWT.RADIO );
    rbInternalBrowser.setLayoutData( spanHorizontal( 2 ) );
    rbInternalBrowser.setText( "Intern&al Browser" );
    rbInternalBrowser.addSelectionListener( selectionListener );
    rbExternalBrowser = new Button( group, SWT.RADIO );
    rbExternalBrowser.setLayoutData( spanHorizontal( 2 ) );
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
    spnPort.setMinimum( RAPLaunchConfig.MIN_PORT_NUMBER );
    spnPort.setMaximum( RAPLaunchConfig.MAX_PORT_NUMBER );
    spnPort.addModifyListener( modifyListener );
    Label lblLogLevel = new Label( group, SWT.NONE );
    lblLogLevel.setText( "Client-side &Log Level" );
    cmbLogLevel = new ComboViewer( group, SWT.DROP_DOWN | SWT.READ_ONLY );
    int itemCount = RAPLaunchConfig.LOG_LEVELS.length;
    cmbLogLevel.getCombo().setVisibleItemCount( itemCount );
    cmbLogLevel.setLabelProvider( new LogLevelLabelProvider() );
    cmbLogLevel.setContentProvider( new ArrayContentProvider() );
    cmbLogLevel.setInput( RAPLaunchConfig.LOG_LEVELS );
    cmbLogLevel.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( final SelectionChangedEvent event ) {
        updateLaunchConfigurationDialog();
      }
    } );
    Label lblLibraryVariant = new Label( group, SWT.NONE );
    lblLibraryVariant.setText( "Client-side Library Variant" );
    cmbLibVariant = new ComboViewer( group, SWT.DROP_DOWN | SWT.READ_ONLY );
    cmbLibVariant.setLabelProvider( new LibraryVariantLabelProvider() );
    cmbLibVariant.setContentProvider( new ArrayContentProvider() );
    cmbLibVariant.setInput( LibraryVariant.values() );
    cmbLibVariant.addSelectionChangedListener( new ISelectionChangedListener() {
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
    group.setLayout( new GridLayout( 2, false ) );
    Label lblImage = new Label( group, SWT.NONE );
    lblImage.setLayoutData( new GridData( SWT.TOP, SWT.LEFT, false, false ) );
    lblImage.setImage( warnImage );
    String text 
      = "Please note, that the RAP Application Launcher only works with "
      + "the  Equinox OSGi Framework (this is the default setting on page "
      + "<a>'Bundles'</a>).";
    Link lblText = new Link( group, SWT.WRAP );
    lblText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    lblText.setText( text );
    lblText.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        handleSelectBundlesTab();
      }
    } );
  }

  ////////////////
  // Layout helper
  
  private static GridData spanHorizontal( final int span ) {
    return new GridData( SWT.FILL, SWT.CENTER, true, false, span, SWT.DEFAULT );
  }

  /////////////
  // Validation
  
  private void validate( final RAPLaunchConfig config ) {
    RAPLaunchConfigValidator validator = new RAPLaunchConfigValidator( config );
    IStatus[] states = validator.validate();
    String infoMessage = findMessage( states, IStatus.INFO );
    String warnMessage = findMessage( states, IStatus.WARNING );
    String errorMessage = findMessage( states, IStatus.ERROR );
    if( warnMessage != null ) {
      setMessage( warnMessage );
    } else {
      setMessage( infoMessage );
    }
    setErrorMessage( errorMessage );
  }
  
  private static String findMessage( final IStatus[] states, 
                                     final int severity ) 
  {
    String result = null;
    for( int i = 0; result == null && i < states.length; i++ ) {
      if( states[ i ].matches( severity ) ) {
        result = states[ i ].getMessage();
      }
    }
    return result;
  }
  
  ////////////////
  // Handle events

  private void handleBrowseEntryPoint() {
    EntryPointSelectionDialog dialog 
      = new EntryPointSelectionDialog( getShell() );
    if( dialog.open() == Window.OK ) {
      Object[] selection = dialog.getResult();
      EntryPointExtension entryPoint = ( EntryPointExtension )selection[ 0 ];
      txtEntryPoint.setText( entryPoint.getParameter() );
    }
  }

  private void handleBrowseServletName() {
    ServletNameSelectionDialog dialog 
      = new ServletNameSelectionDialog( getShell() );
    if( dialog.open() == Window.OK ) {
      Object[] selection = dialog.getResult();
      BrandingExtension branding = ( BrandingExtension )selection[ 0 ];
      txtServletName.setText( branding.getServletName() );
      String defaultEntryPointId = branding.getDefaultEntryPointId();
      String parameter = null;
      try {
        EntryPointExtension defaultEntryPoint 
          = EntryPointExtension.findById( defaultEntryPointId );
        if( defaultEntryPoint != null ) {
          parameter = defaultEntryPoint.getParameter();
        }
      } catch( CoreException e ) {
        String msg = "Failed to obtain default entry point from branding";
        ErrorUtil.show( msg, e );
      }
      if( txtEntryPoint.getText().length() == 0 && parameter != null ) {
        txtEntryPoint.setText( parameter );
      }
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

  private void handleSelectBundlesTab() {
    ILaunchConfigurationTab bundlesTab = null;
    ILaunchConfigurationTab[] tabs = getLaunchConfigurationDialog().getTabs();
    for( int i = 0; bundlesTab == null && i < tabs.length; i++ ) {
      if( tabs[ i ] instanceof BundlesTab ) {
        bundlesTab= tabs[ i ];
      }
    }
    if( bundlesTab != null ) {
      getLaunchConfigurationDialog().setActiveTab( bundlesTab );
    }
  }
  
  /////////////////////////////////////////////////////////
  // Helpers to get entered/selected values from UI widgets  

  private BrowserMode getBrowserMode() {
    return   rbExternalBrowser.getSelection() 
           ? BrowserMode.EXTERNAL 
           : BrowserMode.INTERNAL;
  }

  private Level getLogLevel() {
    Level result = Level.OFF;
    ISelection selection = cmbLogLevel.getSelection();
    if( !selection.isEmpty() ) {
      result = ( Level )( ( IStructuredSelection )selection ).getFirstElement();
    }
    return result;
  }
  
  private LibraryVariant getLibraryVariant() {
    LibraryVariant result = LibraryVariant.STANDARD;
    ISelection selection = cmbLibVariant.getSelection();
    if( !selection.isEmpty() ) {
      IStructuredSelection structuredSel = ( IStructuredSelection )selection;
      result = ( LibraryVariant )structuredSel.getFirstElement();
    }
    return result;
  }
  
  ////////////////
  // Inner classes
  
  private static final class LogLevelLabelProvider extends LabelProvider {
    private static final Map lables = new HashMap();
    static {
      lables.put( Level.ALL, "All" );
      lables.put( Level.OFF, "Off" );
      lables.put( Level.CONFIG, "Config" );
      lables.put( Level.WARNING, "Warning" );
      lables.put( Level.SEVERE, "Severe" );
      lables.put( Level.FINE, "Fine" );
      lables.put( Level.FINER, "Finer" );
      lables.put( Level.FINEST, "Finest" );
      lables.put( Level.INFO, "Info" );
    }
    public String getText( final Object element ) {
      String result = ( String )lables.get( element );
      if( result == null ) {
        result = super.getText( element );
      }
      return result;
    }
  }
  
  private static final class LibraryVariantLabelProvider extends LabelProvider {
    public String getText( final Object element ) {
      String result;
      if( LibraryVariant.STANDARD.equals( element ) ) {
        result = "Standard";
      } else if( LibraryVariant.DEBUG.equals( element ) ) {
        result = "Debug";
      } else {
        result = super.getText( element );
      }
      return result;
    }
  }
}
