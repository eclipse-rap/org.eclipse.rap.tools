/*******************************************************************************
 * Copyright (c) 2017 EclipseSource and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.tools.templates.internal.rap;

import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;


public class E4RAPWizard extends AbstractRAPWizard {

  private static final String LAUNCH_TEMPLATE = "e4_launch.template"; //$NON-NLS-1$

  private AbstractRAPTemplate template;

  @Override
  public void init( IFieldData data ) {
    super.init( data );
    setWindowTitle( Messages.e4RAPWizard_windowTitle );
  }

  @Override
  public ITemplateSection[] createTemplateSections() {
    template = new E4RAPTemplate();
    return new ITemplateSection[] { template };
  }

  @Override
  protected String getServletPath() {
    return template.getServletPath();
  }

  @Override
  protected String getPackageName() {
    return template.getPackageName();
  }

  @Override
  protected String getRequireBundles() {
    return asString( new BundleValueEntry( true, "org.eclipse.rap.rwt", "4.0.0", "5.0.0" ),
                     new BundleValueEntry( true, "org.eclipse.rap.e4", "4.0.0", null ),
                     new BundleValueEntry( true, "org.eclipse.e4.ui.workbench", "1.3.0", null ),
                     BundleValueEntry.bundle( "org.eclipse.e4.core.di" ),
                     BundleValueEntry.bundle( "org.eclipse.e4.core.di.extensions" ),
                     BundleValueEntry.bundle( "org.eclipse.rap.jface" ),
                     BundleValueEntry.bundle( "org.eclipse.e4.core.services" ),
                     BundleValueEntry.bundle( "org.eclipse.e4.ui.di" ),
                     BundleValueEntry.bundle( "org.eclipse.equinox.common" ),
                     BundleValueEntry.bundle( "org.eclipse.e4.ui.model.workbench" ),
                     new BundleValueEntry( true, "jakarta.annotation-api", "1.3.5", null ) );
  }

  @Override
  protected String getActivatorName() {
    return template.getActivatorName();
  }

  @Override
  protected boolean shouldModifyActivator() {
    return true;
  }

  @Override
  protected boolean shouldModifyBuildProperties() {
    return true;
  }

  @Override
  protected String getLaunchTemplate() {
    return LAUNCH_TEMPLATE;
  }

  private static String asString( BundleValueEntry... bundles ) {
    StringBuilder builder = new StringBuilder();
    for( BundleValueEntry entry : bundles ) {
      if( builder.length() > 0 ) {
        builder.append( "," + ResourceModifier.NL + " " );
      }
      builder.append( entry );
    }
    return builder.toString();
  }
  static class BundleValueEntry {

    private String name;
    private String version;
    private boolean bundle;

    public BundleValueEntry( boolean bundle, String name, String min, String max ) {
      this.name = name;
      this.bundle = bundle;
      if( max == null ) {
        version = min;
      } else if( min != null ) {
        version = "[" + min + "," + max + ")";
      }
    }

    public static BundleValueEntry bundle( String name ) {
      return new BundleValueEntry( true, name, null, null );
    }

    public static BundleValueEntry pack( String name ) {
      return new BundleValueEntry( false, name, null, null );
    }

    public static BundleValueEntry pack( String name, String version ) {
      return new BundleValueEntry( false, name, version, null );
    }

    @Override
    public String toString() {
      if( version == null) {
        return name;
      }
      String versionString = bundle ? "bundle-version" : "version";
      return name + ";" + versionString + "=\"" + version + "\"";
    }

  }

}
