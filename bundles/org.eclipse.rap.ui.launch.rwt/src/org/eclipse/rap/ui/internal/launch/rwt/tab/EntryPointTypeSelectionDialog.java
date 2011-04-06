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

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;


public class EntryPointTypeSelectionDialog {

  private final Shell parent;
  private IType selection;

  public EntryPointTypeSelectionDialog( Shell parent ) {
    this.parent = parent;
  }
  
  public boolean open() {
    selection = null;
    SelectionDialog dialog = createDialog();
    boolean result = dialog.open() == Window.OK;
    if( result ) {
      selection = ( IType )dialog.getResult()[ 0 ];
    }
    return result;
  }

  public IType getSelection() {
    return selection;
  }

  private SelectionDialog createDialog() {
    IRunnableContext context = PlatformUI.getWorkbench().getProgressService();
    IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
    int style = IJavaElementSearchConstants.CONSIDER_CLASSES;
    String filter = "";
    try {
      return JavaUI.createTypeDialog( parent, context, scope, style, false, filter, null );
    } catch( JavaModelException jme ) {
      throw new RuntimeException( "Failed to open JavaElement selection dialog.", jme );
    }
  }
}
