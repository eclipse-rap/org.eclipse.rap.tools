/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import org.eclipse.core.resources.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;


public class EntryPointSection extends RWTLaunchTab {
  
  private static final int MARGIN = 20;
  
  private Button rbWebXml;
  private Button rbEntryPoint;
  private Label lblWebXmlLocation;
  private Text txtWebXmlLocation;
  private Label lblServletName;
  private Text txtServletName;
  private Button btnSelectWebXml;
  private Label lblEntryPointClass;
  private Text txtEntryPointClass;
  private Button btnSelectEntryPointClass;
  
  public String getName() {
    return "Application entry eoint";
  }
  
  public void createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Application entry point" );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setLayout( new GridLayout( 3, false ) );
    rbEntryPoint = createRadioButton( group, "Run entry point class" );
    rbEntryPoint.setLayoutData( newGridData( 3, 0 ) );
    rbEntryPoint.addSelectionListener( new EntryPointSelectionListener() );
    lblEntryPointClass = createLabel( group, "Class name" );
    lblEntryPointClass.setLayoutData( newGridData( 0, MARGIN ) );
    txtEntryPointClass = new Text( group, SWT.BORDER );
    txtEntryPointClass.setLayoutData( newFillHorizontalGridData() );
    txtEntryPointClass.addModifyListener( new TextModifyListener() );
    btnSelectEntryPointClass = createPushButton( group, "Search...", null );
    SelectionListener listener = new EntryPointClassSelectionListener();
    btnSelectEntryPointClass.addSelectionListener( listener );
    rbWebXml = createRadioButton( group, "Run from web.xml" );
    rbWebXml.setLayoutData( newGridData( 3, 0 ) );
    rbWebXml.addSelectionListener( new EntryPointSelectionListener() );
    rbWebXml.setSelection( true );
    lblWebXmlLocation = createLabel( group, "Location" );
    lblWebXmlLocation.setLayoutData( newGridData( 0, MARGIN ) );
    txtWebXmlLocation = new Text( group, SWT.BORDER );
    txtWebXmlLocation.setLayoutData( newFillHorizontalGridData() );
    txtWebXmlLocation.addModifyListener( new TextModifyListener() );
    btnSelectWebXml = createPushButton( group, "Search...", null );
    btnSelectWebXml.addSelectionListener( new WebXmlSelectionListener() );
    lblServletName = createLabel( group, "Servlet path" );
    lblServletName.setLayoutData( newGridData( 0, MARGIN ) );
    txtServletName = new Text( group, SWT.BORDER );
    txtServletName.setLayoutData( newFillHorizontalGridData() );
    txtServletName.addModifyListener( new ComboModifyListener() );
    Dialog.applyDialogFont( group );
    setControl( group );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
    updateEnablement();
  }
  
  public void initializeFrom( RWTLaunchConfig launchConfig ) {
    rbWebXml.setSelection( launchConfig.getUseWebXml() );
    rbEntryPoint.setSelection( !launchConfig.getUseWebXml() );
    txtWebXmlLocation.setText( launchConfig.getWebXmlLocation() );
    txtServletName.setText( launchConfig.getServletPath() );
    txtEntryPointClass.setText( launchConfig.getEntryPoint() );
    updateEnablement();
  }
  
  public void performApply( RWTLaunchConfig launchConfig ) {
    launchConfig.setUseWebXml( rbWebXml.getSelection() );
    launchConfig.setWebXmlLocation( txtWebXmlLocation.getText().trim() );
    launchConfig.setServletPath( txtServletName.getText().trim() );
    launchConfig.setEntryPoint( txtEntryPointClass.getText().trim() );
  }

  private void updateEnablement() {
    boolean enabled = rbWebXml.getSelection();
    lblWebXmlLocation.setEnabled( enabled );
    txtWebXmlLocation.setEnabled( enabled );
    btnSelectWebXml.setEnabled( enabled );
    lblServletName.setEnabled( enabled );
    txtServletName.setEnabled( enabled );
    enabled = rbEntryPoint.getSelection();
    lblEntryPointClass.setEnabled( enabled );
    txtEntryPointClass.setEnabled( enabled );
    btnSelectEntryPointClass.setEnabled( enabled );
  }
  
  private void selectWebXml() {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    int file = IResource.FILE;
    FilteredResourcesSelectionDialog dialog 
      = new FilteredResourcesSelectionDialog( getShell(), false, root, file );
    dialog.setTitle( "Web.xml Selection" );
    if( dialog.open() == Window.OK && dialog.getResult().length > 0 ) {
      IResource selection = ( IResource )dialog.getResult()[ 0 ];
      txtWebXmlLocation.setText( selection.getFullPath().toPortableString() );
    }
  }

  private void selectEntryPointClass() {
    EntryPointTypeSelectionDialog dialog = new EntryPointTypeSelectionDialog( getShell() );
    if( dialog.open() ) {
      txtEntryPointClass.setText( dialog.getSelection().getFullyQualifiedName() );
    }
  }

  private static GridData newFillHorizontalGridData() {
    return new GridData( SWT.FILL, SWT.TOP, true, false );
  }

  private static GridData newGridData( int horizontalSpan, int horizontalIndent ) {
    GridData result = new GridData();
    result.horizontalSpan = horizontalSpan;
    result.horizontalIndent = horizontalIndent;
    return result;
  }

  private final class EntryPointSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      updateEnablement();
      updateLaunchConfigurationDialog();
    }
  }
  
  private final class WebXmlSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      selectWebXml();
      updateLaunchConfigurationDialog();
    }
  }

  private final class EntryPointClassSelectionListener extends SelectionAdapter 
  {
    public void widgetSelected( SelectionEvent event ) {
      selectEntryPointClass();
      updateLaunchConfigurationDialog();
    }
  }

  private final class TextModifyListener implements ModifyListener {
    public void modifyText( ModifyEvent event ) {
      updateLaunchConfigurationDialog();
    }
  }

  private class ComboModifyListener implements ModifyListener {
    public void modifyText( ModifyEvent e ) {
      updateLaunchConfigurationDialog();
    }
  }
}
