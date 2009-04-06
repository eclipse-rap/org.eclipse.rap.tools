/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates.rap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.rap.internal.ui.templates.XmlNames;

class MailRAPTemplate extends AbstractRAPTemplate {

  public MailRAPTemplate() {
    setPageCount( 1 );
    createTemplateOptions();
  }

  public void addPages( final Wizard wizard ) {
    WizardPage page = createPage( 0 );
    page.setTitle( Messages.mailRAPTemplate_pageTitle );
    page.setDescription( Messages.mailRAPTemplate_pageDescr );
    wizard.addPage( page );
    markPagesAdded();
  }

  public String getSectionId() {
    return "mailRAP"; //$NON-NLS-1$
  }

  public String[] getNewFiles() {
    return new String[]{
      "icons/" //$NON-NLS-1$
    };
  }

  protected void updateModel( final IProgressMonitor monitor )
    throws CoreException
  {
    createEntryPointsExtension();
    createPerspectivesExtension();
    createViewsExtension();
    createCommandsExtension();
  }

  // helping methods
  // ////////////////
  private void createCommandsExtension() throws CoreException {
    IPluginExtension extension = createExtension( XmlNames.XID_COMMANDS, true );
    IPluginElement element = createElement( extension );
    element.setName( XmlNames.ELEM_CATEGORY );
    element.setAttribute( XmlNames.ATT_NAME,
                          Messages.mailRAPTemplate_categoryName );
    String categoryId = getPackageName() + ".category"; //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_ID, categoryId );
    extension.add( element );
    createCommandElement( extension,
                          Messages.mailRAPTemplate_mailboxCmdName,
                          Messages.mailRAPTemplate_mailboxCmdDescr,
                          categoryId,
                          getPackageName() + ".open" ); //$NON-NLS-1$
    createCommandElement( extension,
                          Messages.mailRAPTemplate_openMsgCmdName,
                          Messages.mailRAPTemplate_openMsgCmdDescr,
                          categoryId,
                          getPackageName() + ".openMessage" ); //$NON-NLS-1$
    addExtensionToPlugin( extension );
  }

  private void createCommandElement( final IPluginExtension extension,
                                     final String name,
                                     final String description,
                                     final String categoryId,
                                     final String commandId )
    throws CoreException
  {
    IPluginElement element = createElement( extension );
    element.setName( XmlNames.ELEM_COMMAND );
    element.setAttribute( XmlNames.ATT_NAME, name );
    element.setAttribute( XmlNames.ATT_DESCRIPTION, description );
    element.setAttribute( XmlNames.ATT_CATID, categoryId );
    element.setAttribute( XmlNames.ATT_ID, commandId );
    extension.add( element );
  }

  private void createEntryPointsExtension() throws CoreException {
    IPluginExtension extension = createExtension( XmlNames.XID_ENTRYPOINT,
                                                  true );
    IPluginElement element = createElement( extension );
    element.setName( XmlNames.ELEM_ENTRYPOINT );
    element.setAttribute( XmlNames.ATT_CLASS, 
                          getPackageName() + "." + getApplicationName() ); //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_ID, 
                          getPackageName() + "." + getApplicationName() ); //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_PARAMETER, "mail" ); //$NON-NLS-1$
    extension.add( element );
    addExtensionToPlugin( extension );
  }

  private void createPerspectivesExtension() throws CoreException {
    IPluginExtension extension = createExtension( XmlNames.XID_PERSPECTIVES,
                                                  true );
    IPluginElement element = createElement( extension );
    element.setName( XmlNames.ELEM_PERSPECTIVE );
    element.setAttribute( XmlNames.ATT_CLASS, 
                          getPackageName() + ".Perspective" ); //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_NAME, "RAP Perspective" ); //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_ID, 
                          getPluginId() + ".perspective" ); //$NON-NLS-1$
    extension.add( element );
    addExtensionToPlugin( extension );
  }

  private void createViewsExtension() throws CoreException {
    IPluginExtension extension = createExtension( XmlNames.XID_VIEWS, true );
    
    IPluginElement element = createElement( extension );
    element.setName( XmlNames.ELEM_VIEW );
    element.setAttribute( XmlNames.ATT_CLASS, 
                          getPackageName() + ".View" ); //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_NAME,
                          Messages.mailRAPTemplate_messageViewName );
    element.setAttribute( XmlNames.ATT_ID, getPluginId() + ".view" ); //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_ALLOWMULTIPLE, "true" ); //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_ICON, "icons/sample2.gif" ); //$NON-NLS-1$
    extension.add( element );
    
    element = createElement( extension );
    element.setName( XmlNames.ELEM_VIEW );
    element.setAttribute( XmlNames.ATT_CLASS,
                          getPackageName() + ".NavigationView" ); //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_NAME,
                          Messages.mailRAPTemplate_mailboxViewName );
    element.setAttribute( XmlNames.ATT_ID, getPluginId() + ".navigationView" ); //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_ICON, "icons/sample3.gif" ); //$NON-NLS-1$
    extension.add( element );
    
    addExtensionToPlugin( extension );
  }

  private void createTemplateOptions() {
    addOption( KEY_WINDOW_TITLE,
               Messages.mailRAPTemplate_windowTitle,
               Messages.mailRAPTemplate_appWindowTitle,
               0 );
    addOption( KEY_PACKAGE_NAME, Messages.mailRAPTemplate_packageName, null, 0 );
    addOption( KEY_APPLICATION_CLASS,
               Messages.mailRAPTemplate_appClass,
               "Application",  //$NON-NLS-1$
               0 );
  }
}
