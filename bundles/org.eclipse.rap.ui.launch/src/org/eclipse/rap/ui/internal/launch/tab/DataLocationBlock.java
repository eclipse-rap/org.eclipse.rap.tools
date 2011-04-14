/*******************************************************************************
 * Copyright (c) 2011 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.tab;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.launcher.BaseBlock;
import org.eclipse.pde.ui.launcher.AbstractLauncherTab;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/* 
 * TODO: This class was copied form org.eclipse.pde.internal.ui.launcher.WorkspaceDataBlock and 
 * modified to fit RAP's needs. Maybe we can add our own implementation in the future to avoid the
 * discouraged access warnings.
 */
public class DataLocationBlock extends BaseBlock {

  private Button useDefaultLocationButton;
  private Button clearInstanceAreaButton;
  private String instanceName;

  public DataLocationBlock( AbstractLauncherTab launcherTab ) {
    super( launcherTab );
  }

  public Control createControl( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( Messages.DataLocationBlock_InstanceAreaTitle );
    group.setLayout( new GridLayout( 2, false ) );
    useDefaultLocationButton = createDefaultLocationButton( group );
    useDefaultLocationButton.setLayoutData( createHFillGridDataWithHSpan( 2 ) );
    createText( group, PDEUIMessages.ConfigurationTab_configLog, 20 );
    Control buttonArea = createButtonArea( group );
    buttonArea.setLayoutData( createHFillGridDataWithHSpan( 2 ) );
    return group;
  }

  public void initializeFrom( RAPLaunchConfig rapConfig )
    throws CoreException
  {
    instanceName = rapConfig.getName();
    boolean useDefaultDataLocation = rapConfig.getUseDefaultDatatLocation();
    useDefaultLocationButton.setSelection( useDefaultDataLocation );
    enableBrowseSection( !useDefaultDataLocation );
    fLocationText.setEditable( !useDefaultDataLocation );
    fLocationText.setEnabled( !useDefaultDataLocation );
    boolean doClear = rapConfig.getDoClearDataLocation();
    clearInstanceAreaButton.setSelection( doClear );
    String defaultLocation = RAPLaunchConfig.getDefaultDataLocation( instanceName );
    String dataLocation = rapConfig.getDataLocation();
    if( useDefaultDataLocation ) {
      fLocationText.setText( defaultLocation );
    } else {
      fLocationText.setText( dataLocation );
    }
  }

  public boolean getUseDefaultDataLocation() {
    return useDefaultLocationButton.getSelection();
  }

  public boolean getDoClearDataLocation() {
    return clearInstanceAreaButton.getSelection();
  }

  public String getLocation() {
    return super.getLocation();
  }

  protected String getName() {
    return "instanceAreaBlock"; //$NON-NLS-1$
  }

  protected boolean isFile() {
    return false;
  }

  private Button createDefaultLocationButton( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( PDEUIMessages.ConfigurationTab_useDefaultLoc );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        updateUseDefaultLocation( button.getSelection() );
      }
    } );
    return button;
  }

  private void updateUseDefaultLocation( boolean useDefaultLocation ) {
    if( useDefaultLocation ) {
      String defaultLocation = RAPLaunchConfig.getDefaultDataLocation( instanceName );
      fLocationText.setText( defaultLocation );
    }
    enableBrowseSection( !useDefaultLocation );
    fLocationText.setEditable( !useDefaultLocation );
    fLocationText.setEnabled( !useDefaultLocation );
  }

  private Control createButtonArea( Composite parent ) {
    Composite buttonsArea = new Composite( parent, SWT.NONE );
    GridLayout layout = new GridLayout( 4, false );
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    buttonsArea.setLayout( layout );
    createInstanceAreaButton( buttonsArea );
    createButtons( buttonsArea, new String[] {
      PDEUIMessages.BaseBlock_workspace,
      PDEUIMessages.BaseBlock_filesystem,
      PDEUIMessages.BaseBlock_variables
    } );
    return buttonsArea;
  }

  private void createInstanceAreaButton( Composite parent ) {
    clearInstanceAreaButton = new Button( parent, SWT.CHECK );
    clearInstanceAreaButton.setText( Messages.DataLocationBlock_ClearInstanceAreaLabel );
    GridData layoutData = createHFillGridDataWithHSpan( 1 );
    clearInstanceAreaButton.setLayoutData( layoutData );
    clearInstanceAreaButton.addSelectionListener( fListener );
  }

  private static GridData createHFillGridDataWithHSpan( int horizontalSpan ) {
    GridData result = new GridData( SWT.FILL, SWT.CENTER, true, false );
    result.horizontalSpan = horizontalSpan;
    return result;
  }
  
}
