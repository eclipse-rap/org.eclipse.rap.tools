/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.config;

abstract class Validator {

  final RWTLaunchConfig config;
  private final ValidationResult validationResult;

  Validator( RWTLaunchConfig config, ValidationResult validationResult ) {
    this.config = config;
    this.validationResult = validationResult;
  }

  abstract void validate();

  final void addError( String message, int code ) {
    validationResult.addError( message, code );
  }
  
  final void addWarning( String message, int code ) {
    validationResult.addWarning( message, code );
  }
}
