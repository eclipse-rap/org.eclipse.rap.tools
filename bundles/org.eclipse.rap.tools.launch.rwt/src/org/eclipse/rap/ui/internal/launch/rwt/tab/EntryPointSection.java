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

import org.eclipse.core.resources.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig.LaunchTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;


public class EntryPointSection extends RWTLaunchTab {

  private static final int MARGIN = 20;

  private Button rbEntryPoint;
  private SearchText stEntryPoint;
  private Button rbWebXml;
  private SearchText stWebXmlLocation;

  public String getName() {
    return "Application Entry Point";
  }

  public void createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Application entry point" );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setLayout( new GridLayout( 3, false ) );
    rbEntryPoint = createLaunchTargetRadioButton( group, "Run entry point class" );
    stEntryPoint = new SearchText( group, "Class name:", "Search...", MARGIN );
    stEntryPoint.addModifyListener( new TextModifyListener() );
    stEntryPoint.addSelectionListener( new EntryPointClassSelectionListener() );
    rbWebXml = createLaunchTargetRadioButton( group, "Run from web.xml" );
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
    rbWebXml.setSelection( LaunchTarget.WEB_XML.equals( launchConfig.getLaunchTarget() ) );
    stWebXmlLocation.setText( launchConfig.getWebXmlLocation() );
    stEntryPoint.setText( launchConfig.getEntryPoint() );
    updateEnablement();
  }

  public void performApply( RWTLaunchConfig launchConfig ) {
    if( rbEntryPoint.getSelection() ) {
      launchConfig.setLaunchTarget( LaunchTarget.ENTRY_POINT );
    } else if( rbWebXml.getSelection() ) {
      launchConfig.setLaunchTarget( LaunchTarget.WEB_XML );
    }
    launchConfig.setEntryPoint( stEntryPoint.getText() );
    launchConfig.setWebXmlLocation( stWebXmlLocation.getText() );
  }

  private void updateEnablement() {
    stEntryPoint.setEnabled( rbEntryPoint.getSelection() );
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
    EntryPointTypeSelectionDialog dialog = new EntryPointTypeSelectionDialog( getShell() );
    if( dialog.open() ) {
      stEntryPoint.setText( dialog.getSelection().getFullyQualifiedName() );
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
