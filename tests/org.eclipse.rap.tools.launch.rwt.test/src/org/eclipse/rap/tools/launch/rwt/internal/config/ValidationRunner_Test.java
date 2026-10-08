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

import static org.junit.Assert.assertNotNull;

import org.eclipse.rap.tools.launch.rwt.internal.config.ValidationRunner;
import org.junit.Test;


public class ValidationRunner_Test {

  @Test
  public void testGetErrors_beforeValidating() {
    ValidationRunner validationRunner = new ValidationRunner( null );

    assertNotNull( validationRunner.getErrors() );
  }

  @Test
  public void testGetWarnings_beforeValidating() {
    ValidationRunner validationRunner = new ValidationRunner( null );

    assertNotNull( validationRunner.getWarnings() );
  }

}
