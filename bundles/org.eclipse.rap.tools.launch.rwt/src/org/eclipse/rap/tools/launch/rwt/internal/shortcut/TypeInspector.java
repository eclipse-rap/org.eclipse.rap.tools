/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.shortcut;

import org.eclipse.jdt.core.*;
import org.eclipse.rap.tools.launch.rwt.internal.util.StringArrays;


class TypeInspector {

  private static final String[] NO_PARAMETERS = new String[ 0 ];
  private static final String[] COMPOSITE_PARAMETER = new String[] { "QComposite;" }; //$NON-NLS-1$
  private static final String[] APPLICATION_PARAMETER = new String[] { "QApplication;" }; //$NON-NLS-1$

  private final IType type;

  TypeInspector( IType type ) {
    this.type = type;
  }

  boolean isEntryPointType() throws JavaModelException {
    boolean result = false;
    if( type.isClass() && !isAbstract() ) {
      if( implementsEntryPoint() ) {
        result = hasCreateUIMethod();
      } else if( extendsAbstractEntryPoint() ) {
        result = hasCreateContentsMethod();
      }
    }
    return result;
  }

  boolean isApplicationConfigurationType() throws JavaModelException {
    return    type.isClass()
           && !isAbstract()
           && implementsApplicationConfiguration()
           && hasConfigureMethod();
  }

  private boolean implementsEntryPoint() throws JavaModelException {
    String[] superInterfaceNames = type.getSuperInterfaceNames();
    return    StringArrays.contains( superInterfaceNames, "EntryPoint" ) //$NON-NLS-1$
           || StringArrays.contains( superInterfaceNames, "IEntryPoint" ); //$NON-NLS-1$
  }

  private boolean hasCreateUIMethod() {
    IMethod method = type.getMethod( "createUI", NO_PARAMETERS ); //$NON-NLS-1$
    return method.exists();
  }

  private boolean extendsAbstractEntryPoint() throws JavaModelException {
    String superClassName = type.getSuperclassName();
    return "AbstractEntryPoint".equals( superClassName ); //$NON-NLS-1$
  }

  private boolean hasCreateContentsMethod() {
    IMethod method = type.getMethod( "createContents", COMPOSITE_PARAMETER ); //$NON-NLS-1$
    return method.exists();
  }

  private boolean isAbstract() throws JavaModelException {
    return Flags.isAbstract( type.getFlags() );
  }

  private boolean implementsApplicationConfiguration() throws JavaModelException {
    String[] superInterfaceNames = type.getSuperInterfaceNames();
    return StringArrays.contains( superInterfaceNames, "ApplicationConfiguration" ); //$NON-NLS-1$
  }

  private boolean hasConfigureMethod() {
    IMethod method = type.getMethod( "configure", APPLICATION_PARAMETER ); //$NON-NLS-1$
    return method.exists();
  }

}
