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

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.rap.ui.internal.launch.rwt.RWTLaunchActivator;
import org.eclipse.rap.ui.internal.launch.rwt.shortcut.RunnableContextHelper.IContextRunnable;


public class RunnableContextHelper_Test extends TestCase {
  
  private RunnableContextHelper runnableContextHelper;

  public void testRunInContextWithNormalFlow() throws Exception {
    final boolean[] wasInvoked = { false };
    IContextRunnable contextRunnable = new IContextRunnable() {
      public void run( IProgressMonitor monitor ) throws Exception {
        wasInvoked[ 0 ] = true;
      }
    };
    
    runnableContextHelper.runInContext( contextRunnable );
    
    assertTrue( wasInvoked[ 0 ] );
  }
  
  public void testRunInContextWithCheckedException() throws Exception {
    final String exceptionMessage = "Bad!";
    IContextRunnable contextRunnable = new IContextRunnable() {
      public void run( IProgressMonitor monitor ) throws Exception {
        throw new Exception( exceptionMessage );
      }
    };
    
    try {
      runnableContextHelper.runInContext( contextRunnable );
      fail();
    } catch( RuntimeException runtimeException ) {
      Throwable cause = runtimeException.getCause();
      assertEquals( exceptionMessage, cause.getMessage() );
      assertEquals( Exception.class, cause.getClass() );
    }
  }
  
  public void testRunInContextWithCoreException() throws Exception {
    final IStatus status = new Status( IStatus.ERROR, RWTLaunchActivator.PLUGIN_ID, "" );
    IContextRunnable contextRunnable = new IContextRunnable() {
      public void run( IProgressMonitor monitor ) throws Exception {
        throw new CoreException( status );
      }
    };
    
    try {
      runnableContextHelper.runInContext( contextRunnable );
      fail();
    } catch( CoreException coreException ) {
      assertSame( status, coreException.getStatus() );
    }
  }
  
  public void testRunInContextWithInterruptedException() throws Exception {
    IContextRunnable contextRunnable = new IContextRunnable() {
      public void run( IProgressMonitor monitor ) throws Exception {
        throw new InterruptedException();
      }
    };
    
    try {
      runnableContextHelper.runInContext( contextRunnable );
      fail();
    } catch( InterruptedException expected ) {
    }
  }
  
  public void testRunInContextWithUncheckedException() throws Exception {
    final String exceptionMessage = "Bad!";
    IContextRunnable contextRunnable = new IContextRunnable() {
      public void run( IProgressMonitor monitor ) throws Exception {
        throw new RuntimeException( exceptionMessage );
      }
    };
    
    try {
      runnableContextHelper.runInContext( contextRunnable );
      fail();
    } catch( RuntimeException re ) {
      assertEquals( exceptionMessage, re.getMessage() );
      assertEquals( RuntimeException.class, re.getClass() );
    }
  }
  
  protected void setUp() throws Exception {
    IRunnableContext context = new IRunnableContext() {
      public void run( boolean fork,
                       boolean cancelable,
                       IRunnableWithProgress runnable )
        throws InvocationTargetException, InterruptedException
      {
        runnable.run( new NullProgressMonitor() );
      }
    };
    
    runnableContextHelper = new RunnableContextHelper( context );
  }
}
