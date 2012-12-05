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

import junit.framework.TestCase;


public class ValidationRunner_Test extends TestCase {
  
  public void testGetErrorsBeforeValidating() {
    ValidationRunner validationRunner = new ValidationRunner( null );
    assertNotNull( validationRunner.getErrors() );
  }

  public void testGetWarningsBeforeValidating() {
    ValidationRunner validationRunner = new ValidationRunner( null );
    assertNotNull( validationRunner.getWarnings() );
  }
}
