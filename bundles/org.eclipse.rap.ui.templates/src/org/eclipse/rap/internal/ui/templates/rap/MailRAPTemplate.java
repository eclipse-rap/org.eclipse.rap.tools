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
import org.eclipse.rap.internal.ui.templates.TemplateUtil;
import org.eclipse.rap.internal.ui.templates.XmlNames;

class MailRAPTemplate extends AbstractRAPTemplate {

  MailRAPTemplate() {
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

  public String getApplicationId() {
    return "mailapp";
  }

  protected void updateModel( final IProgressMonitor monitor )
    throws CoreException
  {
    createApplicationExtension();
    createPerspectivesExtension();
    createViewsExtension();
    createCommandsExtension();
    createBrandingExtension();
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
                          getPluginId() + ".open" ); //$NON-NLS-1$
    createCommandElement( extension,
                          Messages.mailRAPTemplate_openMsgCmdName,
                          Messages.mailRAPTemplate_openMsgCmdDescr,
                          categoryId,
                          getPluginId() + ".openMessage" ); //$NON-NLS-1$
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

  private void createApplicationExtension() throws CoreException {
    IPluginExtension extension = createExtension( XmlNames.XID_APPLICATION,
                                                  true );
    extension.setId( getApplicationId() );
    IPluginElement applicationElement = createElement( extension );
    applicationElement.setName( XmlNames.ELEM_APPLICATION );
    applicationElement.setAttribute( XmlNames.ATT_VISIBLE, 
                                     "true" ); //$NON-NLS-1$
    applicationElement.setAttribute( XmlNames.ATT_CARDINALITY,
                                     "singleton-global" ); //$NON-NLS-1$
    applicationElement.setAttribute( XmlNames.ATT_THREAD, 
                                     "main" ); //$NON-NLS-1$
    extension.add( applicationElement );
    IPluginElement runElement = createElement( extension );
    runElement.setName( XmlNames.ELEM_RUN ); //$NON-NLS-1$
    runElement.setAttribute( XmlNames.ATT_CLASS, getApplicationClass() ); //$NON-NLS-1$
    applicationElement.add( runElement );
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
  
  private void createBrandingExtension() throws CoreException {
    IPluginExtension extension = createExtension( XmlNames.XID_BRANDING, true );
    IPluginElement brandingElement = createElement( extension );
    // create branding
    brandingElement.setName( XmlNames.ELEM_BRANDING );    
    String brandingId = getPackageName() + ".branding"; //$NON-NLS-1$
    brandingElement.setAttribute( XmlNames.ATT_ID, brandingId );
    brandingElement.setAttribute( XmlNames.ATT_SERVLET, "mail" );
    brandingElement.setAttribute( XmlNames.ATT_DEFAULT_ENTRYPOINT, 
                                  getFullApplicationId() );
    brandingElement.setAttribute( XmlNames.ATT_THEME_ID, 
                                  TemplateUtil.BUSINESS_THEME_ID );
    brandingElement.setAttribute( XmlNames.ATT_TITLE, "RAP Maildemo" );
    // create presentationFactory
    IPluginElement presentationElement = createElement( extension );
    presentationElement.setName( XmlNames.ELEM_PRESENTATIONFACTORY );   
    presentationElement.setAttribute( XmlNames.ATT_ID, 
                                      TemplateUtil.BUSINESS_FACTORY_ID );
    presentationElement.setAttribute( XmlNames.ATT_DEFAULT_LAYOUT, 
                                      TemplateUtil.BUSINESS_LAYOUT_ID );
    presentationElement.setAttribute( XmlNames.ATT_NAME, 
                                      "Business PresentationFactory" );
    brandingElement.add( presentationElement );
    // create defaultStackPresentation
    IPluginElement stackElement = createElement( extension );
    stackElement.setName( XmlNames.ELEM_DEFAULTSTACKPRESENTATION );
    stackElement.setAttribute( XmlNames.ATT_ID, TemplateUtil.STACK_ID );
    presentationElement.add( stackElement );
    // write extension
    extension.add( brandingElement );
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
