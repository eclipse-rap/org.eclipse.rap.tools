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
package org.eclipse.rap.ui.internal.intro.target;

import junit.framework.TestCase;


public class TargetProvider_Test extends TestCase {
  
  public void testGetRAPRuntimeVersion() {
    String version = TargetProvider.getRAPRuntimeVersion();
    assertNotNull( version );
    assertTrue( version.length() > 0 );
  }
  
  public void testDefaultTargetDestination() {
    String defaultDestination = TargetProvider.getDefaultTargetDestination();
    assertNotNull( defaultDestination );
    assertTrue( defaultDestination.length() > 0 );
  }
}
