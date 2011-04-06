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
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.ui.internal.launch.rwt.util.StatusUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class JavaProjectSelectionDialog {
  private static final int LABEL_PROVIDER_FLAGS 
    = JavaElementLabelProvider.SHOW_DEFAULT;
  
  private final Shell parent;
  private ElementListSelectionDialog dialog;
  private IJavaProject initialSelection;

  public JavaProjectSelectionDialog( Shell parent ) {
    this.parent = parent;
  }
  
  public void setInitialSelection( IJavaProject initialSelection ) {
    this.initialSelection = initialSelection;
  }
  
  public IJavaProject getInitialSelection() {
    return initialSelection;
  }
  
  public IJavaProject open() {
    createDialog();
    initializeDialog();
    IJavaProject result = openDialog();
    closeDialog();
    return result;
  }

  private void createDialog() {
    ILabelProvider labelProvider = createLabelProvider();
    dialog = new ElementListSelectionDialog( parent, labelProvider );
    dialog.setTitle( "Project Selection" );
    dialog.setMessage( "Select a project to constrain your search" );
  }

  private void initializeDialog() {
    dialog.setElements( collectAllJavaProjects() );
    if( initialSelection != null ) {
      dialog.setInitialSelections( new Object[]{ initialSelection } );
    } else {
      dialog.setInitialSelections( new Object[ 0 ] );
    }
  }

  private IJavaProject openDialog() {
    IJavaProject result = null;
    if( dialog.open() == Window.OK ) {
      result = ( IJavaProject )dialog.getFirstResult();
    }
    return result;
  }

  private void closeDialog() {
    dialog.close();
    dialog = null;
  }

  private static JavaElementLabelProvider createLabelProvider() {
    return new JavaElementLabelProvider( LABEL_PROVIDER_FLAGS );
  }

  private static IJavaProject[] collectAllJavaProjects() {
    IJavaProject[] result;
    try {
      result = JavaCore.create( getWorkspaceRoot() ).getJavaProjects();
    } catch( JavaModelException jme ) {
      result = new IJavaProject[ 0 ];
      StatusUtil.log( jme );
    }
    return result;
  }

  private static IWorkspaceRoot getWorkspaceRoot() {
    return ResourcesPlugin.getWorkspace().getRoot();
  }
}
