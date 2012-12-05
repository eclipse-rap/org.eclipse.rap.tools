/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Rüdiger Herrmann - adopted original code for use in RWT launcher 
 *******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class VMArgumentsSection extends RWTLaunchTab {
  private Text txtVMArguments;
  private Button btnVariables;

  public void createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    setControl( group );
    group.setLayout( new GridLayout() );
    group.setLayoutData( newFillGridData() );
    group.setText( "VM arguments" );
    int style = SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL;
    txtVMArguments = new Text( group, style );
    txtVMArguments.addTraverseListener( new VMArgumentsTraverseListener() );
    txtVMArguments.setLayoutData( newFillGridData() );
    txtVMArguments.addModifyListener( new VMArgumentsModifyListener() );
    btnVariables = createPushButton( group, "Variable&s...", null );
    btnVariables.setLayoutData( new GridData( SWT.END, SWT.DEFAULT, false, false ) );
    btnVariables.addSelectionListener( new VariablesSelectionListener() );
  }

  public void initializeFrom( RWTLaunchConfig launchCOnfig ) {
    txtVMArguments.setText( launchCOnfig.getVMArguments() );
  }

  public void performApply( RWTLaunchConfig launchConfig ) {
    launchConfig.setVMArguments( txtVMArguments.getText().trim() );
  }

  public String getName() {
    return "VM arguments";
  }

  private static GridData newFillGridData() {
    return new GridData( SWT.FILL, SWT.FILL, true, true );
  }

  private void selectVariable() {
    StringVariableSelectionDialog dialog = new StringVariableSelectionDialog( getShell() );
    dialog.open();
    String variable = dialog.getVariableExpression();
    if( variable != null ) {
      txtVMArguments.insert( variable );
    }
    dialog.close();
  }

  private final class VMArgumentsModifyListener implements ModifyListener {
    public void modifyText( ModifyEvent event ) {
      scheduleUpdateJob();
    }
  }

  private final class VariablesSelectionListener extends SelectionAdapter {
    public void widgetSelected( SelectionEvent event ) {
      selectVariable();
    }
  }

  private final class VMArgumentsTraverseListener implements TraverseListener {
    public void keyTraversed( TraverseEvent event ) {
      switch( event.detail ) {
        case SWT.TRAVERSE_ESCAPE:
        case SWT.TRAVERSE_PAGE_NEXT:
        case SWT.TRAVERSE_PAGE_PREVIOUS:
          event.doit = true;
        break;
        case SWT.TRAVERSE_RETURN:
        case SWT.TRAVERSE_TAB_NEXT:
        case SWT.TRAVERSE_TAB_PREVIOUS:
          if( ( txtVMArguments.getStyle() & SWT.SINGLE ) != 0 ) {
            event.doit = true;
          } else {
            if( !txtVMArguments.isEnabled() || ( event.stateMask & SWT.MODIFIER_MASK ) != 0 ) {
              event.doit = true;
            }
          }
        break;
      }
    }
  }
}
