/*******************************************************************************
 * Copyright (c) 2007, 2011 EclipseSource
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates.rap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.rap.internal.ui.templates.XmlNames;

class HelloRAPTemplate extends AbstractRAPTemplate {

  HelloRAPTemplate() {
    setPageCount( 1 );
    createTemplateOptions();
  }

  public void addPages( final Wizard wizard ) {
    WizardPage page = createPage( 0 );
    page.setTitle( Messages.helloRAPTemplate_pageTitle );
    page.setDescription( Messages.helloRAPTemplate_pageDescr );
    wizard.addPage( page );
    markPagesAdded();
  }

  public String getSectionId() {
    return "helloRAP"; //$NON-NLS-1$
  }
  
  public String getApplicationId() {
    return "helloapp";
  }

  protected void updateModel( final IProgressMonitor monitor )
    throws CoreException
  {
    createApplicationExtension();
    createPerspectivesExtension();
  }

  ///////////////////
  // helping methods
  
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
    IPluginExtension extension 
      = createExtension( XmlNames.XID_PERSPECTIVES, true );
    
    IPluginElement element = createElement( extension );
    element.setName( XmlNames.ELEM_PERSPECTIVE );
    element.setAttribute( XmlNames.ATT_CLASS, 
                          getPackageName() + ".Perspective" ); //$NON-NLS-1$
    element.setAttribute( XmlNames.ATT_NAME,
                          Messages.helloRAPTemplate_perspectiveName );
    element.setAttribute( XmlNames.ATT_ID, 
                          getPluginId() + ".perspective" ); //$NON-NLS-1$
    extension.add( element );
    addExtensionToPlugin( extension );
  }

  private void createTemplateOptions() {
    addOption( KEY_WINDOW_TITLE,
               Messages.helloRAPTemplate_windowTitle,
               Messages.helloRAPTemplate_appWindowTitle,
               0 );
    addOption( KEY_PACKAGE_NAME, Messages.helloRAPTemplate_packageNmae, null, 0 );
    addOption( KEY_APPLICATION_CLASS,
               Messages.helloRAPTemplate_appClass,
               "Application", //$NON-NLS-1$ 
               0 ); 
  }
  
}
