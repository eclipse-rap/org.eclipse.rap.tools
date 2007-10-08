/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.target;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.rap.ui.internal.intro.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public final class InstallTargetDialog extends TitleAreaDialog {

  private boolean shouldSwitchTarget = true;
  private String targetDestination;
  private Image titleImage;
  private Text txtPath;

  public InstallTargetDialog( final Shell parentShell ) {
    super( parentShell );
    setShellStyle( SWT.TITLE | SWT.CLOSE | SWT.RESIZE );
    setHelpAvailable( false );
  }

  public boolean shouldSwitchTarget() {
    return shouldSwitchTarget;
  }

  public String getTargetDestination() {
    return targetDestination;
  }

  //////////////////////////////
  // (TitleArea)Dialog overrides
  
  protected Control createDialogArea( final Composite parent ) {
    Composite result = ( Composite )super.createDialogArea( parent );
    configureDialog();
    createTargetLocationArea( result );
    createSwitchTargetArea( result );
    txtPath.setText( TargetProvider.getDefaultTargetDestination() );
    return result;
  }

  protected void okPressed() {
    targetDestination = txtPath.getText();
    super.okPressed();
  }
  
  public boolean close() {
    if( titleImage != null ) {
      titleImage.dispose();
    }
    return super.close();
  }
  
  //////////////////////////////////////
  // Dialog content construction helpers 
  
  private void configureDialog() {
    getShell().setText( IntroMessages.InstallDialog_ShellTitle );
    setTitle( IntroMessages.InstallDialog_DialogTitle );
    if( titleImage == null ) {
      titleImage = Images.EXTRACT_TARGET.createImage( false );
    }
    setTitleImage( titleImage );
    setMessage( IntroMessages.InstallDialog_Message_selectLocation );
  }

  private void createTargetLocationArea( final Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    container.setLayout( new GridLayout( 3, false ) );
    Label lblPath = new Label( container, SWT.NONE );
    lblPath.setText( IntroMessages.InstallDialog_Location );
    txtPath = new Text( container, SWT.BORDER );
    txtPath.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    txtPath.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent e ) {
        validateLocation();
      }
    } );
    Button browse = new Button( container, SWT.PUSH );
    browse.setText( IntroMessages.InstallDialog_Browse );
    browse.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        DirectoryDialog dirChooser = new DirectoryDialog( getShell() );
        if( txtPath.getText().length() > 0 ) {
          dirChooser.setFilterPath( txtPath.getText() );
        }
        String location = dirChooser.open();
        if( location != null ) {
          txtPath.setText( location );
        }
      }
    } );
    Dialog.applyDialogFont( container );
  }

  private void createSwitchTargetArea( final Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, true ) );
    FillLayout layout = new FillLayout();
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    container.setLayout( layout );
    Group grgTarget = new Group( container, SWT.NONE );
    grgTarget.setLayout( new GridLayout() );
    grgTarget.setText( IntroMessages.InstallDialog_TargetGroup );
    final Button switchTarget = new Button( grgTarget, SWT.CHECK );
    switchTarget.setText( IntroMessages.InstallDialog_switchTarget );
    switchTarget.setSelection( true );
    switchTarget.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        shouldSwitchTarget = switchTarget.getSelection();
      }
    } );
    Label lblDescription = new Label( grgTarget, SWT.WRAP );
    final GridData gd3 = new GridData( GridData.FILL_HORIZONTAL );
    gd3.widthHint = 120;
    lblDescription.setLayoutData( gd3 );
    lblDescription.setText( IntroMessages.InstallDialog_TargetDescription );
    Dialog.applyDialogFont( container );
  }

  /////////////
  // Validation
  
  private void validateLocation() {
    boolean isValid = txtPath.getText().length() > 0;
    if( isValid ) {
      setErrorMessage( null );
    } else {
      setErrorMessage( IntroMessages.InstallDialog_validPath );
    }
    Button okButton = getButton( OK );
    if( okButton != null ) {
      okButton.setEnabled( isValid );
    }
  }
}
