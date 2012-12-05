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
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Shell;


public class SearchText_Test extends TestCase {
  
  private Shell shell;

  public void testGetTextIsTrimmed() {
    SearchText searchText = new SearchText( shell, "", "", 0 );
    
    String text = "text";
    searchText.setText( text + "  " );
    
    assertEquals( text, searchText.getText() );
  }
  
  protected void setUp() throws Exception {
    shell = new Shell();
  }
  
  protected void tearDown() throws Exception {
    shell.dispose();
  }
}
