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
package org.eclipse.rap.ui.internal.launch.rwt.shortcut;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.rap.ui.internal.launch.rwt.shortcut.RunnableContextHelper.IContextRunnable;
import org.eclipse.rap.ui.internal.launch.rwt.util.StringArrays;


class EntryPointSearchEngine {

  private static final int SCOPE_CONSTRAINTS
    = IJavaSearchScope.SOURCES
    | IJavaSearchScope.APPLICATION_LIBRARIES
    | IJavaSearchScope.REFERENCED_PROJECTS;

  private final RunnableContextHelper runnableContextHelper;
  private final EntryPointCollector entryPointCollector;
  private IJavaSearchScope searchScope;


  EntryPointSearchEngine( IRunnableContext runnableContext ) {
    runnableContextHelper = new RunnableContextHelper( runnableContext );
    entryPointCollector = new EntryPointCollector();
  }

  IType[] search( IJavaElement[] javaElements ) throws CoreException, InterruptedException {
    searchScope = SearchEngine.createJavaSearchScope( javaElements, SCOPE_CONSTRAINTS );
    entryPointCollector.clear();
    IContextRunnable contextRunnable = new IContextRunnable() {
      public void run( IProgressMonitor monitor ) throws Exception {
        search( monitor );
      }
    };
    runnableContextHelper.runInContext( contextRunnable );
    return entryPointCollector.getResult();
  }

  private void search( IProgressMonitor monitor ) throws CoreException {
    monitor.beginTask( "Searching for entry points...", 100 );
    try {
      int matchRule = SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE;
      SearchParticipant[] participants = getSearchParticipants();
      IProgressMonitor searchMonitor = new SubProgressMonitor( monitor, 100 );
      SearchEngine searchEngine = new SearchEngine();
      SearchPattern pattern1
        = SearchPattern.createPattern( "createUI() int", //$NON-NLS-1$
                                       IJavaSearchConstants.METHOD,
                                       IJavaSearchConstants.DECLARATIONS,
                                       matchRule );
      SearchPattern pattern2
        = SearchPattern.createPattern( "createContents( Composite ) void", //$NON-NLS-1$
                                       IJavaSearchConstants.METHOD,
                                       IJavaSearchConstants.DECLARATIONS,
                                       matchRule );
      SearchPattern pattern = SearchPattern.createOrPattern( pattern1, pattern2 );
      searchEngine.search( pattern, participants, searchScope, entryPointCollector, searchMonitor );
    } finally {
      monitor.done();
    }
  }

  private static SearchParticipant[] getSearchParticipants() {
    return new SearchParticipant[]{ SearchEngine.getDefaultSearchParticipant() };
  }

  private static class EntryPointCollector extends SearchRequestor {
    private final List<IType> collectedTypes;

    public EntryPointCollector() {
      collectedTypes = new LinkedList<IType>();
    }

    void clear() {
      collectedTypes.clear();
    }

    IType[] getResult() {
      return collectedTypes.toArray( new IType[ collectedTypes.size() ] );
    }

    public void acceptSearchMatch( SearchMatch match ) throws CoreException {
      Object enclosingElement = match.getElement();
      if( enclosingElement instanceof IMethod ) {
        IMethod method = ( IMethod )enclosingElement;
        IType type = method.getDeclaringType();
        if( isEntryPointType( type ) ) {
          collectedTypes.add( type );
        }
      }
    }

    private static boolean isEntryPointType( IType type ) throws JavaModelException {
      return new TypeInspector( type ).isEntryPointType();
    }
  }

  private static class TypeInspector {
    private static final String[] NO_PARAMETERS = new String[ 0 ];
    private static final String[] COMPOSITE_PARAMETER = new String[] { "QComposite;" }; //$NON-NLS-1$

    private final IType type;

    TypeInspector( IType type ) {
      this.type = type;
    }

    boolean isEntryPointType() throws JavaModelException {
      boolean result = false;
      if( type.isClass() ) {
        if( implementsEntryPoint() ) {
          result = hasCreateUIMethod() && !isAbstract();
        } else if( extendsAbstractEntryPoint() ) {
          result = hasCreateContentsMethod() && !isAbstract();
        }
      }
      return result;
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
  }
}
