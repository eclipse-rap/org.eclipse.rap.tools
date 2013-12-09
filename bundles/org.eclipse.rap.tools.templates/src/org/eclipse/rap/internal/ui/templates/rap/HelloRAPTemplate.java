/*******************************************************************************
 * Copyright (c) 2007, 2013 EclipseSource and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates.rap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

class HelloRAPTemplate extends AbstractRAPTemplate {

  HelloRAPTemplate() {
    setPageCount( 1 );
    createTemplateOptions();
  }

  public void addPages( Wizard wizard ) {
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

  public String getServletPath() {
    return "/hello";
  }

  @Override
  protected void updateModel( IProgressMonitor monitor ) throws CoreException {
  }

  private void createTemplateOptions() {
    addOption( KEY_WINDOW_TITLE,
               Messages.helloRAPTemplate_windowTitle,
               Messages.helloRAPTemplate_appWindowTitle,
               0 );
    addOption( KEY_PACKAGE_NAME, Messages.helloRAPTemplate_packageName, null, 0 );
  }

}
