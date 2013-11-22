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
package org.eclipse.rap.ui.internal.launch.rwt.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class TestProcess extends Process {

  private final InputStream errorStream;
  private final InputStream inputStream;
  private final OutputStream outputStream;

  public TestProcess() {
    errorStream = createInputStream();
    inputStream = createInputStream();
    outputStream = new ByteArrayOutputStream();
  }

  @Override
  public InputStream getErrorStream() {
    return errorStream;
  }

  @Override
  public InputStream getInputStream() {
    return inputStream;
  }

  @Override
  public OutputStream getOutputStream() {
    return outputStream;
  }

  @Override
  public int waitFor() throws InterruptedException {
    return 0;
  }

  @Override
  public int exitValue() {
    return 0;
  }

  @Override
  public void destroy() {
  }

  private static InputStream createInputStream() {
    return new ByteArrayInputStream( new byte[ 0 ] );
  }

}
