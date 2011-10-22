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
import org.eclipse.jdt.core.*;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class ProjectSection extends RWTLaunchTab {
  
  private Text txtProject;
  private Button btnSelectProject;

  public String getName() {
    return "Project";
  }

  public void createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
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
    setControl( group );
    HelpContextIds.assign( getControl(), HelpContextIds.MAIN_TAB );
  }

  public void initializeFrom( RWTLaunchConfig launchConfig ) {
    txtProject.setText( launchConfig.getProjectName() );
  }

  public void performApply( RWTLaunchConfig launchConfig ) {
    launchConfig.setProjectName( txtProject.getText().trim() );
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
