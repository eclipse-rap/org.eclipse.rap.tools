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
package org.eclipse.rap.ui.internal.launch.rwt.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.tests.Fixture;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestLaunch;
import org.eclipse.rap.ui.internal.launch.rwt.tests.TestProcess;


public class DebugUtil_Test extends TestCase {

  private List runtimeProcesses;
  
  public void testGetLaunchName() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = Fixture.createRWTLaunchConfig();
    ILaunch launch = new TestLaunch( launchConfig );
    
    String launchName = DebugUtil.getLaunchName( launch );
    
    assertEquals( launchConfig.getName(), launchName );
  }
  
  public void testGetLaunchNameWhenLaunchConfigIsNull() {
    ILaunch launch = new TestLaunch( null );
    
    String launchName = DebugUtil.getLaunchName( launch );
    
    assertNull( launchName );
  }
  
  public void testContainsCreateEventWithCreateEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.CREATE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };
    
    boolean containsCreateEvent = DebugUtil.containsCreateEventFor( debugEvents, launch );
    
    assertTrue( containsCreateEvent );
  }
  
  public void testContainsCreateEventWithCreateEventAndOtherEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent otherEvent = new DebugEvent( new Object(), DebugEvent.CREATE );
    DebugEvent createEvent = new DebugEvent( runtimeProcess, DebugEvent.CREATE );
    DebugEvent[] debugEvents = new DebugEvent[] { otherEvent, createEvent };
    
    boolean containsCreateEvent = DebugUtil.containsCreateEventFor( debugEvents, launch );
    
    assertTrue( containsCreateEvent );
  }
  
  public void testContainsCreateEventWithTerminateEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.TERMINATE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };
    
    boolean containsCreateEvent = DebugUtil.containsCreateEventFor( debugEvents, launch );
    
    assertFalse( containsCreateEvent );
  }

  public void testContainsCreateEventWithCreateEventFromDifferentLaunch() throws Exception {
    ILaunch launch = createLaunch();
    ILaunch otherLaunch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( otherLaunch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.CREATE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };
    
    boolean containsCreateEvent = DebugUtil.containsCreateEventFor( debugEvents, launch );
    
    assertFalse( containsCreateEvent );
  }
  
  public void testContainsTerminateEventWithTerminateEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.TERMINATE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };
    
    boolean containsTerminateEvent = DebugUtil.containsTerminateEventFor( debugEvents, launch );
    
    assertTrue( containsTerminateEvent );
  }
  
  public void testContainsTerminateEventWithTerminateEventAndOtherEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent otherEvent = new DebugEvent( new Object(), DebugEvent.CREATE );
    DebugEvent terminateEvent = new DebugEvent( runtimeProcess, DebugEvent.TERMINATE );
    DebugEvent[] debugEvents = new DebugEvent[] { otherEvent, terminateEvent };
    
    boolean containsTerminateEvent = DebugUtil.containsTerminateEventFor( debugEvents, launch );
    
    assertTrue( containsTerminateEvent );
  }
  
  public void testContainsTerminateEventWithDifferentEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.CHANGE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };
    
    boolean containsTerminateEvent = DebugUtil.containsTerminateEventFor( debugEvents, launch );
    
    assertFalse( containsTerminateEvent );
  }
  
  public void testContainsTerminateEventWithTerminateEventFromDifferentLaunch() throws Exception {
    ILaunch launch = createLaunch();
    ILaunch otherLaunch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( otherLaunch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.TERMINATE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };
    
    boolean containsTerminateEvent = DebugUtil.containsTerminateEventFor( debugEvents, launch );
    
    assertFalse( containsTerminateEvent );
  }
  
  protected void setUp() throws Exception {
    runtimeProcesses = new LinkedList();
  }
  
  protected void tearDown() throws Exception {
    Iterator iter = runtimeProcesses.iterator();
    while( iter.hasNext() ) {
      RuntimeProcess runtimeProcess = ( RuntimeProcess )iter.next();
      runtimeProcess.terminate();
    }
    Fixture.deleteAllRWTLaunchConfigs();
  }

  private RuntimeProcess createRuntimeProcess( ILaunch launch ) {
    RuntimeProcess result = new RuntimeProcess( launch, new TestProcess(), "", new HashMap() );
    runtimeProcesses.add( result );
    return result;
  }

  private static ILaunch createLaunch() throws CoreException {
    RWTLaunchConfig launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    return new TestLaunch( launchConfig.getUnderlyingLaunchConfig() );
  }
}
