/*******************************************************************************
 * Copyright (c) 2007, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro.target;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.rap.ui.internal.intro.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public final class InstallTargetDialog extends TitleAreaDialog {

  private final static String[] ROOT_IUs = new String[]{
    "org.eclipse.rap.runtime.sdk.feature.group" //$NON-NLS-1$
  };
  private final static String TARGET_REPOSITORY_LATEST_BUILD 
    = "http://download.eclipse.org/rt/rap/latest-stable/runtime"; //$NON-NLS-1$
  private final static String TARGET_REPOSITORY_LATEST_RELEASE 
    = "http://download.eclipse.org/rt/rap/latest-release/runtime"; //$NON-NLS-1$
  private final static String TARGET_QUALIFIER_LATEST_BUILD = "latest stable build"; //$NON-NLS-1$
  private final static String TARGET_QUALIFIER_LATEST_RELEASE = "latest release"; //$NON-NLS-1$
  private boolean isLatestBuild = true;
  private boolean shouldSwitchTarget = true;
  private Image titleImage;

  public InstallTargetDialog( Shell parentShell ) {
    super( parentShell );
    setShellStyle( SWT.TITLE | SWT.CLOSE | SWT.RESIZE );
    setHelpAvailable( false );
  }

  public boolean shouldSwitchTarget() {
    return shouldSwitchTarget;
  }

  public String getTargetRepository() {
    return isLatestBuild ? TARGET_REPOSITORY_LATEST_BUILD 
                         : TARGET_REPOSITORY_LATEST_RELEASE;
  }

  public String getTargetQualifier() {
    return isLatestBuild ? TARGET_QUALIFIER_LATEST_BUILD
                         : TARGET_QUALIFIER_LATEST_RELEASE;
  }

  public String[] getRootIUs() {
    return ROOT_IUs;
  }

  protected Point getInitialSize() {
    Point initialSize = super.getInitialSize();
    return new Point( initialSize.x, initialSize.y - 150 );
  }
  
  protected Control createDialogArea( Composite parent ) {
    Composite result = ( Composite )super.createDialogArea( parent );
    configureDialog();
    createTargetDescription( result );
    createTargetLocationArea( result );
    createSwitchTargetArea( result );
    Dialog.applyDialogFont( result );
    return result;
  }

  protected void okPressed() {
    super.okPressed();
  }

  public boolean close() {
    if( titleImage != null ) {
      titleImage.dispose();
    }
    return super.close();
  }

  private void configureDialog() {
    getShell().setText( IntroMessages.InstallDialog_ShellTitle );
    setTitle( IntroMessages.InstallDialog_DialogTitle );
    if( titleImage == null ) {
      titleImage = Images.EXTRACT_TARGET.createImage( false );
    }
    setTitleImage( titleImage );
    setMessage( IntroMessages.InstallDialog_Message_selectLocation );
  }

  private void createTargetDescription( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    container.setLayout( new GridLayout() );
    Label targetDescriptionLabel = new Label( container, SWT.WRAP );
    targetDescriptionLabel.setLayoutData( getLayoutDataForDescriptions() );
    targetDescriptionLabel.setText( IntroMessages.InstallTargetDialog_TargetDescriptionMsg );
  }
  
  private void createTargetLocationArea( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    container.setLayout( new GridLayout() );
    Group targetSelectionGroup = new Group( container, SWT.NONE );
    targetSelectionGroup.setLayout( new GridLayout( 1, true ) );
    targetSelectionGroup.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    String targetVersionGroupTitle = IntroMessages.InstallTargetDialog_TargetVersionGroupTitle;
    targetSelectionGroup.setText( targetVersionGroupTitle );
    createButtons( targetSelectionGroup );
  }

  private void createButtons( Group targetSelectionGroup ) {
    Button latestStableBuildBtn = new Button( targetSelectionGroup, SWT.RADIO );
    String latestBuildText = IntroMessages.InstallTargetDialog_LatestBuildText;
    latestStableBuildBtn.setText( latestBuildText );
    latestStableBuildBtn.setSelection( true );
    latestStableBuildBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        isLatestBuild = true;
      }
    } );
    Button latestReleaseBtn = new Button( targetSelectionGroup, SWT.RADIO );
    String latestReleaseText 
      = IntroMessages.InstallTargetDialog_LatestReleaseText;
    latestReleaseBtn.setText( latestReleaseText );
    latestReleaseBtn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        isLatestBuild = false;
      }
    } );
  }

  private void createSwitchTargetArea( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayout( new GridLayout() );
    container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    Group grgTarget = new Group( container, SWT.NONE );
    grgTarget.setLayout( new GridLayout() );
    grgTarget.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    grgTarget.setText( IntroMessages.InstallDialog_TargetGroup );
    final Button switchTarget = new Button( grgTarget, SWT.CHECK );
    switchTarget.setText( IntroMessages.InstallDialog_switchTarget );
    switchTarget.setSelection( true );
    switchTarget.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        shouldSwitchTarget = switchTarget.getSelection();
      }
    } );
    Label lblDescription = new Label( grgTarget, SWT.WRAP );
    lblDescription.setLayoutData( getLayoutDataForDescriptions() );
    lblDescription.setText( IntroMessages.InstallDialog_TargetDescription );
  }
  
  private GridData getLayoutDataForDescriptions() {
    GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
    gridData.widthHint = 120;
    return gridData;
  }
  
}
