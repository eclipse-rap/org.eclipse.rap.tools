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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.pde.ui.launcher.AbstractLauncherTab;
import org.eclipse.rap.ui.internal.launch.*;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig.BrowserMode;
import org.eclipse.rap.ui.internal.launch.util.ErrorUtil;
import org.eclipse.rap.ui.internal.launch.util.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
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
  private Button openBrowserCheckBox;
  private Button internalBrowserRadioButton;
  private Button externalBrowserRadioButton;
  private Text applicationUrlTextField;
  private Button useFixedPortCheckBox;
  private Button contextPathCheckBox;
  private Text contextPathTextField;
  private Spinner portSpinner;
  private Button useSessionTimeoutCheckBox;
  private Spinner sessionTimeoutSpinner;
  private Button developmentModeCheckBox;
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
      @Override
      public void widgetSelected( SelectionEvent e ) {
        updateLaunchConfigurationDialog();
      }
    };
    return result;
  }

  ////////////
  // Overrides

  @Override
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
    createRAPSettingsSection( container );
    createDataLocationSection( container );
    Dialog.applyDialogFont( container );
    setControl( container );
  }

  public String getName() {
    return LaunchMessages.MainTab_Name;
  }

  @Override
  public Image getImage() {
    return tabImage;
  }

  public void initializeFrom( ILaunchConfiguration config ) {
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    try {
      servletPathTextField.setText( rapConfig.getServletPath() );
      useFixedPortCheckBox.setSelection( rapConfig.getUseManualPort() );
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
      developmentModeCheckBox.setSelection( rapConfig.getDevelopmentMode() );
      dataLocationBlock.initializeFrom( rapConfig );
    } catch( CoreException e ) {
      ErrorUtil.show( null, e );
    }
  }

  public void performApply( ILaunchConfigurationWorkingCopy config ) {
    RAPLaunchConfig rapConfig = new RAPLaunchConfig( config );
    rapConfig.setServletPath( servletPathTextField.getText() );
    rapConfig.setOpenBrowser( openBrowserCheckBox.getSelection() );
    rapConfig.setBrowserMode( getBrowserMode() );
    portSpinner.setEnabled( useFixedPortCheckBox.getSelection() );
    rapConfig.setUseManualPort( useFixedPortCheckBox.getSelection() );
    rapConfig.setPort( portSpinner.getSelection() );
    contextPathTextField.setEnabled( contextPathCheckBox.getSelection() );
    rapConfig.setUseManualContextPath( contextPathCheckBox.getSelection() );
    rapConfig.setContextPath( contextPathTextField.getText() );
    sessionTimeoutSpinner.setEnabled( useSessionTimeoutCheckBox.getSelection() );
    rapConfig.setUseSessionTimeout( useSessionTimeoutCheckBox.getSelection() );
    rapConfig.setSessionTimeout( sessionTimeoutSpinner.getSelection() );
    rapConfig.setDevelopmentMode( developmentModeCheckBox.getSelection() );
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

  @Override
  public boolean isValid( ILaunchConfiguration launchConfig ) {
    return getErrorMessage() == null;
  }

  @Override
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

  private void createBrowserModeSection( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( LaunchMessages.MainTab_Browser );
    group.setLayout( new GridLayout() );
    createBrowserActivationPart( group );
    createServletPathPart( group );
  }

  private void createBrowserActivationPart( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayoutData( fillHorizontal.span( 2, 1 ).create() );
    GridLayout layout = new GridLayout( 2, false );
    layout.horizontalSpacing = 10;
    layout.verticalSpacing = 0;
    composite.setLayout( layout );
    openBrowserCheckBox = new Button( composite, SWT.CHECK );
    openBrowserCheckBox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    openBrowserCheckBox.setText( LaunchMessages.MainTab_OpenApplicationIn );
    Link browserPrefsLink = createBrowserPrefsLink( composite );
    browserPrefsLink.setLayoutData( new GridData( SWT.END, SWT.CENTER, false, false ) );
    Composite modePart = createBrowserModePart( composite );
    modePart.setLayoutData( GridDataFactory.fillDefaults().span( 2, 1 ).indent( 17, 0 ).create() );
    addSelectionListeners();
  }

  private Link createBrowserPrefsLink( Composite composite ) {
    Link link = new Link( composite, SWT.NONE );
    link.setText( LaunchMessages.MainTab_ConfigureBrowsers );
    link.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        openBrowserPrefs();
      }
    } );
    return link;
  }

  private Composite createBrowserModePart( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    internalBrowserRadioButton = new Button( composite, SWT.RADIO );
    internalBrowserRadioButton.setText( LaunchMessages.MainTab_InternalBrowser );
    internalBrowserRadioButton.addSelectionListener( selectionListener );
    externalBrowserRadioButton = new Button( composite, SWT.RADIO );
    externalBrowserRadioButton.setText( LaunchMessages.MainTab_ExternalBrowser );
    externalBrowserRadioButton.addSelectionListener( selectionListener );
    return composite;
  }

  private void addSelectionListeners() {
    openBrowserCheckBox.addSelectionListener( selectionListener );
    openBrowserCheckBox.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        boolean openBrowser = openBrowserCheckBox.getSelection();
        internalBrowserRadioButton.setEnabled( openBrowser );
        externalBrowserRadioButton.setEnabled( openBrowser );
        servletPathTextField.setEnabled( openBrowser );
      }
    } );
  }

  private void createServletPathPart( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayoutData( fillHorizontal.create() );
    composite.setLayout( new GridLayout( 2, false ) );
    Label lblServletPath = new Label( composite, SWT.NONE );
    lblServletPath.setText( LaunchMessages.MainTab_ServletPath );
    servletPathTextField = new Text( composite, SWT.BORDER );
    servletPathTextField.addModifyListener( modifyListener );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( servletPathTextField );
    Label finalUrlLabel = new Label( composite, SWT.NONE );
    finalUrlLabel.setText( LaunchMessages.MainTab_ApplicationUrl );
    // Create a text area which is read-only but not disabled to allow the user to select the text
    // and copy it, set the background to make it clear to the user this is not a place to edit
    applicationUrlTextField = new Text( composite, SWT.SINGLE | SWT.READ_ONLY );
    applicationUrlTextField.setBackground( applicationUrlTextField.getParent().getBackground() );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( applicationUrlTextField );
  }

  private void createRuntimeSettingsSection( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( LaunchMessages.MainTab_ServerSettings );
    group.setLayout( new GridLayout( 2, false ) );
    useFixedPortCheckBox = new Button( group, SWT.CHECK );
    useFixedPortCheckBox.setText( LaunchMessages.MainTab_ManualPortConfig );
    useFixedPortCheckBox.addSelectionListener( selectionListener );
    portSpinner = new Spinner( group, SWT.BORDER );
    portSpinner.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    portSpinner.setMinimum( RAPLaunchConfig.MIN_PORT_NUMBER );
    portSpinner.setMaximum( RAPLaunchConfig.MAX_PORT_NUMBER );
    portSpinner.addModifyListener( modifyListener );
    useSessionTimeoutCheckBox = new Button( group, SWT.CHECK );
    useSessionTimeoutCheckBox.setText( LaunchMessages.MainTab_ManualTimeoutConfig );
    useSessionTimeoutCheckBox.addSelectionListener( selectionListener );
    sessionTimeoutSpinner = new Spinner( group, SWT.BORDER );
    sessionTimeoutSpinner.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    sessionTimeoutSpinner.setMinimum( RAPLaunchConfig.MIN_SESSION_TIMEOUT );
    sessionTimeoutSpinner.setMaximum( RAPLaunchConfig.MAX_SESSION_TIMEOUT );
    sessionTimeoutSpinner.addModifyListener( modifyListener );
    contextPathCheckBox = new Button( group, SWT.CHECK );
    contextPathCheckBox.setText( LaunchMessages.MainTab_ManualContextPath );
    contextPathCheckBox.addSelectionListener( selectionListener );
    contextPathTextField = new Text( group, SWT.BORDER | SWT.SINGLE );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( contextPathTextField );
    contextPathTextField.addModifyListener( modifyListener );
  }

  private void createRAPSettingsSection( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( fillHorizontal.create() );
    group.setText( LaunchMessages.MainTab_RAPSettings );
    group.setLayout( new GridLayout( 2, true ) );
    Composite leftPart = new Composite( group, SWT.NONE );
    leftPart.setLayout( createGridLayoutWithoutMargin( 1 ) );
    GridDataFactory.fillDefaults().grab( true, false ).applyTo( leftPart );
    createRAPSettingsLeftPart( leftPart );
    Composite rightPart = new Composite( group, SWT.NONE );
    rightPart.setLayout( createGridLayoutWithoutMargin( 2 ) );
    GridDataFactory.fillDefaults().grab( true, false ).indent( 15, 0 ).applyTo( rightPart );
  }

  private GridLayout createGridLayoutWithoutMargin( int numColumns ) {
    GridLayout result = new GridLayout( numColumns, false );
    result.marginHeight = 0;
    result.marginWidth = 0;
    return result;
  }

  private void createRAPSettingsLeftPart( Composite leftPartComposite ) {
    developmentModeCheckBox = new Button( leftPartComposite, SWT.CHECK );
    developmentModeCheckBox.setText( LaunchMessages.MainTab_DevelopmentMode );
    developmentModeCheckBox.addSelectionListener( selectionListener );
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

  private void openBrowserPrefs() {
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

}
