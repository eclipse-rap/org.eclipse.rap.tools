/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.target;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InstallTargetDialog extends TitleAreaDialog {

  protected boolean shouldswitchTarget = true;
  protected String targetDestination;
  private Text txtPath;

  public InstallTargetDialog( Shell parentShell ) {
    super( parentShell );
    setHelpAvailable( false );
  }

  protected Control createDialogArea( Composite parent ) {
    Composite container = createTargetLocationArea( parent );
    createSwitchTargetGroup( container );
    return container;
  }

  private Composite createTargetLocationArea( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayout( new GridLayout( 3, false ) );
    Dialog.applyDialogFont( container );
    getShell().setText( IntroMessages.InstallDialog_ShellTitle );
    setTitle( IntroMessages.InstallDialog_DialogTitle );
    setMessage( IntroMessages.InstallDialog_Message_selectLocation );
    Label lblPath = new Label( container, SWT.NONE );
    lblPath.setText( IntroMessages.InstallDialog_Path );
    txtPath = new Text( container, SWT.SINGLE );
    txtPath.setText( TargetProvider.getTargetDest() );
    txtPath.addModifyListener( new ModifyListener() {

      public void modifyText( ModifyEvent e ) {
        if( checkLocation() ) {
          targetDestination = txtPath.getText();
        }
      }
    } );
    Button browse = new Button( container, SWT.PUSH );
    browse.setText( IntroMessages.InstallDialog_Browse );
    browse.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        DirectoryDialog dirChooser = new DirectoryDialog( getShell() );
        String location = dirChooser.open();
        txtPath.setText( location == null
                                         ? "" : location ); //$NON-NLS-1$
      }
    } );
    return container;
  }

  private boolean checkLocation() {
    boolean result = false;
    String location = txtPath.getText();
    File dir = new File( location );
    if( location != null && dir.canWrite() && dir.isDirectory() ) {
      // everything ok
      setErrorMessage( null );
      result = true;
    } else {
      setErrorMessage( IntroMessages.InstallDialog_validPath );
    }
    return result;
  }

  private void createSwitchTargetGroup( Composite container ) {
    final GridData gd2 = new GridData( GridData.FILL_HORIZONTAL );
    gd2.horizontalSpan = 3;
    Group gTarget = new Group( container, SWT.SHADOW_ETCHED_OUT );
    gTarget.setLayoutData( gd2 );
    gTarget.setLayout( new GridLayout() );
    gTarget.setText( IntroMessages.InstallDialog_TargetGroup );
    final Button switchTarget = new Button( gTarget, SWT.CHECK );
    switchTarget.setText( IntroMessages.InstallDialog_switchTarget );
    switchTarget.setSelection( true );
    switchTarget.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        shouldswitchTarget = switchTarget.getSelection();
      }
    } );
    Label lblDescription = new Label( gTarget, SWT.WRAP );
    final GridData gd3 = new GridData( GridData.FILL_HORIZONTAL );
    gd3.widthHint = 225;
    lblDescription.setLayoutData( gd3 );
    lblDescription.setText( IntroMessages.InstallDialog_TargetDescription );
  }

  public boolean shouldSwitchTarget() {
    return shouldswitchTarget;
  }

  public String getTargetDestination() {
    return targetDestination;
  }
}
