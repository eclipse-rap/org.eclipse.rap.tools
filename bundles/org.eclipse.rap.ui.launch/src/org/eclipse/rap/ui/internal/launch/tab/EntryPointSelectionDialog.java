/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.ui.internal.launch.tab;

import java.util.Comparator;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.rap.ui.internal.launch.Activator;
import org.eclipse.rap.ui.internal.launch.util.Images;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.SearchPattern;


final class EntryPointSelectionDialog extends FilteredItemsSelectionDialog {

  private static final String SETTINGS_ID 
    = Activator.PLUGIN_ID + ".ENTRY_POINT_SELECTION_DIALOG"; //$NON-NLS-1$

  private static final Comparator COMPARATOR = new EntryPointComparator();
  
  private EntryPointExtension[] entryPoints;

  EntryPointSelectionDialog( final Shell shell ) {
    super( shell );
    setTitle( "Select Entry Point" );
    String msg 
      = "&Select an entry point to open (? = any character, * = any string, " 
      + "EP = EntryPoint)";
    setMessage( msg );
    setSelectionHistory( new EntryPointSelectionHistory() );
    setListLabelProvider( new EntryPointLabelProvider() );
    setDetailsLabelProvider( new EntryPointLabelProvider() );
  }

  //////////////////////////////////////////////////////////
  // FilteredItemsSelectionDialog overrides - UI adjustments  
  
  protected Control createExtendedContentArea( final Composite parent ) {
    return null;
  }

  protected IDialogSettings getDialogSettings() {
    IDialogSettings settings = Activator.getDefault().getDialogSettings();
    IDialogSettings section = settings.getSection( SETTINGS_ID );
    if( section == null ) {
      section = settings.addNewSection( SETTINGS_ID );
    }
    return section;
  }

  ///////////////////////////////////////////////////////////
  // FilteredItemsSelectionDialog overrides - item management   
  
  protected void fillContentProvider( final AbstractContentProvider provider,
                                      final ItemsFilter itemsFilter,
                                      final IProgressMonitor monitor )
    throws CoreException
  {
    if( entryPoints == null ) {
      if( monitor != null ) {
        String msg = "Searching for entry points in workspace";
        monitor.beginTask( msg, IProgressMonitor.UNKNOWN );
      }
      entryPoints = EntryPointExtension.findInWorkspace( monitor );
    }
    for( int i = 0; i < entryPoints.length; i++ ) {
      provider.add( entryPoints[ i ], itemsFilter );
    }
    if( monitor != null ) {
      monitor.done();
    }
  }

  protected ItemsFilter createFilter() {
    SearchPattern searchPattern = SelectionDialogUtil.createSearchPattern();
    return new EntryPointItemsFilter( searchPattern );
  }

  public String getElementName( final Object element ) {
    EntryPointExtension entryPoint = ( EntryPointExtension )element;
    String project = entryPoint.getProject();
    String parameter = entryPoint.getParameter();
    return SelectionDialogUtil.getLabel( project, parameter );
  }

  protected Comparator getItemsComparator() {
    return COMPARATOR;
  }

  protected IStatus validateItem( final Object item ) {
    return Status.OK_STATUS;
  }

  ////////////////
  // Inner classes
  
  private static final class EntryPointComparator implements Comparator {
    
    public int compare( final Object object1, final Object object2 ) {
      EntryPointExtension extension1 = ( EntryPointExtension )object1;
      EntryPointExtension extension2 = ( EntryPointExtension )object2;
      String string1 = extension1.getProject() + extension1.getParameter(); 
      String string2 = extension2.getProject() + extension2.getParameter();
      return string1.compareTo( string2 );
    }
  }

  private final class EntryPointItemsFilter extends ItemsFilter {
    
    public EntryPointItemsFilter( final SearchPattern searchPattern ) {
      super( searchPattern );
    }

    public boolean isConsistentItem( final Object item ) {
      return true;
    }

    public boolean matchItem( final Object item ) {
      return matches( ( ( EntryPointExtension )item ).getParameter() );
    }
  }
  
  private static final class EntryPointLabelProvider extends LabelProvider {

    private final Image image = Images.EXTENSION.createImage();

    public String getText( final Object element ) {
      String result = null;
      if( element != null ) {
        EntryPointExtension entryPoint = ( EntryPointExtension )element;
        String project = entryPoint.getProject();
        String parameter = entryPoint.getParameter();
        result = SelectionDialogUtil.getLabel( project, parameter );
      }
      return result;
    }

    public Image getImage( final Object element ) {
      return image;
    }

    public void dispose() {
      image.dispose();
      super.dispose();
    }
  }

  /* Empty SelectionHistory implementation, necessary to be able pass something
   * non-null to setSelectionHistory. Without calling it, an exception would 
   * occur when the dialog is canceled. */
  private static final class EntryPointSelectionHistory extends SelectionHistory 
  {
    
    protected Object restoreItemFromMemento( final IMemento memento ) {
      return null;
    }

    protected void storeItemToMemento( final Object item, 
                                       final IMemento memento ) 
    {
      // do nothing
    }
  }
}
