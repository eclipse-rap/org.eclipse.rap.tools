/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.config;

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
