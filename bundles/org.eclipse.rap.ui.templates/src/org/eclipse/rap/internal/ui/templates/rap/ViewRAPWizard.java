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

import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;

public class ViewRAPWizard extends AbstractRAPWizard {

  private AbstractRAPTemplate template;

  public void init( final IFieldData data ) {
    super.init( data );
    setWindowTitle( Messages.viewRAPWizard_windowTitle );
  }

  public ITemplateSection[] createTemplateSections() {
    template = new ViewRAPTemplate();
    return new ITemplateSection[] {
      template
    };
  }

  protected String getEntryPointName() {
    return template.getFullApplicationId();
  }

  protected String getServletName() {
    return "view";
  }
    
}
