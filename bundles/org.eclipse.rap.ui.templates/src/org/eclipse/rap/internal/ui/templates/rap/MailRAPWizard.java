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

public class MailRAPWizard extends AbstractRAPWizard {

  private AbstractRAPTemplate template;
  
  public void init( final IFieldData data ) {
    super.init( data );
    setWindowTitle( Messages.mailRAPWizard_windowTitle );
  }

  public ITemplateSection[] createTemplateSections() {
    template = new MailRAPTemplate();
    return new ITemplateSection[] {
      template
    };
  }

  protected String getEntryPointName() {
    return template.getFullApplicationId();
  }

  protected String getServletName() {
    return "mail";
  }
  
  protected boolean isRapTargetInstallSelected() {
    return template.isRapTargetInstallSelected();
  }
  
}
