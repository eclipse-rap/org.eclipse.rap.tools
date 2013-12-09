/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;


class SearchText {

  private final int margin;
  private Label label;
  private Text text;
  private Button button;

  SearchText( Composite parent, String labelText, String buttonText, int margin ) {
    this.margin = margin;
    this.label = createLabel( parent, labelText );
    this.text = createText( parent );
    this.button = createButton( parent, buttonText );
  }

  void setEnabled( boolean enabled ) {
    label.setEnabled( enabled );
    text.setEnabled( enabled );
    button.setEnabled( enabled );
  }

  String getText() {
    return text.getText().trim();
  }

  void setText( String string ) {
    text.setText( string );
  }

  void addModifyListener( ModifyListener modifyListener ) {
    text.addModifyListener( modifyListener );
  }

  void removeModifyListener( ModifyListener modifyListener ) {
    text.removeModifyListener( modifyListener );
  }

  void addSelectionListener( SelectionListener selectionListener ) {
    button.addSelectionListener( selectionListener );
  }

  void removeSelectionListener( SelectionListener selectionListener ) {
    button.removeSelectionListener( selectionListener );
  }

  private Label createLabel( Composite parent, String string ) {
    Label result = new Label( parent, SWT.NONE );
    result.setText( string );
    GridData gridData = new GridData();
    gridData.horizontalIndent = margin;
    result.setLayoutData( gridData );
    return result;
  }

  private Text createText( Composite parent ) {
    Text result = new Text( parent, SWT.BORDER );
    result.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    return result;
  }

  private Button createButton( Composite parent, String string ) {
    Button result = new Button( parent, SWT.PUSH );
    result.setText( string );
    return result;
  }

}
