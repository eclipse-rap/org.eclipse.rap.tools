/*******************************************************************************
 * Copyright (c) 2007, 2014 EclipseSource and others
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

public class HelloRAPWizard extends AbstractRAPWizard {

  private AbstractRAPTemplate template;

  public void init( IFieldData data ) {
    super.init( data );
    setWindowTitle( Messages.helloRAPWizard_windowTitle );
  }

  public ITemplateSection[] createTemplateSections() {
    template = new HelloRAPTemplate();
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
    return "org.eclipse.rap.rwt;bundle-version=\"[2.0.0,4.0.0)\"";
  }

  @Override
  protected String getActivatorName() {
    return template.getActivatorName();
  }

  @Override
  protected boolean shouldModifyActivator() {
    return true;
  }

  @Override
  protected boolean shouldModifyBuildProperties() {
    return true;
  }

}
