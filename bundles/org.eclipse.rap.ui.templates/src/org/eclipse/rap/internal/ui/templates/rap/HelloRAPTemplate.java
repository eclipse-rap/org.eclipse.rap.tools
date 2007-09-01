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
import org.eclipse.rap.internal.ui.templates.IXmlNames;

class HelloRAPTemplate extends AbstractRAPTemplate {

  public HelloRAPTemplate() {
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

  protected void updateModel( final IProgressMonitor monitor )
    throws CoreException
  {
    createEntryPointsExtension();
    createPerspectivesExtension();
  }

  // helping methods
  // ////////////////
  private void createEntryPointsExtension() throws CoreException {
    IPluginExtension extension = createExtension( IXmlNames.XID_ENTRYPOINT,
                                                  true );
    IPluginElement element = createElement( extension );
    element.setName( IXmlNames.ELEM_ENTRYPOINT );
    element.setAttribute( IXmlNames.ATT_CLASS, 
                          getPackageName() + "." + getApplicationName() ); //$NON-NLS-1$
    element.setAttribute( IXmlNames.ATT_ID,
                          getPackageName() + "." + getApplicationName() ); //$NON-NLS-1$
    element.setAttribute( IXmlNames.ATT_PARAMETER, "hello" ); //$NON-NLS-1$
    extension.add( element );
    addExtensionToPlugin( extension );
  }

  private void createPerspectivesExtension() throws CoreException {
    IPluginExtension extension 
      = createExtension( IXmlNames.XID_PERSPECTIVES, true );
    
    IPluginElement element = createElement( extension );
    element.setName( IXmlNames.ELEM_PERSPECTIVE );
    element.setAttribute( IXmlNames.ATT_CLASS, 
                          getPackageName() + ".Perspective" ); //$NON-NLS-1$
    element.setAttribute( IXmlNames.ATT_NAME,
                          Messages.helloRAPTemplate_perspectiveName );
    element.setAttribute( IXmlNames.ATT_ID, 
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
