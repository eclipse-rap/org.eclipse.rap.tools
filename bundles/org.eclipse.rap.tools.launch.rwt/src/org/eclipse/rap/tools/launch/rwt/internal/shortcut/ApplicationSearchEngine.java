/*******************************************************************************
 * Copyright (c) 2011, 2014 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.launch.rwt.internal.shortcut;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.rap.tools.launch.rwt.internal.shortcut.RunnableContextHelper.IContextRunnable;


class ApplicationSearchEngine {

  private static final int SCOPE_CONSTRAINTS
    = IJavaSearchScope.SOURCES
    | IJavaSearchScope.APPLICATION_LIBRARIES
    | IJavaSearchScope.REFERENCED_PROJECTS;

  private final RunnableContextHelper runnableContextHelper;
  private final ApplicationCollector collector;
  private IJavaSearchScope searchScope;


  ApplicationSearchEngine( IRunnableContext runnableContext ) {
    runnableContextHelper = new RunnableContextHelper( runnableContext );
    collector = new ApplicationCollector();
  }

  IType[] search( IJavaElement[] javaElements ) throws CoreException, InterruptedException {
    searchScope = SearchEngine.createJavaSearchScope( javaElements, SCOPE_CONSTRAINTS );
    collector.clear();
    IContextRunnable contextRunnable = new IContextRunnable() {
      public void run( IProgressMonitor monitor ) throws Exception {
        search( monitor );
      }
    };
    runnableContextHelper.runInContext( contextRunnable );
    return collector.getResult();
  }

  private void search( IProgressMonitor monitor ) throws CoreException {
    monitor.beginTask( "Searching for entry points or application configurations...", 100 );
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
      SearchPattern pattern3
        = SearchPattern.createPattern( "configure( Application ) void", //$NON-NLS-1$
                                       IJavaSearchConstants.METHOD,
                                       IJavaSearchConstants.DECLARATIONS,
                                       matchRule );
      SearchPattern pattern1or2 = SearchPattern.createOrPattern( pattern1, pattern2 );
      SearchPattern pattern = SearchPattern.createOrPattern( pattern3, pattern1or2 );
      searchEngine.search( pattern, participants, searchScope, collector, searchMonitor );
    } finally {
      monitor.done();
    }
  }

  private static SearchParticipant[] getSearchParticipants() {
    return new SearchParticipant[]{ SearchEngine.getDefaultSearchParticipant() };
  }

  private static class ApplicationCollector extends SearchRequestor {

    private final List<IType> collectedTypes;

    public ApplicationCollector() {
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
        TypeInspector inspector = new TypeInspector( type );
        if( inspector.isEntryPointType() || inspector.isApplicationConfigurationType() ) {
          collectedTypes.add( type );
        }
      }
    }

  }

}
