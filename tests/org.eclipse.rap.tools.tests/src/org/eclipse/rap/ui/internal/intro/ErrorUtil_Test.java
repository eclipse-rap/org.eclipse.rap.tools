/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.eclipse.core.runtime.IStatus;
import org.junit.Test;


public class ErrorUtil_Test {

  private static final String EXCEPTION_MESSAGE = "exception-message";
  private static final String STATUS_MESSAGE = "status-message";

  @Test
  public void testCreateErrorStatus_withNullThrowable() {
    IStatus status = ErrorUtil.createErrorStatus( STATUS_MESSAGE, null );

    assertEquals( IStatus.ERROR, status.getSeverity() );
    assertEquals( STATUS_MESSAGE, status.getMessage() );
    assertNull( status.getException() );
  }

  @Test
  public void testCreateErrorStatus_withThrowable() {
    Exception exception = new Exception( EXCEPTION_MESSAGE );

    IStatus status = ErrorUtil.createErrorStatus( STATUS_MESSAGE, exception );

    assertEquals( IStatus.ERROR, status.getSeverity() );
    assertEquals( STATUS_MESSAGE, status.getMessage() );
    assertSame( exception, status.getException() );
  }

  @Test
  public void testCreateErrorStatus_withAllNull() {
    IStatus status = ErrorUtil.createErrorStatus( null, null );

    assertEquals( IStatus.ERROR, status.getSeverity() );
    assertEquals( "", status.getMessage() );
    assertNull( status.getException() );
  }

}
