/*******************************************************************************
 * Copyright (c) 2011, 2014 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.tab;

import org.eclipse.core.resources.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;


public class ApplicationSection extends RWTLaunchTab {

  private static final int MARGIN = 20;

  private Button rbEntryPoint;
  private SearchText stEntryPoint;
  private Button rbAppConfig;
  private SearchText stAppConfig;
  private Button rbWebXml;
  private SearchText stWebXmlLocation;

  public String getName() {
    return "RWT Application";
  }

  public void createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Application" );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setLayout( new GridLayout( 3, false ) );
    rbEntryPoint = createLaunchTargetRadioButton( group, "Run &entry point" );
    stEntryPoint = new SearchText( group, "Class name:", "Search...", MARGIN );
    stEntryPoint.addModifyListener( new TextModifyListener() );
    stEntryPoint.addSelectionListener( new EntryPointClassSelectionListener() );
    rbAppConfig = createLaunchTargetRadioButton( group, "Run &application configuration" );
    stAppConfig = new SearchText( group, "Class name:", "Search...", MARGIN );
    stAppConfig.addModifyListener( new TextModifyListener() );
    stAppConfig.addSelectionListener( new AppConfigClassSelectionListener() );
    rbWebXml = createLaunchTargetRadioButton( group, "Run from &web.xml" );
    stWebXmlLocation = new SearchText( group, "Location:", "Search...", MARGIN );
    stWebXmlLocation.addModifyListener( new TextModifyListener() );
    stWebXmlLocation.addSelectionListener( new WebXmlSelectionListener() );
    Dialog.applyDialogFont( group );
    setControl( group );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
    updateEnablement();
  }

  public void initializeFrom( RWTLaunchConfig launchConfig ) {
    rbEntryPoint.setSelection( LaunchTarget.ENTRY_POINT.equals( launchConfig.getLaunchTarget() ) );
    rbAppConfig.setSelection( LaunchTarget.APP_CONFIG.equals( launchConfig.getLaunchTarget() ) );
    rbWebXml.setSelection( LaunchTarget.WEB_XML.equals( launchConfig.getLaunchTarget() ) );
    stEntryPoint.setText( launchConfig.getEntryPoint() );
    stAppConfig.setText( launchConfig.getAppConfig() );
    stWebXmlLocation.setText( launchConfig.getWebXmlLocation() );
    updateEnablement();
  }

  public void performApply( RWTLaunchConfig launchConfig ) {
    if( rbEntryPoint.getSelection() ) {
      launchConfig.setLaunchTarget( LaunchTarget.ENTRY_POINT );
    } else if( rbAppConfig.getSelection() ) {
      launchConfig.setLaunchTarget( LaunchTarget.APP_CONFIG );
    } else if( rbWebXml.getSelection() ) {
      launchConfig.setLaunchTarget( LaunchTarget.WEB_XML );
    }
    launchConfig.setEntryPoint( stEntryPoint.getText() );
    launchConfig.setAppConfig( stAppConfig.getText() );
    launchConfig.setWebXmlLocation( stWebXmlLocation.getText() );
  }

  private void updateEnablement() {
    stEntryPoint.setEnabled( rbEntryPoint.getSelection() );
    stAppConfig.setEnabled( rbAppConfig.getSelection() );
    stWebXmlLocation.setEnabled( rbWebXml.getSelection() );
  }

  private void selectWebXml() {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    int file = IResource.FILE;
    SelectionDialog dialog = new FilteredResourcesSelectionDialog( getShell(), false, root, file );
    dialog.setTitle( "Web.xml Selection" );
    if( dialog.open() == Window.OK && dialog.getResult().length > 0 ) {
      IResource selection = ( IResource )dialog.getResult()[ 0 ];
      stWebXmlLocation.setText( selection.getFullPath().toPortableString() );
    }
  }

  private void selectEntryPointClass() {
    ApplicationTypeSelectionDialog dialog = new ApplicationTypeSelectionDialog( getShell() );
    if( dialog.open() ) {
      stEntryPoint.setText( dialog.getSelection().getFullyQualifiedName() );
    }
  }

  private void selectAppConfigClass() {
    ApplicationTypeSelectionDialog dialog = new ApplicationTypeSelectionDialog( getShell() );
    if( dialog.open() ) {
      stAppConfig.setText( dialog.getSelection().getFullyQualifiedName() );
    }
  }

  private Button createLaunchTargetRadioButton( Composite parent, String label ) {
    Button result = createRadioButton( parent, label );
    result.setLayoutData( newGridData( 3, 0 ) );
    result.addSelectionListener( new LaunchTargetSelectionListener() );
    return result;
  }

  private static GridData newGridData( int horizontalSpan, int horizontalIndent ) {
    GridData result = new GridData();
    result.horizontalSpan = horizontalSpan;
    result.horizontalIndent = horizontalIndent;
    return result;
  }

  private class LaunchTargetSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      updateEnablement();
      updateLaunchConfigurationDialog();
    }
  }

  private class EntryPointClassSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      selectEntryPointClass();
      updateLaunchConfigurationDialog();
    }
  }

  private class AppConfigClassSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      selectAppConfigClass();
      updateLaunchConfigurationDialog();
    }
  }

  private class WebXmlSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      selectWebXml();
      updateLaunchConfigurationDialog();
    }
  }

  private class TextModifyListener implements ModifyListener {
    public void modifyText( ModifyEvent event ) {
      updateLaunchConfigurationDialog();
    }
  }
}
