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
import org.eclipse.rap.ui.internal.launch.*;
import org.eclipse.rap.ui.internal.launch.util.Images;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.SearchPattern;


final class EntryPointSelectionDialog extends FilteredItemsSelectionDialog {

  private static final EntryPointComparator ENTRY_POINT_COMPARATOR 
    = new EntryPointComparator();
  
  private static final String SETTINGS_ID 
    = Activator.PLUGIN_ID + ".ENTRY_POINT_SELECTION_DIALOG"; //$NON-NLS-1$

  private EntryPointExtension[] entryPoints;

  EntryPointSelectionDialog( final Shell shell ) {
    super( shell );
    setTitle( "Select Entry Point" );
    String msg 
      = "&Select an entry point to open (? = any character, * = any string, " 
      + "EP = EntryPoint)";
    setMessage( msg );
    setSelectionHistory( new EntryPointSelectionHistory() );
    EntryPointLabelProvider labelProvider = new EntryPointLabelProvider();
    setListLabelProvider( labelProvider );
    setDetailsLabelProvider( labelProvider );
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
        String msg = "Search for entry points in workspace";
        monitor.beginTask( msg, IProgressMonitor.UNKNOWN );
      }
      entryPoints = EntryPointExtension.findInWorkspace();
    }
    for( int i = 0; i < entryPoints.length; i++ ) {
      provider.add( entryPoints[ i ], itemsFilter );
    }
    if( monitor != null ) {
      monitor.done();
    }
  }

  protected ItemsFilter createFilter() {
    return new EntryPointItemsFilter( new EntryPointSearchPattern() );
  }

  public String getElementName( final Object item ) {
    EntryPointExtension entryPoint = ( EntryPointExtension )item;
    return getEntryPointLabel( entryPoint );
  }

  protected Comparator getItemsComparator() {
    return ENTRY_POINT_COMPARATOR;
  }

  protected IStatus validateItem( final Object item ) {
    return Status.OK_STATUS;
  }
  
  //////////////////
  // Helping methods
  
  private static final String getEntryPointLabel( 
    final EntryPointExtension entryPoint ) 
  {
    StringBuffer label = new StringBuffer();
    String parameter = entryPoint.getParameter();
    if( parameter == null ) {
      parameter = "(unknown)";
    }
    label.append( parameter );
    label.append( " - " ); //$NON-NLS-1$
    String project = entryPoint.getProject();
    if( project == null ) {
      project = "(unknown project)";
    } 
    label.append( project );
    return label.toString();
  }

  ////////////////
  // Inner classes
  
  private static final class EntryPointSelectionHistory extends SelectionHistory 
  {
    
    private static final String SEPARATOR = "#"; //$NON-NLS-1$

    protected Object restoreItemFromMemento( final IMemento memento ) {
      String project = null;
      String parameter = null;
      String textData = memento.getTextData();
      if( textData != null ) {
        String[] parts = textData.split( SEPARATOR ); 
        if( parts.length > 0 ) {
          project = parts[ 0 ];
        }
        if( parts.length > 1 ) {
          parameter = parts[ 1 ];
        }
      }
      return new EntryPointExtension( project, parameter );
    }

    protected void storeItemToMemento( final Object item, 
                                       final IMemento memento ) 
    {
      EntryPointExtension entryPoint = ( EntryPointExtension )item;
      String text 
        = entryPoint.getProject() 
        + SEPARATOR
        + entryPoint.getParameter();
      memento.putTextData( text );
    }
  }
  
  private static final class EntryPointLabelProvider extends LabelProvider {
    
    private final Image image = Images.ENTRY_POINT.createImage();
    
    public String getText( final Object element ) {
      EntryPointExtension entryPoint = ( EntryPointExtension )element;
      return getEntryPointLabel( entryPoint );
    }
    
    public Image getImage( final Object element ) {
      return image;
    }
    
    public void dispose() {
      image.dispose();
      super.dispose();
    }
  }
  
  private static final class EntryPointComparator implements Comparator {

    public int compare( final Object object1, final Object object2 ) {
      EntryPointExtension entryPoint1 = ( EntryPointExtension )object1;
      EntryPointExtension entryPoint2 = ( EntryPointExtension )object2;
      String value1 = entryPoint1.getProject() + entryPoint1.getParameter();
      String value2 = entryPoint2.getProject() + entryPoint2.getParameter();
      return value1.compareTo( value2 );
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
      EntryPointExtension entryPoint = ( EntryPointExtension )item;
      return matches( entryPoint.getParameter() );
    }
  }

  private static final class EntryPointSearchPattern extends SearchPattern {

    public void setPattern( final String stringPattern ) {
      String pattern = stringPattern;
      if( pattern.length() == 0 ) { 
        pattern = "**"; //$NON-NLS-1$
      }
      super.setPattern( pattern );
    }
  }
}
