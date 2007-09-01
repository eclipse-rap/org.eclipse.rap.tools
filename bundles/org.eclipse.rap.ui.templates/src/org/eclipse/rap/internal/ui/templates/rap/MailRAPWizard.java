/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates.rap;

import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;

public class MailRAPWizard extends AbstractRAPWizard {

  public void init( final IFieldData data ) {
    super.init( data );
    setWindowTitle( Messages.mailRAPWizard_windowTitle );
  }

  public ITemplateSection[] createTemplateSections() {
    return new ITemplateSection[]{
      new MailRAPTemplate()
    };
  }

  protected String getEntryPointName() {
    return "mail"; //$NON-NLS-1$
  }
}
