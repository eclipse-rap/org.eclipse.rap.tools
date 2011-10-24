package org.eclipse.rap.ui.internal.launch.rwt.tab;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.ui.internal.launch.rwt.RWTLaunchActivator;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

class FolderSelectionDialog {
  private final Shell parent;
  private final IWorkspaceRoot workspaceRoot;
  private String initialSelection;
  
  FolderSelectionDialog( Shell parent ) {
    this.parent = parent;
    this.workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
  }

  void setInitialSelection( String folder ) {
    initialSelection = folder;
  }

  IContainer open() {
    IContainer result = null;
    ElementTreeSelectionDialog dialog = new InternalFolderSelectionDialog( parent );
    dialog.setTitle( "Folder Selection" );
    dialog.setMessage( "&Choose the folder for the web application root:" );
    dialog.setInput( workspaceRoot );
    dialog.setComparator( new ResourceComparator( ResourceComparator.NAME ) );
    dialog.setValidator( new SelectionValidator() );
    if( initialSelection != null ) {
      dialog.setInitialSelection( workspaceRoot.findMember( initialSelection ) );
    }
    if( dialog.open() == Window.OK && dialog.getResult().length > 0 ) {
      result = ( IContainer )dialog.getResult()[ 0 ];
    }
    return result;
  }
  
  private static class InternalFolderSelectionDialog extends ElementTreeSelectionDialog {

    InternalFolderSelectionDialog( Shell parent ) {
      super( parent, new WorkbenchLabelProvider(), new WorkbenchContentProvider() );
    }

    protected void configureShell( Shell shell ) {
      super.configureShell( shell );
      HelpContextIds.assign( shell, HelpContextIds.MAIN_TAB );
    }

    protected Control createDialogArea( Composite parent ) {
      Composite result = ( Composite )super.createDialogArea( parent );
      getTreeViewer().expandToLevel( 2 );
      applyDialogFont( result );
      return result;
    }
  }
  
  private static class SelectionValidator implements ISelectionStatusValidator {
    private static final IStatus ERROR_STATUS
      = new Status( IStatus.ERROR, RWTLaunchActivator.getPluginId(), "" );
    private static final IStatus OK_STATUS
      = new Status( IStatus.OK, RWTLaunchActivator.getPluginId(), "" );

    public IStatus validate( Object[] selection ) {
      IStatus result = ERROR_STATUS;
      if( selection.length == 1 ) {
        if( selection[ 0 ] instanceof IProject || selection[ 0 ] instanceof IFolder ) {
          result = OK_STATUS;
        }
      }
      return result;
    }
  }
}
