/*******************************************************************************
 * Copyright (c) 2017 EclipseSource and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.templates.internal.rap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.rap.tools.templates.internal.XmlNames;

class E4RAPTemplate extends AbstractRAPTemplate {

  E4RAPTemplate() {
    setPageCount( 1 );
    createTemplateOptions();
  }

  public void addPages( Wizard wizard ) {
    WizardPage page = createPage( 0 );
    page.setTitle( Messages.e4RAPTemplate_pageTitle );
    page.setDescription( Messages.e4RAPTemplate_pageDescr );
    wizard.addPage( page );
    markPagesAdded();
  }

  public String getSectionId() {
    return "e4RAP"; //$NON-NLS-1$
  }

  public String getApplicationId() {
    return "e4app";
  }

  public String getServletPath() {
    return "/e4";
  }

  @Override
  protected void updateModel( IProgressMonitor monitor ) throws CoreException {
    createE4ApplicationExtension();
  }

  private void createE4ApplicationExtension() throws CoreException {
    IPluginExtension extension = createExtension( XmlNames.XID_PRODUCT, true );
    extension.setId( getFullApplicationId() );
    IPluginElement applicationElement = createElement( extension );
    applicationElement.setName( XmlNames.ELEM_PRODUCT );
    applicationElement.setAttribute( XmlNames.ATT_APPLICATION, "org.eclipse.e4.ui.workbench.swt.E4Application" ); //$NON-NLS-1$
    applicationElement.setAttribute( XmlNames.ATT_NAME, "RAP E4 Application" ); //$NON-NLS-1$
    extension.add( applicationElement );
    addExtensionToPlugin( extension );
  }

  private void createTemplateOptions() {
    addOption( KEY_WINDOW_TITLE,
               Messages.e4RAPTemplate_windowTitle,
               Messages.e4RAPTemplate_appWindowTitle,
               0 );
    addOption( KEY_PACKAGE_NAME, Messages.e4RAPTemplate_packageName, null, 0 );
  }

}
