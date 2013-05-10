/*******************************************************************************
 * Copyright (c) 2007, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro.target;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public final class InstallTargetDialog extends Dialog {

  private boolean shouldSwitchTarget = true;

  public InstallTargetDialog( Shell parentShell ) {
    super( parentShell );
    setShellStyle( SWT.TITLE | SWT.CLOSE | SWT.RESIZE );
  }

  public boolean shouldSwitchTarget() {
    return shouldSwitchTarget;
  }

  protected Point getInitialSize() {
    Point initialSize = super.getInitialSize();
    return new Point( initialSize.x + 100, initialSize.y - 150 );
  }

  protected Control createDialogArea( Composite parent ) {
    Composite result = ( Composite )super.createDialogArea( parent );
    configureDialog();
    createTargetDescription( result );
    createSwitchTargetArea( result );
    Dialog.applyDialogFont( result );
    return result;
  }

  private void configureDialog() {
    getShell().setText( IntroMessages.InstallDialog_ShellTitle );
  }

  private void createTargetDescription( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    container.setLayout( new GridLayout() );
    Label targetDescriptionLabel = new Label( container, SWT.WRAP );
    targetDescriptionLabel.setLayoutData( getLayoutDataForDescriptions() );
    String text = MessageFormat.format( IntroMessages.InstallTargetDialog_TargetDescriptionMsg,
                                        new Object[] { TargetProvider.getVersion() } );
    targetDescriptionLabel.setText( text );
  }

  private void createSwitchTargetArea( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayout( new GridLayout() );
    container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    container.setLayout( new GridLayout() );
    final Button switchTarget = new Button( container, SWT.CHECK );
    switchTarget.setText( IntroMessages.InstallDialog_switchTarget );
    switchTarget.setSelection( true );
    switchTarget.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        shouldSwitchTarget = switchTarget.getSelection();
      }
    } );
    Label lblDescription = new Label( container, SWT.WRAP );
    lblDescription.setLayoutData( getLayoutDataForDescriptions() );
    lblDescription.setText( IntroMessages.InstallDialog_TargetDescription );
  }

  private GridData getLayoutDataForDescriptions() {
    GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
    gridData.widthHint = 120;
    return gridData;
  }

}
