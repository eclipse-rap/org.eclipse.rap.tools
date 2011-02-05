/*******************************************************************************
 * Copyright (c) 2011 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.intro.target;

import java.lang.reflect.Method;
import java.net.URI;

import org.eclipse.pde.internal.core.target.IUBundleContainer;
import org.eclipse.pde.internal.core.target.TargetPlatformService;
import org.eclipse.pde.internal.core.target.provisional.ITargetDefinition;
import org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService;

public abstract class ContainerCreator {

  private static final String NEW_IU_CONTAINER = "newIUContainer"; //$NON-NLS-1$
  private static ContainerCreator creator;

  public static ContainerCreator getInstance() {
    if( creator == null ) {
      createContainerInstance();
    }
    return creator;
  }

  private static void createContainerInstance() {
    if( isWithFlagParameter() ) {
      creator = new ContainerCreatorWithFlagParameter();
    } else {
      creator = new ContainerCreatorWithoutFlagParameter();
    }
  }

  public abstract IUBundleContainer createContainer( 
    final String[] rootIUs,
    final String[] versions,
    final URI[] targetRepositoryURIs,
    final ITargetPlatformService service ) throws Exception;

  private static boolean isWithFlagParameter() {
    boolean isFlagVersion = false;
    try {
      Class[] paramsIndigo = new Class[]{
        String[].class, String[].class, URI[].class, Integer.TYPE
      };
      TargetPlatformService.class.getMethod( NEW_IU_CONTAINER,
                                             paramsIndigo );
      // is the version with the flag parameter of newIUContainer
      isFlagVersion = true;
    } catch( NoSuchMethodException e ) {
      // Ignore
    }
    return isFlagVersion;
  }

  /*
   * ContainerCreatorWithoutFlagParameter is used to create the contianer
   * for Eclipse 3.6
   */
  private static final class ContainerCreatorWithoutFlagParameter
    extends ContainerCreator
  {
    public IUBundleContainer createContainer( 
      final String[] rootIUs,
      final String[] versions,
      final URI[] targetRepositoryURIs,
      final ITargetPlatformService service ) throws Exception
    {
      Class[] params = new Class[] {
        String[].class, String[].class, URI[].class
      };
      Object[] inputNewContainer = new Object[] {
        rootIUs, versions, targetRepositoryURIs
      };
      Class serviceClass = TargetPlatformService.class;
      Method newIUContainerMethod 
        = serviceClass.getMethod( NEW_IU_CONTAINER, params );
      Object invokeResult 
        = newIUContainerMethod.invoke( service, inputNewContainer );
      IUBundleContainer container = ( IUBundleContainer ) invokeResult;
      configureContainer( container );
      return container;
    }

    private void configureContainer( IUBundleContainer container )
      throws Exception
    {
      // Configure container to ignore all required
      Class[] argsInclude = new Class[]{
        Boolean.TYPE, ITargetDefinition.class
      };
      Object[] inputIcludeAllRequired = new Object[]{
        Boolean.FALSE, null
      };
      Class clazz = container.getClass();
      Method methodIncludeAllRequired 
        = clazz.getMethod( "setIncludeAllRequired", argsInclude ); //$NON-NLS-1$
      methodIncludeAllRequired.invoke( container, inputIcludeAllRequired );
    }
  }
  
  /*
   * ContainerCreatorWithFlagParameter is used to create the container for
   * Eclipse 3.7 and higher
   */
  private static final class ContainerCreatorWithFlagParameter
    extends ContainerCreator
  {
    
    public IUBundleContainer createContainer( 
      final String[] rootIUs,
      final String[] versions,
      final URI[] targetRepositoryURIs,
      final ITargetPlatformService service ) throws Exception
    {
      Class[] params = new Class[]{
        String[].class, String[].class, URI[].class, Integer.TYPE
      };
      Integer flags = Integer.valueOf( "0" ); //$NON-NLS-1$
      Object[] inputNewContainer = new Object[]{
        rootIUs, versions, targetRepositoryURIs, flags
      };
      Class clazz = service.getClass();
      Method newIUContainerMethod = clazz.getMethod( NEW_IU_CONTAINER, params );
      Object invokeResult 
        = newIUContainerMethod.invoke( service, inputNewContainer );
      IUBundleContainer container = ( IUBundleContainer ) invokeResult;
      return container;
    }
  }
}
