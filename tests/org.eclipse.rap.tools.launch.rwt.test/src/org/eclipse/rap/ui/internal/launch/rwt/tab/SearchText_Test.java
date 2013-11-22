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
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SearchText_Test {

  private Shell shell;

  @Before
  public void setUp() throws Exception {
    shell = new Shell();
  }

  @After
  public void tearDown() throws Exception {
    shell.dispose();
  }

  @Test
  public void testGetTextIsTrimmed() {
    SearchText searchText = new SearchText( shell, "", "", 0 );

    String text = "text";
    searchText.setText( text + "  " );

    assertEquals( text, searchText.getText() );
  }

}
