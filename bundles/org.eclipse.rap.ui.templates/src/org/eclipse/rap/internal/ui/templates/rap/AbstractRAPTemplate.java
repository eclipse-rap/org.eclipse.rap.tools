/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates.rap;

import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginModelFactory;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.rap.internal.ui.templates.PluginReference;
import org.eclipse.rap.internal.ui.templates.TemplateUtil;

abstract class AbstractRAPTemplate extends OptionTemplateSection {

  protected static final String KEY_APPLICATION_CLASS = "applicationClass"; //$NON-NLS-1$
  protected static final String KEY_WINDOW_TITLE = "windowTitle"; //$NON-NLS-1$
  protected static final String TEMPLATES_DIRECTORY = "templates_3.2"; //$NON-NLS-1$

  protected final URL getInstallURL() {
    return TemplateUtil.getInstallURL();
  }

  protected final ResourceBundle getPluginResourceBundle() {
    return TemplateUtil.getPluginResourceBundle();
  }

  public String[] getNewFiles() {
    return new String[ 0 ];
  }

  public String getUsedExtensionPoint() {
    return null;
  }

  public boolean isDependentOnParentWizard() {
    return true;
  }

  public void initializeFields( final IPluginModelBase modelBase ) {
    String id = modelBase.getPluginBase().getId();
    String packageName = TemplateUtil.getFormattedPackageName( id );
    initializeOption( KEY_PACKAGE_NAME, packageName );
  }

  protected void initializeFields(IFieldData data) {
    String packageName = TemplateUtil.getFormattedPackageName( data.getId() );
    initializeOption( KEY_PACKAGE_NAME, packageName );
  }

  public IPluginReference[] getDependencies(final String schemaVersion) {
    return new IPluginReference[] {
      new PluginReference( "org.eclipse.rap.ui", null, 0 ) //$NON-NLS-1$
    };
  }

  // protected methods
  ////////////////////

  protected final void addExtensionToPlugin( final IPluginExtension extension ) 
  throws CoreException {
    IPluginBase plugin = model.getPluginBase();
    if( !extension.isInTheModel() ) {
      plugin.add( extension );
    }
  }

  protected final IPluginElement createElement( final IPluginExtension extension ) {
    IPluginModelFactory factory = model.getPluginFactory();
    return factory.createElement( extension );
  }

  protected final String getApplicationName() {
    return getStringOption( KEY_APPLICATION_CLASS );
  }

  protected final String getPackageName() {
    return getStringOption( KEY_PACKAGE_NAME );
  }

  protected final String getPluginId() {
    IPluginBase plugin = model.getPluginBase();
    return plugin.getId();
  }
  
  protected String getTemplateDirectory() {
    return TEMPLATES_DIRECTORY;
  }

}
