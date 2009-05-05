/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.ui.internal.launch.*;
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
  private Button cbOpenBrowser;
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
    // Set container for this tab page
    Dialog.applyDialogFont( container );
    setControl( container );
  }

  public String getName() {
    return LaunchMessages.MainTab_Name;
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
      // OpenBrowser
      boolean openBrowser = rapConfig.getOpenBrowser();
      cbOpenBrowser.setSelection( openBrowser );
      rbInternalBrowser.setEnabled( openBrowser );
      rbExternalBrowser.setEnabled( openBrowser );
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
    // OpenBrowser
    rapConfig.setOpenBrowser( cbOpenBrowser.getSelection() );
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
    group.setText( LaunchMessages.MainTab_ServletAndEntryPoint );
    Label lblServletName = new Label( group, SWT.NONE );
    lblServletName.setText( LaunchMessages.MainTab_ServletName );
    txtServletName = new Text( group, SWT.BORDER );
    txtServletName.setLayoutData( fillHorizontal.create() );
    txtServletName.addModifyListener( modifyListener );
    Button btnBrowseServletName = new Button( group, SWT.PUSH );
    btnBrowseServletName.setText( LaunchMessages.MainTab_BrowseServletName );
    btnBrowseServletName.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        handleBrowseServletName();
      }
    } );
    Label lblEntryPoint = new Label( group, SWT.NONE );
    lblEntryPoint.setText( LaunchMessages.MainTab_EntryPoint );
    txtEntryPoint = new Text( group, SWT.BORDER );
    txtEntryPoint.setLayoutData( fillHorizontal.create() );
    txtEntryPoint.addModifyListener( modifyListener );
    Button btnBrowseEntryPoint = new Button( group, SWT.PUSH );
    btnBrowseEntryPoint.setText( LaunchMessages.MainTab_BrowseEntryPoint );
    btnBrowseEntryPoint.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        handleBrowseEntryPoint();
      }
    } );
    cbTerminatePrevious = new Button( group, SWT.CHECK );
    cbTerminatePrevious.setLayoutData( spanHorizontal( 3, 0 ) );
    String text = LaunchMessages.MainTab_TerminatePrevious;
    cbTerminatePrevious.setText( text );
    cbTerminatePrevious.addSelectionListener( selectionListener );
  }
  
  private void createBrowserModeSection( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( LaunchMessages.MainTab_Browser );
    group.setLayout( new GridLayout( 2, false ) );
    cbOpenBrowser = new Button( group, SWT.CHECK );
    GridDataFactory grab = GridDataFactory.swtDefaults();
    grab.grab( true, false );
    cbOpenBrowser.setLayoutData( grab.create() );
    cbOpenBrowser.setText( LaunchMessages.MainTab_OpenApplicationIn );
    cbOpenBrowser.addSelectionListener( selectionListener );
    Link lnkBrowserPrefs = new Link( group, SWT.NONE );
    lnkBrowserPrefs.setText( LaunchMessages.MainTab_ConfigureBrowsers );
    lnkBrowserPrefs.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        handleBrowserPrefsLink();
      }
    } );
    rbInternalBrowser = new Button( group, SWT.RADIO );
    rbInternalBrowser.setLayoutData( spanHorizontal( 2, 17 ) );
    rbInternalBrowser.setText( LaunchMessages.MainTab_InternalBrowser );
    rbInternalBrowser.addSelectionListener( selectionListener );
    rbExternalBrowser = new Button( group, SWT.RADIO );
    rbExternalBrowser.setLayoutData( spanHorizontal( 2, 17 ) );
    rbExternalBrowser.setText( LaunchMessages.MainTab_ExternalBrowser );
    rbExternalBrowser.addSelectionListener( selectionListener );
    cbOpenBrowser.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        boolean openBrowser = cbOpenBrowser.getSelection();
        rbInternalBrowser.setEnabled( openBrowser );
        rbExternalBrowser.setEnabled( openBrowser );
      }
    } );
  }
  
  private void createRuntimeSettingsSection( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( LaunchMessages.MainTab_RuntimeSettings );
    group.setLayout( new GridLayout( 2, false ) );
    cbManualPort = new Button( group, SWT.CHECK );
    cbManualPort.setText( LaunchMessages.MainTab_ManualPortConfig );
    cbManualPort.addSelectionListener( selectionListener );
    spnPort = new Spinner( group, SWT.BORDER );
    spnPort.setLayoutData( new GridData( 60, SWT.DEFAULT ) );
    spnPort.setMinimum( RAPLaunchConfig.MIN_PORT_NUMBER );
    spnPort.setMaximum( RAPLaunchConfig.MAX_PORT_NUMBER );
    spnPort.addModifyListener( modifyListener );
    Label lblLogLevel = new Label( group, SWT.NONE );
    lblLogLevel.setText( LaunchMessages.MainTab_ClientLogLevel );
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
    lblLibraryVariant.setText( LaunchMessages.MainTab_ClientLibraryVariant );
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

  ////////////////
  // Layout helper
  
  private static GridData spanHorizontal( final int span, int indent ) {
    GridData result
      = new GridData( SWT.FILL, SWT.CENTER, true, false, span, SWT.DEFAULT );
    result.horizontalIndent = indent;
    return result;
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
        String msg
          = LaunchMessages.MainTab_ObtainDefaultEntryPointForBrandingFailed;
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
    dialog.close();
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
      lables.put( Level.ALL, LaunchMessages.MainTab_LogLevelAll );
      lables.put( Level.OFF, LaunchMessages.MainTab_LogLevelOff );
      lables.put( Level.CONFIG, LaunchMessages.MainTab_LogLevelConfig );
      lables.put( Level.WARNING, LaunchMessages.MainTab_LogLevelWarning );
      lables.put( Level.SEVERE, LaunchMessages.MainTab_LogLevelSevere );
      lables.put( Level.FINE, LaunchMessages.MainTab_LogLevelFine );
      lables.put( Level.FINER, LaunchMessages.MainTab_LogLevelFiner );
      lables.put( Level.FINEST, LaunchMessages.MainTab_LogLevelFinest );
      lables.put( Level.INFO, LaunchMessages.MainTab_LogLevelInfo );
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
        result = LaunchMessages.MainTab_LibraryVariantStandard;
      } else if( LibraryVariant.DEBUG.equals( element ) ) {
        result = LaunchMessages.MainTab_LibraryVariantDebug;
      } else {
        result = super.getText( element );
      }
      return result;
    }
  }
}
