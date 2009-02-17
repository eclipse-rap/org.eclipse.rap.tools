/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.tab;


/**
 * Abstract base class that should be used to represent an extension with the 
 * project it is contained in. 
 */
abstract class AbstractExtension {

  final String project;
  
  AbstractExtension( final String project ) {
    this.project = project;
  }

  /////////
  // Getter

  final String getProject() {
    return project;
  }
}
