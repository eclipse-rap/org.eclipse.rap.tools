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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;


final class RunnableContextHelper {
  
  interface IContextRunnable {
    void run( IProgressMonitor monitor ) throws Exception;
  }
  
  private final IRunnableContext context;

  RunnableContextHelper( IRunnableContext context ) {
    this.context = context;
  }
  
  void runInContext( IContextRunnable contextRunnable ) throws CoreException, InterruptedException {
    IRunnableWithProgress progressRunnable = wrapContextRunnable( contextRunnable );
    runInContext( progressRunnable );
  }

  private void runInContext( IRunnableWithProgress progressRunnable ) 
    throws CoreException, InterruptedException 
  {
    try {
      context.run( true, true, progressRunnable );
    } catch( InvocationTargetException ite ) {
      handleInvocationTargetException( ite );
    } 
  }

  private static void handleInvocationTargetException( InvocationTargetException ite ) 
    throws CoreException, InterruptedException
  {
    if( ite.getCause() instanceof CoreException ) {
      throw ( CoreException )ite.getCause();
    }
    if( ite.getCause() instanceof InterruptedException ) {
      throw ( InterruptedException )ite.getCause();
    }
    if( ite.getCause() instanceof RuntimeException ) {
      throw ( RuntimeException )ite.getCause();
    } 
    throw new RuntimeException( ite.getCause() );
  }

  private static IRunnableWithProgress wrapContextRunnable( IContextRunnable contextRunnable ) {
    return new ContextRunnableWrapper( contextRunnable );
  }

  private static final class ContextRunnableWrapper implements IRunnableWithProgress {
  
    private final IContextRunnable contextRunnable;
  
    private ContextRunnableWrapper( IContextRunnable contextRunnable ) {
      this.contextRunnable = contextRunnable;
    }
  
    public void run( IProgressMonitor monitor ) throws InvocationTargetException {
      try {
        contextRunnable.run( monitor );
      } catch( Exception e ) {
        throw new InvocationTargetException( e );
      }
    }
  }
}
