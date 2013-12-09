/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.rap.tools.launch.rwt.internal.config.ValidationResult;
import org.junit.Before;
import org.junit.Test;


public class ValidationResult_Test {

  private ValidationResult validationResult;

  @Before
  public void setUp() throws Exception {
    validationResult = new ValidationResult();
  }

  @Test
  public void testGetAll_whenEmpty() {
    IStatus[] all = validationResult.getAll();

    assertEquals( 0, all.length );
  }

  @Test
  public void testGetAll() {
    validationResult.addError( "message", 4711 );
    validationResult.addWarning( "message", 4711 );

    IStatus[] all = validationResult.getAll();

    assertEquals( 2, all.length );
  }

  @Test
  public void testGetWarnings() {
    String message = "warning";
    int code = 4711;
    validationResult.addWarning( message, code );
    validationResult.addError( "error", 123 );

    IStatus[] warnings = validationResult.getWarnings();

    assertEquals( 1, warnings.length );
    assertEquals( message, warnings[ 0 ].getMessage() );
    assertEquals( code, warnings[ 0 ].getCode() );
    assertTrue( warnings[ 0 ].matches( IStatus.WARNING ) );
  }

  @Test
  public void testGetErrors() {
    String message = "error";
    int code = 4711;
    validationResult.addError( message, code );
    validationResult.addWarning( "warning", 123 );

    IStatus[] errors = validationResult.getErrors();

    assertEquals( 1, errors.length );
    assertEquals( message, errors[ 0 ].getMessage() );
    assertEquals( code, errors[ 0 ].getCode() );
    assertTrue( errors[ 0 ].matches( IStatus.ERROR ) );
  }

  @Test( expected = NullPointerException.class )
  public void testAddError_failsWithNullMessage() {
    validationResult.addError( null, 0 );
  }

  @Test( expected = NullPointerException.class )
  public void testAddWarningWithNullMessage() {
    validationResult.addWarning( null, 0 );
  }

}
