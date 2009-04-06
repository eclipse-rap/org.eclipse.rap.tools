/*******************************************************************************
 * Copyright (c) 2009 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro;

import org.eclipse.core.runtime.IStatus;

import junit.framework.TestCase;


public class ErrorUtil_Test extends TestCase {
  
  private static final String EXCEPTION_MESSAGE = "exception-message";
  private static final String STATUS_MESSAGE = "status-message";

  public void testCreateErrorStatusWithNullThrowable() {
    IStatus status = ErrorUtil.createErrorStatus( STATUS_MESSAGE, null );
    assertEquals( IStatus.ERROR, status.getSeverity() );
    assertEquals( STATUS_MESSAGE, status.getMessage() );
    assertNull( status.getException() );
  }
  
  public void testCreateErrorStatusWithThrowable() {
    Exception exception = new Exception( EXCEPTION_MESSAGE );
    IStatus status = ErrorUtil.createErrorStatus( STATUS_MESSAGE, exception );
    assertEquals( IStatus.ERROR, status.getSeverity() );
    assertEquals( STATUS_MESSAGE, status.getMessage() );
    assertSame( exception, status.getException() );
  }
  
  public void testCreateErrorStatusWithAllNull() {
    IStatus status = ErrorUtil.createErrorStatus( null, null );
    assertEquals( IStatus.ERROR, status.getSeverity() );
    assertEquals( "", status.getMessage() );
    assertNull( status.getException() );
  }
}
