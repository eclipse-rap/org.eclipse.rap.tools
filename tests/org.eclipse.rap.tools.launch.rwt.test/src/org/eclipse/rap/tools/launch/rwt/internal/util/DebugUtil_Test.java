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
package org.eclipse.rap.tools.launch.rwt.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.rap.tools.launch.rwt.internal.config.RWTLaunchConfig;
import org.eclipse.rap.tools.launch.rwt.internal.tests.Fixture;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestLaunch;
import org.eclipse.rap.tools.launch.rwt.internal.tests.TestProcess;
import org.eclipse.rap.tools.launch.rwt.internal.util.DebugUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DebugUtil_Test {

  private List<RuntimeProcess> runtimeProcesses;

  @Before
  public void setUp() throws Exception {
    runtimeProcesses = new LinkedList<RuntimeProcess>();
  }

  @After
  public void tearDown() throws Exception {
    Iterator iter = runtimeProcesses.iterator();
    while( iter.hasNext() ) {
      RuntimeProcess runtimeProcess = ( RuntimeProcess )iter.next();
      runtimeProcess.terminate();
    }
    Fixture.deleteAllRWTLaunchConfigs();
  }

  @Test
  public void testGetLaunchName() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = Fixture.createRWTLaunchConfig();
    ILaunch launch = new TestLaunch( launchConfig );

    String launchName = DebugUtil.getLaunchName( launch );

    assertEquals( launchConfig.getName(), launchName );
  }

  @Test
  public void testGetLaunchName_whenLaunchConfigIsNull() {
    ILaunch launch = new TestLaunch( null );

    String launchName = DebugUtil.getLaunchName( launch );

    assertNull( launchName );
  }

  @Test
  public void testContainsCreateEventFor_withCreateEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.CREATE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };

    boolean containsCreateEvent = DebugUtil.containsCreateEventFor( debugEvents, launch );

    assertTrue( containsCreateEvent );
  }

  @Test
  public void testContainsCreateEventFor_withCreateEventAndOtherEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent otherEvent = new DebugEvent( new Object(), DebugEvent.CREATE );
    DebugEvent createEvent = new DebugEvent( runtimeProcess, DebugEvent.CREATE );
    DebugEvent[] debugEvents = new DebugEvent[] { otherEvent, createEvent };

    boolean containsCreateEvent = DebugUtil.containsCreateEventFor( debugEvents, launch );

    assertTrue( containsCreateEvent );
  }

  @Test
  public void testContainsCreateEventFor_withTerminateEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.TERMINATE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };

    boolean containsCreateEvent = DebugUtil.containsCreateEventFor( debugEvents, launch );

    assertFalse( containsCreateEvent );
  }

  @Test
  public void testContainsCreateEventFor_withCreateEventFromDifferentLaunch() throws Exception {
    ILaunch launch = createLaunch();
    ILaunch otherLaunch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( otherLaunch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.CREATE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };

    boolean containsCreateEvent = DebugUtil.containsCreateEventFor( debugEvents, launch );

    assertFalse( containsCreateEvent );
  }

  @Test
  public void testContainsTerminateEventFor_withTerminateEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.TERMINATE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };

    boolean containsTerminateEvent = DebugUtil.containsTerminateEventFor( debugEvents, launch );

    assertTrue( containsTerminateEvent );
  }

  @Test
  public void testContainsTerminateEventFor_withTerminateEventAndOtherEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent otherEvent = new DebugEvent( new Object(), DebugEvent.CREATE );
    DebugEvent terminateEvent = new DebugEvent( runtimeProcess, DebugEvent.TERMINATE );
    DebugEvent[] debugEvents = new DebugEvent[] { otherEvent, terminateEvent };

    boolean containsTerminateEvent = DebugUtil.containsTerminateEventFor( debugEvents, launch );

    assertTrue( containsTerminateEvent );
  }

  @Test
  public void testContainsTerminateEventFor_withDifferentEvent() throws Exception {
    ILaunch launch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( launch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.CHANGE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };

    boolean containsTerminateEvent = DebugUtil.containsTerminateEventFor( debugEvents, launch );

    assertFalse( containsTerminateEvent );
  }

  @Test
  public void testContainsTerminateEventFor_withTerminateEventFromDifferentLaunch()
    throws Exception
  {
    ILaunch launch = createLaunch();
    ILaunch otherLaunch = createLaunch();
    RuntimeProcess runtimeProcess = createRuntimeProcess( otherLaunch );
    DebugEvent debugEvent = new DebugEvent( runtimeProcess, DebugEvent.TERMINATE );
    DebugEvent[] debugEvents = new DebugEvent[] { debugEvent };

    boolean containsTerminateEvent = DebugUtil.containsTerminateEventFor( debugEvents, launch );

    assertFalse( containsTerminateEvent );
  }

  private RuntimeProcess createRuntimeProcess( ILaunch launch ) {
    HashMap<String, String> attributes = new HashMap<String, String>();
    RuntimeProcess result = new RuntimeProcess( launch, new TestProcess(), "", attributes );
    runtimeProcesses.add( result );
    return result;
  }

  private static ILaunch createLaunch() throws CoreException {
    RWTLaunchConfig launchConfig = new RWTLaunchConfig( Fixture.createRWTLaunchConfig() );
    return new TestLaunch( launchConfig.getUnderlyingLaunchConfig() );
  }

}
