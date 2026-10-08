/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.shortcut;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.rap.tools.launch.rwt.internal.config.ServletPathValidator;
import org.eclipse.rap.tools.launch.rwt.internal.config.ValidationResult;
import org.eclipse.swt.widgets.Shell;


public class ServletPathInputDialog extends InputDialog {

  private static final String TITLE = "Launching RWT Application";
  private static final String MESSAGE
    = "Please enter the servlet path of the entry point you wish to open.";

  public ServletPathInputDialog( Shell parent ) {
    super( parent, TITLE, MESSAGE, "/", createValidator() );
  }

  private static IInputValidator createValidator() {
    return new IInputValidator() {
      public String isValid( String text ) {
        ValidationResult validationResult = new ValidationResult();
        new ServletPathValidator( text, validationResult ).validate();
        IStatus[] errors = validationResult.getErrors();
        return errors.length > 0 ? errors[ 0 ].getMessage() : null;
      }
    };
  }

}
