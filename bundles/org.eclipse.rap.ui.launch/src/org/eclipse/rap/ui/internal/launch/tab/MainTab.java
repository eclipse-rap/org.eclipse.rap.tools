/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.tab;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.*;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.pde.ui.launcher.AbstractLauncherTab;
import org.eclipse.rap.ui.internal.launch.*;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig.BrowserMode;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig.LibraryVariant;
import org.eclipse.rap.ui.internal.launch.util.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PreferencesUtil;


public final class MainTab extends AbstractLauncherTab {

  private static final String BROWSER_PREFERENCE_PAGE
    = "org.eclipse.ui.browser.preferencePage"; //$NON-NLS-1$

  private final GridDataFactory fillHorizontal;
  private final ModifyListener modifyListener;
  private final SelectionListener selectionListener;
  private final Image tabImage;
  private final Image warnImage;
  private Text servletPathTextField;
  private Text startupParamTextField;
  private Button openBrowserCheckBox;
  private Button internalBrowserRadioButton;
  private Button externalBrowserRadioButton;
  private Text applicationUrlTextField;
  private Button manualPortCheckBox;
  private Button contextPathCheckBox;
  private Text contextPathTextField;
  private Spinner portSpinner;
  private Button useSessionTimeoutCheckBox;
  private Spinner sessionTimeoutSpinner;
  private ComboViewer libraryVariantCombo;
  private ILaunchConfigurationListener launchConfigListener;
  private DataLocationBlock dataLocationBlock;

  public MainTab() {
    tabImage = Images.DESC_MAIN_TAB.createImage();
    warnImage = Images.WARNING.createImage();
    GridData gridData = new GridData( SWT.FILL, SWT.CENTER, true, false );
    fillHorizontal = GridDataFactory.createFrom( gridData );
    modifyListener = createDialogModifyListener();
    selectionListener = createDialogSelectionListener();
    addLaunchConfigListener();
  }

