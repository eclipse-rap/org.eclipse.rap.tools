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
package org.eclipse.rap.ui.internal.launch.tab;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.rap.ui.internal.launch.Activator;
import org.eclipse.rap.ui.internal.launch.LaunchMessages;
import org.eclipse.rap.ui.internal.launch.util.Images;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.SearchPattern;


final class ServletNameSelectionDialog extends FilteredItemsSelectionDialog {

  private static final String SETTINGS_ID = Activator.PLUGIN_ID + ".SERVLET_NAME_SELECTION_DIALOG"; //$NON-NLS-1$
  private static final String HAS_TARGET_SCOPE = "hasTargetScope";

  private static final Comparator COMPARATOR = new BrandingComparator();

  private BrandingExtension[] brandings;
  private boolean hasTargetScope;
  private final ToggleFilterScopeAction toggleFilterScopeAction;

  ServletNameSelectionDialog( Shell shell ) {
    super( shell );
    setTitle( LaunchMessages.ServletNameSelectionDialog_Title );
    String msg = LaunchMessages.ServletNameSelectionDialog_Message;
    toggleFilterScopeAction = new ToggleFilterScopeAction();
    setMessage( msg );
    setSelectionHistory( new ServletNameSelectionHistory() );
    setListLabelProvider( new BrandingLabelProvider() );
    setDetailsLabelProvider( new BrandingLabelProvider() );
  }

  //////////////////////////////////////////////////////////
  // FilteredItemsSelectionDialog overrides - UI adjustments

  protected Control createExtendedContentArea( Composite parent ) {
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

  protected void fillViewMenu( IMenuManager menuManager ) {
    super.fillViewMenu( menuManager );
    menuManager.add( toggleFilterScopeAction );
  }
  
  ///////////////////////////////////////////////////////////
  // FilteredItemsSelectionDialog overrides - item management

  protected void fillContentProvider( AbstractContentProvider provider,
                                      ItemsFilter itemsFilter,
                                      IProgressMonitor monitor )
    throws CoreException
  {
    startMonitoring( monitor );
    prepareContent( monitor );
    addContentToProvider( provider, itemsFilter );
    finishMonitoring( monitor );
  }

  private void startMonitoring( IProgressMonitor monitor ) {
    if( monitor != null && brandings == null ) {
      String msg = LaunchMessages.ServletNameSelectionDialog_Searching;
      monitor.beginTask( msg, IProgressMonitor.UNKNOWN );
    }
  }
  
  private void prepareContent( IProgressMonitor monitor ) {
    if( brandings == null ) {
      if( hasTargetScope ) {
        brandings = BrandingExtension.findAllActive( monitor );
      }else {
        brandings = BrandingExtension.findInWorkspace( monitor );
      }
    }
  }
  
  private void addContentToProvider( AbstractContentProvider provider, ItemsFilter itemsFilter ) {
    for( int i = 0; i < brandings.length; i++ ) {
      provider.add( brandings[ i ], itemsFilter );
    }
  }
  
  private void finishMonitoring( IProgressMonitor monitor ) {
    if( monitor != null ) {
      monitor.done();
    }
  }

  protected ItemsFilter createFilter() {
    return new BrandingItemsFilter( SelectionDialogUtil.createSearchPattern(), hasTargetScope );
  }

  public String getElementName( Object element ) {
    BrandingExtension branding = ( BrandingExtension )element;
    String project = branding.getProject();
    String servletName = branding.getServletName();
    return SelectionDialogUtil.getLabel( project, servletName );
  }

  protected Comparator getItemsComparator() {
    return COMPARATOR;
  }

  protected IStatus validateItem( Object item ) {
    return Status.OK_STATUS;
  }

  protected void storeDialog( IDialogSettings settings ) {
    super.storeDialog( settings );
    settings.put( HAS_TARGET_SCOPE, toggleFilterScopeAction.isChecked() );
  }
  
  protected void restoreDialog( IDialogSettings settings ) {
    super.restoreDialog( settings );
    if ( settings.get( HAS_TARGET_SCOPE ) != null ) {
      toggleFilterScopeAction.setChecked( settings.getBoolean( HAS_TARGET_SCOPE ) );
      toggleFilterScopeAction.run();
    }
  }
  
  ////////////////
  // Inner classes

  private static final class BrandingComparator 
    implements Comparator, Serializable 
  {

    private static final long serialVersionUID = 1L;

    public int compare( Object object1, Object object2 ) {
      BrandingExtension extension1 = ( BrandingExtension )object1;
      BrandingExtension extension2 = ( BrandingExtension )object2;
      String string1 = extension1.getProject() + extension1.getServletName();
      String string2 = extension2.getProject() + extension2.getServletName();
      return string1.compareTo( string2 );
    }
  }

  private final class BrandingItemsFilter extends ItemsFilter {

    private final boolean scope;

    public BrandingItemsFilter( SearchPattern searchPattern, boolean workspaceScope )
    {
      super( searchPattern );
      this.scope = workspaceScope;
    }

    public boolean isConsistentItem( Object item ) {
      return true;
    }

    public boolean matchItem( Object item ) {
      return matches( ( ( BrandingExtension )item ).getServletName() );
    }
    
    public boolean isSubFilter( ItemsFilter filter ) {
      boolean result;
      if( scope != ( ( BrandingItemsFilter )filter ).scope ) {
        result = false;
      } else{
        result = super.isSubFilter( filter );
      }
      return result;
    }
    
    public boolean equalsFilter( ItemsFilter filter ) {
      boolean result;
      if( scope != ( ( BrandingItemsFilter )filter ).scope ) {
        result = false;
      }else {
        result = super.equalsFilter( filter );
      }
      return result;
    }
  }
  
  private static final class BrandingLabelProvider
    extends LabelProvider
  {

    private final Image image = Images.EXTENSION.createImage();

    public String getText( Object element ) {
      String result = null;
      if( element != null ) {
        BrandingExtension branding = ( BrandingExtension )element;
        String project = branding.getProject();
        String servletName = branding.getServletName();
        result = SelectionDialogUtil.getLabel( project, servletName );
      }
      return result;
    }

    public Image getImage( Object element ) {
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
  private static final class ServletNameSelectionHistory
    extends SelectionHistory
  {

    protected Object restoreItemFromMemento( IMemento memento ) {
      return null;
    }

    protected void storeItemToMemento( Object item, IMemento memento )
    {
      // do nothing
    }
  }
  
  private class ToggleFilterScopeAction extends Action {

    public ToggleFilterScopeAction() {
        super( LaunchMessages.ServletNameSelectionDialog_WorkspaceFilterMsg, IAction.AS_CHECK_BOX );
        setChecked( hasTargetScope );
    }

    public void run() {
      hasTargetScope = isChecked();
      brandings = null;
      applyFilter();
    }
  }

}
