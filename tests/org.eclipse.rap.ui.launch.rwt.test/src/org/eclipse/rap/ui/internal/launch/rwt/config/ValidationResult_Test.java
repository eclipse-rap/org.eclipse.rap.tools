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

import org.eclipse.core.runtime.IStatus;

import junit.framework.TestCase;


public class ValidationResult_Test extends TestCase {
  
  private ValidationResult validationResult;

  public void testGetAllWhenEmpty() {
    IStatus[] all = validationResult.getAll();
    assertEquals( 0, all.length );
  }
  
  public void testGetAll() {
    validationResult.addError( "message", 4711 );
    validationResult.addWarning( "message", 4711 );
    IStatus[] all = validationResult.getAll();
    assertEquals( 2, all.length );
  }
  
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
  
  public void testAddErrorWithNullMessage() {
    try {
      validationResult.addError( null, 0 );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testAddWarningWithNullMessage() {
    try {
      validationResult.addWarning( null, 0 );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    validationResult = new ValidationResult();
  }
}