  private ModifyListener createDialogModifyListener() {
     ModifyListener result = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        updateLaunchConfigurationDialog();
      }
    };
    return result;
  }

  private SelectionAdapter createDialogSelectionListener() {
     SelectionAdapter result = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        updateLaunchConfigurationDialog();
      }
    };
    return result;
  }

  ////////////
  // Overrides

  public void dispose() {
    tabImage.dispose();
    warnImage.dispose();
    ILaunchManager launchManager = getLaunchManager();
    launchManager.removeLaunchConfigurationListener( launchConfigListener );
    super.dispose();
  }

  public void createControl( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayout( new GridLayout() );
    createBrowserModeSection( container );
    createRuntimeSettingsSection( container );
    createDataLocationSection( container );
    Dialog.applyDialogFont( container );
    setControl( container );
  }

  public String getName() {
    return LaunchMessages.MainTab_Name;
  }

  public Image getImage() {
    return tabImage;
  }

  public void initializeFrom( ILaunchConfiguration config ) {
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    try {
      servletPathTextField.setText( rapConfig.getServletPath() );
      startupParamTextField.setText( rapConfig.getEntryPoint() );
      manualPortCheckBox.setSelection( rapConfig.getUseManualPort() );
      portSpinner.setSelection( rapConfig.getPort() );
      contextPathCheckBox.setSelection( rapConfig.getUseManualContextPath() );
      contextPathTextField.setText( rapConfig.getContextPath() );
      boolean openBrowser = rapConfig.getOpenBrowser();
      openBrowserCheckBox.setSelection( openBrowser );
      internalBrowserRadioButton.setEnabled( openBrowser );
      externalBrowserRadioButton.setEnabled( openBrowser );
      if( BrowserMode.EXTERNAL.equals( rapConfig.getBrowserMode() ) ) {
        externalBrowserRadioButton.setSelection( true );
        internalBrowserRadioButton.setSelection( false );
      } else {
        externalBrowserRadioButton.setSelection( false );
        internalBrowserRadioButton.setSelection( true );
      }
      useSessionTimeoutCheckBox.setSelection( rapConfig.getUseSessionTimeout() );
      sessionTimeoutSpinner.setSelection( rapConfig.getSessionTimeout() );
      LibraryVariant libVariant = rapConfig.getLibraryVariant();
      StructuredSelection libSelection = new StructuredSelection( libVariant );
      libraryVariantCombo.setSelection( libSelection );
      dataLocationBlock.initializeFrom( rapConfig );
    } catch( CoreException e ) {
      ErrorUtil.show( null, e );
    }
  }

  public void performApply( ILaunchConfigurationWorkingCopy config ) {
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    rapConfig.setServletPath( servletPathTextField.getText() );
    rapConfig.setEntryPoint( startupParamTextField.getText() );
    rapConfig.setOpenBrowser( openBrowserCheckBox.getSelection() );
    rapConfig.setBrowserMode( getBrowserMode() );
    portSpinner.setEnabled( manualPortCheckBox.getSelection() );
    rapConfig.setUseManualPort( manualPortCheckBox.getSelection() );
    rapConfig.setPort( portSpinner.getSelection() );
    contextPathTextField.setEnabled( contextPathCheckBox.getSelection() );
    rapConfig.setUseManualContextPath( contextPathCheckBox.getSelection() );
    rapConfig.setContextPath( contextPathTextField.getText() );
    sessionTimeoutSpinner.setEnabled( useSessionTimeoutCheckBox.getSelection() );
    rapConfig.setUseSessionTimeout( useSessionTimeoutCheckBox.getSelection() );
    rapConfig.setSessionTimeout( sessionTimeoutSpinner.getSelection() );
    rapConfig.setLibraryVariant( getLibraryVariant() );
    rapConfig.setDataLocation( dataLocationBlock.getLocation() );
    boolean useDefaultDataLocation = dataLocationBlock.getUseDefaultDataLocation();
    rapConfig.setUseDefaultDataLocation( useDefaultDataLocation );
    rapConfig.setDoClearDataLocation( dataLocationBlock.getDoClearDataLocation() );
    rapConfig.setAskClearDataLocation( false );
    validate( rapConfig );
    setDirty( true );
  }

  public void setDefaults( ILaunchConfigurationWorkingCopy config ) {
    RAPLaunchConfig.setDefaults( config );
  }

  public boolean isValid( ILaunchConfiguration launchConfig ) {
    return getErrorMessage() == null;
  }

  public void validateTab() {
    // We validate on performApply and launcher changes. No need to validate here.
  }

  ///////////////////////////////////
  // Helping methods to create the UI

  private void addLaunchConfigListener() {
    launchConfigListener = getLaunchConfigListener();
    ILaunchManager launchManager = getLaunchManager();
    launchManager.addLaunchConfigurationListener( launchConfigListener );
  }

  private ILaunchConfigurationListener getLaunchConfigListener() {
    ILaunchConfigurationListener result = new ILaunchConfigurationListener() {
      public void launchConfigurationChanged( ILaunchConfiguration configuration ) {
        RAPLaunchConfig rapConfig = new RAPLaunchConfig( configuration );
        validate( rapConfig );
        updateApplicationUrl( rapConfig );
      }

      public void launchConfigurationAdded( ILaunchConfiguration configuration ) {
        // Do nothing
      }

      public void launchConfigurationRemoved( ILaunchConfiguration configuration ) {
        // Do nothing
      }
    };
    return result;
  }

  private void createDataLocationSection( Composite container ) {
    dataLocationBlock = new DataLocationBlock( this );
    Control blockControl = dataLocationBlock.createControl( container );
    blockControl.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
  }

  private void createServletPathPart( Composite parent ) {
    Label lblServletName = new Label( parent, SWT.NONE );
    lblServletName.setText( LaunchMessages.MainTab_ServletPath );
    servletPathTextField = new Text( parent, SWT.BORDER );
    servletPathTextField.setLayoutData( spanHorizontal( 2, 0 ) );
    servletPathTextField.addModifyListener( modifyListener );
  }

  private void createStartupParamPart( Composite parent ) {
    Label lblEntryPoint = new Label( parent, SWT.NONE );
    lblEntryPoint.setText( LaunchMessages.MainTab_StartupParam );
    startupParamTextField = new Text( parent, SWT.BORDER );
    startupParamTextField.setLayoutData( spanHorizontal( 2, 0 ) );
    startupParamTextField.addModifyListener( modifyListener );
    createStartupParameterDecorator();
  }

  private void createBrowserModeSection( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( LaunchMessages.MainTab_Browser );
    group.setLayout( new GridLayout( 3, false ) );
    createBrowserActivationPart( group );
    createBrowserModePart( group );
    createServletPathPart( group );
    createStartupParamPart( group );
    createApplicationUrlPart( group );
  }

  private void createBrowserActivationPart( Composite parent ) {
    openBrowserCheckBox = new Button( parent, SWT.CHECK );
    GridDataFactory.swtDefaults().grab( true, false ).span( 2, 1 ).applyTo( openBrowserCheckBox );
    openBrowserCheckBox.setText( LaunchMessages.MainTab_OpenApplicationIn );
    openBrowserCheckBox.addSelectionListener( selectionListener );
    Link lnkBrowserPrefs = new Link( parent, SWT.NONE );
    lnkBrowserPrefs.setText( LaunchMessages.MainTab_ConfigureBrowsers );
    lnkBrowserPrefs.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        handleBrowserPrefsLink();
      }
    } );
  }

  private void createBrowserModePart( Composite parent ) {
    internalBrowserRadioButton = new Button( parent, SWT.RADIO );
    GridDataFactory radioBtnData = GridDataFactory.fillDefaults().indent( 17, 0 );
    internalBrowserRadioButton.setLayoutData( radioBtnData.create() );
    internalBrowserRadioButton.setText( LaunchMessages.MainTab_InternalBrowser );
    internalBrowserRadioButton.addSelectionListener( selectionListener );
    externalBrowserRadioButton = new Button( parent, SWT.RADIO );
    externalBrowserRadioButton.setLayoutData( spanHorizontal( 2, 17 ) );
    externalBrowserRadioButton.setText( LaunchMessages.MainTab_ExternalBrowser );
    externalBrowserRadioButton.addSelectionListener( selectionListener );
    openBrowserCheckBox.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        boolean openBrowser = openBrowserCheckBox.getSelection();
        internalBrowserRadioButton.setEnabled( openBrowser );
        externalBrowserRadioButton.setEnabled( openBrowser );
        servletPathTextField.setEnabled( openBrowser );
        startupParamTextField.setEnabled( openBrowser );
      }
    } );
  }

  private void createApplicationUrlPart( Composite parent ) {
    Composite urlComposite = new Composite( parent, SWT.NONE );
    Label finalUrlLabel = new Label( urlComposite, SWT.NONE );
    GridDataFactory.fillDefaults().grab( true, false ).span( 3, 1 ).applyTo( urlComposite );
    GridLayoutFactory.fillDefaults().numColumns( 3 ).generateLayout( urlComposite );
    finalUrlLabel.setText( LaunchMessages.MainTab_ApplicationUrl );
    // Create a text area which is read-only but not disabled to allow the user to select the text
    // and copy it, set the background to make it clear to the user this is not a place to edit
    applicationUrlTextField = new Text( urlComposite, SWT.SINGLE | SWT.READ_ONLY );
    applicationUrlTextField.setBackground( applicationUrlTextField.getParent().getBackground() );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( applicationUrlTextField );
  }

  private void createRuntimeSettingsSection( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( LaunchMessages.MainTab_RuntimeSettings );
    group.setLayout( new GridLayout( 2, true ) );
    Composite leftPart = new Composite( group, SWT.NONE );
    leftPart.setLayout( createGridLayoutWithoutMargin( 2 ) );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( leftPart );
    createRuntimeSettingsLeftPart( leftPart );
    Composite rightPart = new Composite( group, SWT.NONE );
    rightPart.setLayout( createGridLayoutWithoutMargin( 2 ) );
    GridDataFactory.fillDefaults().grab( true, false ).indent( 15, 0 ).applyTo( rightPart );
    createRuntimeSettingsRightPart( rightPart );
  }

  private GridLayout createGridLayoutWithoutMargin( int numColumns ) {
    GridLayout result = new GridLayout( numColumns, false );
    result.marginHeight = 0;
    result.marginWidth = 0;
    return result;
  }

  private void createRuntimeSettingsLeftPart( Composite leftPartComposite ) {
    manualPortCheckBox = new Button( leftPartComposite, SWT.CHECK );
    manualPortCheckBox.setText( LaunchMessages.MainTab_ManualPortConfig );
    manualPortCheckBox.addSelectionListener( selectionListener );
    portSpinner = new Spinner( leftPartComposite, SWT.BORDER );
    portSpinner.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    portSpinner.setMinimum( RAPLaunchConfig.MIN_PORT_NUMBER );
    portSpinner.setMaximum( RAPLaunchConfig.MAX_PORT_NUMBER );
    portSpinner.addModifyListener( modifyListener );
    useSessionTimeoutCheckBox = new Button( leftPartComposite, SWT.CHECK );
    useSessionTimeoutCheckBox.setText( LaunchMessages.MainTab_ManualTimeoutConfig );
    useSessionTimeoutCheckBox.addSelectionListener( selectionListener );
    sessionTimeoutSpinner = new Spinner( leftPartComposite, SWT.BORDER );
    sessionTimeoutSpinner.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    sessionTimeoutSpinner.setMinimum( RAPLaunchConfig.MIN_SESSION_TIMEOUT );
    sessionTimeoutSpinner.setMaximum( RAPLaunchConfig.MAX_SESSION_TIMEOUT );
    sessionTimeoutSpinner.addModifyListener( modifyListener );
    contextPathCheckBox = new Button( leftPartComposite, SWT.CHECK );
    contextPathCheckBox.setText( LaunchMessages.MainTab_ManualContextPath );
    contextPathCheckBox.addSelectionListener( selectionListener );
    contextPathTextField = new Text( leftPartComposite, SWT.BORDER | SWT.SINGLE );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( contextPathTextField );
    contextPathTextField.addModifyListener( modifyListener );
  }

  private void createRuntimeSettingsRightPart( Composite righttPartComposite ) {
    Label libraryVariantLabel = new Label( righttPartComposite, SWT.NONE );
    libraryVariantLabel.setText( LaunchMessages.MainTab_ClientLibraryVariant );
    libraryVariantCombo = new ComboViewer( righttPartComposite, SWT.DROP_DOWN | SWT.READ_ONLY );
    libraryVariantCombo.getCombo().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    libraryVariantCombo.setLabelProvider( new LibraryVariantLabelProvider() );
    libraryVariantCombo.setContentProvider( new ArrayContentProvider() );
    libraryVariantCombo.setInput( LibraryVariant.values() );
    libraryVariantCombo.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( SelectionChangedEvent event ) {
        updateLaunchConfigurationDialog();
      }
    } );
  }

  private void createStartupParameterDecorator() {
    final ControlDecoration decorator = new ControlDecoration( startupParamTextField, SWT.LEFT );
    FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
    FieldDecoration warningDecoration
      = registry.getFieldDecoration( FieldDecorationRegistry.DEC_WARNING );
    decorator.setImage( warningDecoration.getImage() );
    decorator.setShowHover( true );
    decorator.setDescriptionText( LaunchMessages.MainTab_StartupParamWarningMsg );
    decorator.setMarginWidth( 5 );
    updateStartupDecorator( decorator );
    startupParamTextField.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        updateStartupDecorator( decorator );
      }
    } );
  }

  private void updateStartupDecorator( ControlDecoration decorator ) {
    if( startupParamTextField.getText().length() == 0 ) {
      decorator.hide();
    } else {
      decorator.show();
    }
  }

  ////////////////
  // Layout helper

  private static GridData spanHorizontal( int span, int indent ) {
    GridData result = new GridData( SWT.FILL, SWT.CENTER, true, false, span, SWT.DEFAULT );
    result.horizontalIndent = indent;
    return result;
  }

  /////////////
  // Validation

  private void validate( RAPLaunchConfig config ) {
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

  private void updateApplicationUrl( RAPLaunchConfig config ) {
    String applicationUrl;
    try {
      applicationUrl = getApplicationUrl( config );
    } catch( Exception e ) {
      setErrorMessage( e.getMessage() );
      applicationUrl = "";
    }
    udpateApplicationUrlTextField( applicationUrl );
  }

  private String getApplicationUrl( RAPLaunchConfig config ) throws CoreException {
    String result;
    if( config.getUseManualPort() ) {
      int port = config.getPort();
      result = URLBuilder.fromLaunchConfig( config, port, false );
    } else {
      result = URLBuilder.fromLaunchConfig( config, "<PORT>", false );
    }
    return result;
  }

  private void udpateApplicationUrlTextField( final String finalApplicationUrl ) {
    if( applicationUrlTextField != null && !applicationUrlTextField.isDisposed() ) {
      // could be called from a non UI thread
      applicationUrlTextField.getDisplay().syncExec( new Runnable() {
        public void run() {
          if( !finalApplicationUrl.equals( applicationUrlTextField.getText() ) ) {
            applicationUrlTextField.setText( finalApplicationUrl );
          }
        }
      } );
    }
  }

  private static String findMessage( IStatus[] states, int severity ) {
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

  private void handleBrowserPrefsLink() {
    PreferenceDialog dialog
      = PreferencesUtil.createPreferenceDialogOn( getShell(), BROWSER_PREFERENCE_PAGE, null, null );
    dialog.open();
    dialog.close();
  }

  /////////////////////////////////////////////////////////
  // Helpers to get entered/selected values from UI widgets

  private BrowserMode getBrowserMode() {
    boolean selection = externalBrowserRadioButton.getSelection();
    return selection ? BrowserMode.EXTERNAL : BrowserMode.INTERNAL;
  }

  private LibraryVariant getLibraryVariant() {
    LibraryVariant result = LibraryVariant.STANDARD;
    ISelection selection = libraryVariantCombo.getSelection();
    if( !selection.isEmpty() ) {
      IStructuredSelection structuredSel = ( IStructuredSelection )selection;
      result = ( LibraryVariant )structuredSel.getFirstElement();
    }
    return result;
  }

  ////////////////
  // Inner classes

  private static final class LibraryVariantLabelProvider extends LabelProvider {
    public String getText( Object element ) {
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
