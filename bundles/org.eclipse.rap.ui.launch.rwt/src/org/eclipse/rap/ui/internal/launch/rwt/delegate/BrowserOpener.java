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
package org.eclipse.rap.ui.internal.launch.rwt.delegate;

import java.net.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.*;
import org.eclipse.rap.ui.internal.launch.rwt.config.BrowserMode;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.util.DebugUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.statushandlers.StatusManager;

class BrowserOpener {
  private static final int CONNECT_TIMEOUT = 20000; // 20 Seconds

  private final ILaunch launch;
  private final RWTLaunchConfig launchConfig;
  private final URL url;

  BrowserOpener( ILaunch launch, String url ) {
    this.launch = launch;
    this.launchConfig = new RWTLaunchConfig( launch.getLaunchConfiguration() );
    this.url = toURL( url );
  }
  
  void scheduleOpen() {
    DebugPlugin.getDefault().addDebugEventListener( new IDebugEventSetListener() {
      public void handleDebugEvents( DebugEvent[] events ) {
        if( DebugUtil.containsCreateEventFor( events, launch ) ) {
          DebugPlugin.getDefault().removeDebugEventListener( this );
          // Start a separate job to wait for the servlet engine and launch  the browser. 
          // Otherwise we would block the application on whose service we are waiting for
          scheduleOpenJob();
        } else if( DebugUtil.containsTerminateEventFor( events, launch ) ) {
          DebugPlugin.getDefault().removeDebugEventListener( this );
        }
      }
    } );
  }

  private void scheduleOpenJob() {
    final String taskName = "Starting client application";
    Job job = new Job( taskName ) {
      protected IStatus run( IProgressMonitor monitor ) {
        monitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
        try {
          waitForServletEngine( monitor );
          open( monitor );
        } finally {
          monitor.done();
        }
        return Status.OK_STATUS;
      }
    };
    job.schedule();
  }

  private void open( IProgressMonitor monitor ) {
    SubProgressMonitor subMonitor = new SubProgressMonitor( monitor, 1 );
    String taskName = "Starting client application";
    subMonitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
    try {
      openUrl();
    } finally {
      subMonitor.done();
    }
  }

  private void openUrl() {
    Display.getDefault().syncExec( new Runnable() {
      public void run() {
        if( !launch.isTerminated() ) {
          try {
            IWebBrowser browser = getBrowser();
            browser.openURL( url );
          } catch( CoreException ce ) {
            handleCoreException( ce );
          }
        }
      }
    } );
  }

  IWebBrowser getBrowser() throws CoreException {
    int style =   IWorkbenchBrowserSupport.LOCATION_BAR
                | IWorkbenchBrowserSupport.NAVIGATION_BAR
                | IWorkbenchBrowserSupport.STATUS;
    if( BrowserMode.EXTERNAL.equals( launchConfig.getBrowserMode() ) ) {
      style |= IWorkbenchBrowserSupport.AS_EXTERNAL;
    } else {
      style |= IWorkbenchBrowserSupport.AS_EDITOR;
    }
    // Starting the same launch first with the external, then with the
    // internal browser without restarting the workbench will still open
    // an external browser.
    // The fix is to append the browserMode to the id
    String id = launchConfig.getName() + launchConfig.getBrowserMode();
    String name = launchConfig.getName();
    String toolTip = launchConfig.getName();
    IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
    return support.createBrowser( style, id, name, toolTip );
  }

  private void waitForServletEngine( IProgressMonitor monitor ) {
    SubProgressMonitor subMonitor = new SubProgressMonitor( monitor, 1 );
    String taskName = "Waiting for servlet engine";
    subMonitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
    try {
      long start = System.currentTimeMillis();
      boolean isReady = false;
      while(    System.currentTimeMillis() - start <= CONNECT_TIMEOUT 
             && !isReady
             && !Thread.interrupted()
             && !monitor.isCanceled()
             && !launch.isTerminated() ) 
      {
        isReady = canConnectToUrl(); 
        if( !isReady ) {
          // http service not yet started - wait a bit
          try {
            Thread.sleep( 200 );
          } catch( InterruptedException ie ) {
            // handled in while condition
          }
        }
      }
    } finally {
      subMonitor.done();
    }
  }

  boolean canConnectToUrl() {
    boolean result = false;
    try {
      Socket socket = new Socket( url.getHost(), url.getPort() );
      socket.close();
      result = true;
    } catch( Exception e ) {
    }
    return result;
  }
  
  private static void handleCoreException( CoreException coreException ) {
    IStatus status = coreException.getStatus();
    StatusManager.getManager().handle( status, StatusManager.SHOW );
  }

  private static URL toURL( String urlString ) {
    try {
      return new URL( urlString );
    } catch( MalformedURLException e ) {
      String msg = "Failed to create URL from string: " + urlString; //$NON-NLS-1$
      throw new RuntimeException( msg, e );
    }
  }
}
