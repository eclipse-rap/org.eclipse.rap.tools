/*******************************************************************************
 * Copyright (c) 2005, 2014 Borland Software Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Mickael Istria (EBM Websourcing) - Support for target platform creation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.internal.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.container.ModuleRevision;
import org.eclipse.osgi.internal.framework.EquinoxBundle;
import org.eclipse.osgi.storage.BundleInfo.Generation;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.core.target.LoadTargetDefinitionJob;
import org.eclipse.pde.internal.core.target.TargetPlatformService;
import org.osgi.framework.Bundle;


/**
 * Helper to run unit tests with tycho, where the target platform is empty by default. For the
 * origin of this code, see bug 422952.
 */
@SuppressWarnings( "restriction" )
public class TargetUtil {

  private static final String CORE_RUNTIME = "org.eclipse.core.runtime";
  private static final ITargetPlatformService TP_SERVICE = TargetPlatformService.getDefault();

  /**
   * Loads a target platform that contains all bundles from the running OSGi platform.
   */
  public static void initializeTargetPlatform() throws CoreException {
    ITargetDefinition targetDef = TP_SERVICE.newTarget();
    targetDef.setName( "Tycho platform" );
    targetDef.setTargetLocations( getAllBundleLocations() );
    targetDef.setArch( Platform.getOSArch() );
    targetDef.setOS( Platform.getOS() );
    targetDef.setWS( Platform.getWS() );
    targetDef.setNL( Platform.getNL() );
    TP_SERVICE.saveTargetDefinition( targetDef );
    loadTargetPlatform( targetDef );
  }

  private static ITargetLocation[] getAllBundleLocations() {
    Bundle[] bundles = Platform.getBundle( CORE_RUNTIME ).getBundleContext().getBundles();
    List<ITargetLocation> bundleContainers = new ArrayList<ITargetLocation>();
    Set<File> dirs = new HashSet<File>();
    for( Bundle bundle : bundles ) {
      ModuleRevision moduleRevision = ( ( EquinoxBundle )bundle ).getModule().getCurrentRevision();
      Generation generation = ( Generation )moduleRevision.getRevisionInfo();
      File file = generation.getBundleFile().getBaseFile();
      File folder = file.getParentFile();
      if( !dirs.contains( folder ) ) {
        dirs.add( folder );
        bundleContainers.add( TP_SERVICE.newDirectoryLocation( folder.getAbsolutePath() ) );
      }
    }
    return bundleContainers.toArray( new ITargetLocation[ bundleContainers.size() ] );
  }

  private static void loadTargetPlatform( ITargetDefinition targetDef ) {
    Job job = new LoadTargetDefinitionJob( targetDef );
    job.schedule();
    try {
      job.join();
    } catch( InterruptedException exception ) {
      throw new RuntimeException( exception );
    }
  }

  private TargetUtil() {
    // not designed for instantiation
  }

}
