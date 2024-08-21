/*******************************************************************************
 * Copyright (c) 2007, 2017 EclipseSource and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.templates.internal.rap;

import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;

public class MailRAPWizard extends AbstractRAPWizard {

  private static final String LAUNCH_TEMPLATE = "workbench_launch.template"; //$NON-NLS-1$

  private AbstractRAPTemplate template;

  @Override
  public void init( IFieldData data ) {
    super.init( data );
    setWindowTitle( Messages.mailRAPWizard_windowTitle );
  }

  @Override
  public ITemplateSection[] createTemplateSections() {
    template = new MailRAPTemplate();
    return new ITemplateSection[] {
      template
    };
  }

  @Override
  protected String getServletPath() {
    return template.getServletPath();
  }

  @Override
  protected String getPackageName() {
    return template.getPackageName();
  }

  @Override
  protected String getRequireBundles() {
    return "org.eclipse.rap.ui;bundle-version=\"[4.0.0,5.0.0)\"";
  }

  @Override
  protected String getActivatorName() {
    return template.getActivatorName();
  }

  @Override
  protected boolean shouldModifyActivator() {
    return false;
  }

  @Override
  protected boolean shouldModifyBuildProperties() {
    return false;
  }

  @Override
  protected String getLaunchTemplate() {
    return LAUNCH_TEMPLATE;
  }

}
