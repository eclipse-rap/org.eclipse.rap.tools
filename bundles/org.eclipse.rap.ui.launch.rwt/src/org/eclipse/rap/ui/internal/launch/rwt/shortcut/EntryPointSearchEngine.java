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
      SearchPattern pattern
        = SearchPattern.createPattern( "createUI() int",  //$NON-NLS-1$
                                       IJavaSearchConstants.METHOD, 
                                       IJavaSearchConstants.DECLARATIONS, 
                                       matchRule );
      SearchParticipant[] participants = getSearchParticipants();
      IProgressMonitor searchMonitor = new SubProgressMonitor( monitor, 100 );
      SearchEngine searchEngine = new SearchEngine();
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
        if( isIEntryPointType( type ) ) {
          collectedTypes.add( type );
        }
      }
    }
    
    private static boolean isIEntryPointType( IType type ) throws JavaModelException {
      return new TypeInspector( type ).isIEntryPointType();
    }
  }

  private static class TypeInspector {
    private static final String[] NO_PARAMETERS = new String[ 0 ];
    
    private final IType type;

    TypeInspector( IType type ) {
      this.type = type;
    }
    
    boolean isIEntryPointType() throws JavaModelException {
      return type.isClass() && implementsIEntryPoint() && hasCreateUIMethod();
    }

    private boolean hasCreateUIMethod() {
      IMethod method = type.getMethod( "createUI", NO_PARAMETERS ); //$NON-NLS-1$
      return method.exists();
    }
    
    private boolean implementsIEntryPoint() throws JavaModelException {
      String[] superInterfaceNames = type.getSuperInterfaceNames();
      return StringArrays.contains( superInterfaceNames, "IEntryPoint" ); //$NON-NLS-1$
    }
  }
}
