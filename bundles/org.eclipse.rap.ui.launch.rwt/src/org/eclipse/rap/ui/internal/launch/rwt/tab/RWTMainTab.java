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

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.util.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class RWTMainTab extends RWTLaunchTab {

  private Text txtProject;
  private Button btnSelectProject;
  private EntryPointSection entryPointSection;
  private RuntimeSettingsSection runtimeSettingsSection;
  private BrowserSection  browserSection;
  
  public RWTMainTab() {
    entryPointSection = new EntryPointSection();
    runtimeSettingsSection = new RuntimeSettingsSection();
    browserSection = new BrowserSection();
  }
  
  public String getName() {
    return "Main";
  }
  
  public Image getImage() {
    return Images.getImage( Images.VIEW_MAIN_TAB );
  }

  public void createControl( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    container.setLayout( new GridLayout( 1, false ) );
    createProjectSection( container );
    entryPointSection.createControl( container );
    runtimeSettingsSection.createControl( container );
    browserSection.createControl( container );
    Dialog.applyDialogFont( container );
    setControl( container );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
  }

  private void createProjectSection( Composite container ) {
    Group group = new Group( container, SWT.NONE );
    group.setText( "Project" );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setLayout( new GridLayout( 2, false ) );
    txtProject = new Text( group, SWT.BORDER );
    txtProject.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    txtProject.addModifyListener( new TextModifyListener() );
    btnSelectProject = new Button( group, SWT.PUSH );
    btnSelectProject.setText( "&Browse..." );
    GridData gridData = new GridData( SWT.LEFT, SWT.TOP, false, false );
    btnSelectProject.setLayoutData( gridData );
    btnSelectProject.addSelectionListener( new JavaProjectSelectionListener() );
  }

  public void setLaunchConfigurationDialog( ILaunchConfigurationDialog dialog ) {
    super.setLaunchConfigurationDialog( dialog );
    entryPointSection.setLaunchConfigurationDialog( dialog );
    runtimeSettingsSection.setLaunchConfigurationDialog( dialog );
    browserSection.setLaunchConfigurationDialog( dialog );
  }

  public void initializeFrom( RWTLaunchConfig launchConfig ) {
    txtProject.setText( launchConfig.getProjectName() );
    entryPointSection.initializeFrom( launchConfig );
    runtimeSettingsSection.initializeFrom( launchConfig );
    browserSection.initializeFrom( launchConfig );
  }

  public void performApply( RWTLaunchConfig launchConfig ) {
    launchConfig.setProjectName( txtProject.getText().trim() );
    entryPointSection.performApply( launchConfig );
    runtimeSettingsSection.performApply( launchConfig );
    browserSection.performApply( launchConfig );
  }
  
  private void handleSelectJavaProject() {
    IJavaProject project = selectJavaProject();
    if( project != null ) {
      String projectName = project.getElementName();
      txtProject.setText( projectName );
    }
  }

  private IJavaProject selectJavaProject() {
    Shell shell = getShell();
    JavaProjectSelectionDialog dialog = new JavaProjectSelectionDialog( shell );
    dialog.setInitialSelection( getJavaProject() );
    return dialog.open();
  }

  private IJavaProject getJavaProject() {
    IJavaProject result = null;
    String projectName = txtProject.getText().trim();
    if( projectName.length() > 0 ) {
      result = getJavaModel().getJavaProject( projectName );
    }
    return result;    
  }

  private static IJavaModel getJavaModel() {
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    return JavaCore.create( workspaceRoot );
  }
  
  private class JavaProjectSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      handleSelectJavaProject();
      updateLaunchConfigurationDialog();
    }
  }
  
  private class TextModifyListener implements ModifyListener {
    public void modifyText( ModifyEvent event ) {
      updateLaunchConfigurationDialog();
    }
  }
}
