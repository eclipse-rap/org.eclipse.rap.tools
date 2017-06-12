/*******************************************************************************
 * Copyright (c) 2007, 2017 EclipseSource and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.tools.templates.internal.rap;

import java.io.*;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;
import org.eclipse.rap.tools.templates.internal.Activator;
import org.eclipse.rap.tools.templates.internal.TemplateUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * <p>
 * Abstract RAP template wizard. Subclasses must implement
 * {@link #init(org.eclipse.pde.ui.IFieldData)} and
 * {@link #createTemplateSections()}.
 * </p>
 */
abstract class AbstractRAPWizard extends NewPluginTemplateWizard {

  private static final String CHARSET = "ISO-8859-1"; //$NON-NLS-1$
  private static final String PREFERENCE_INSTALL_TARGET = "installTarget"; //$NON-NLS-1$

  private static final String TAG_SERVLET_PATH = "${servletPath}"; //$NON-NLS-1$
  private static final String TAG_PLUGIN_ID = "${pluginId}"; //$NON-NLS-1$
  private static final String TAG_PROJECT_NAME = "${projectName}"; //$NON-NLS-1$
  private static final String TAG_PACKAGE_NAME = "${packageName}"; //$NON-NLS-1$

  static final String SERVICE_COMPONENT_FILE = "OSGI-INF/contribution.xml"; //$NON-NLS-1$
  private static final String E4_APP_CONFIG_FILE = "Application.e4xmi"; //$NON-NLS-1$

  private ResourceChangeListener listener;

  @Override
  public void init( IFieldData data ) {
    super.init( data );
    listener = new ResourceChangeListener( this );
    ResourcesPlugin.getWorkspace().addResourceChangeListener( listener );
  }

  public boolean performFinish( IProject project, IPluginModelBase model, IProgressMonitor monitor )
  {
    boolean result = super.performFinish( project, model, monitor );
    if( result ) {
      copyServiceComponentConfig( project, model );
      copyLaunchConfig( project, model );
      copyE4AppConfig( project, model );
      handleRapTargetVerification();
    }
    return result;
  }

  @Override
  public void dispose() {
    super.dispose();
    ResourcesPlugin.getWorkspace().removeResourceChangeListener( listener );
  }

  private void copyServiceComponentConfig( IProject project, IPluginModelBase model ) {
    IFile serviceComponentXml = project.getFile( SERVICE_COMPONENT_FILE );
    if( serviceComponentXml.exists() ) {
      try {
        String pluginId = model.getPluginBase().getId();
        InputStream templete = serviceComponentXml.getContents();
        InputStream stream = readTemplete( templete, project.getName(), pluginId );
        serviceComponentXml.setContents( stream, true, false, new NullProgressMonitor() );
      } catch( CoreException exception ) {
        TemplateUtil.log( exception.getStatus() );
      }
    }
  }

  private void copyLaunchConfig( IProject project, IPluginModelBase model ) {
    String name = project.getName() + ".launch"; //$NON-NLS-1$
    IFile launchConfig = project.getFile( name );
    if( !launchConfig.exists() ) {
      try {
        String pluginId = model.getPluginBase().getId();
        InputStream templete = AbstractRAPWizard.class.getResourceAsStream( getLaunchTemplate() );
        InputStream stream = readTemplete( templete, project.getName(), pluginId );
        launchConfig.create( stream, true, new NullProgressMonitor() );
      } catch( CoreException exception ) {
        TemplateUtil.log( exception.getStatus() );
      }
    }
  }

  private void copyE4AppConfig( IProject project, IPluginModelBase model ) {
    IFile e4AppConfig = project.getFile( E4_APP_CONFIG_FILE );
    if( e4AppConfig.exists() ) {
      try {
        String pluginId = model.getPluginBase().getId();
        InputStream templete = e4AppConfig.getContents();
        InputStream stream = readTemplete( templete, project.getName(), pluginId );
        e4AppConfig.setContents( stream, true, false, new NullProgressMonitor() );
      } catch( CoreException exception ) {
        TemplateUtil.log( exception.getStatus() );
      }
    }
  }

  private InputStream readTemplete( InputStream templete, String projectName, String pluginId )
    throws CoreException
  {
    StringBuffer buffer = new StringBuffer();
    try {
      InputStreamReader reader = new InputStreamReader( templete, CHARSET );
      BufferedReader br = new BufferedReader( reader );
      try {
        int character = br.read();
        while( character != -1 ) {
          buffer.append( ( char )character );
          character = br.read();
        }
      } finally {
        br.close();
      }
    } catch( Exception exception ) {
      String msg = "Could not read template"; //$NON-NLS-1$
      Status status = new Status( IStatus.ERROR, TemplateUtil.PLUGIN_ID, msg, exception );
      throw new CoreException( status );
    }
    // Replace $-placeholder with actual values
    replacePlaceholder( buffer, TAG_PROJECT_NAME, projectName );
    replacePlaceholder( buffer, TAG_PACKAGE_NAME, getPackageName() );
    replacePlaceholder( buffer, TAG_PLUGIN_ID, pluginId );
    replacePlaceholder( buffer, TAG_SERVLET_PATH, getServletPath() );
    return new ByteArrayInputStream( buffer.toString().getBytes() );
  }

  protected abstract String getServletPath();

  protected abstract String getPackageName();

  protected abstract String getRequireBundles();

  protected abstract String getActivatorName();

  protected abstract String getLaunchTemplate();

  protected abstract boolean shouldModifyActivator();

  protected abstract boolean shouldModifyBuildProperties();

  private static void replacePlaceholder( StringBuffer buffer,
                                          String placeholder,
                                          String replacement )
  {
    int index = buffer.indexOf( placeholder );
    while( index != -1 ) {
      buffer.replace( index, index + placeholder.length(), replacement );
      index = buffer.indexOf( placeholder );
     }
  }

  private void handleRapTargetVerification() {
    if( !containsRequiredBundle() ) {
      handleRapTargetInstallation();
    }
  }

  private boolean containsRequiredBundle() {
    String requiredBundle = "org.eclipse.rap." + getRequiredBundleSuffix();
    return PluginRegistry.findModel( requiredBundle ) != null;
  }

  private String getRequiredBundleSuffix() {
    String requireBundles = getRequireBundles();
    if( requireBundles.contains( "org.eclipse.rap.e4" ) ) {
      return "e4";
    } else if( requireBundles.contains( "org.eclipse.rap.ui" ) ) {
      return "ui";
    }
    return "rwt";
  }

  private void handleRapTargetInstallation() {
    // [if] Open target installer dialog after RAP wizard is closed
    // and workbench window shell become active again
    // 469119: [Tools] Target installer dialog freezes IDE when opened from New Project wizard
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=469119
    final Shell mainShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    mainShell.addListener( SWT.Activate, new Listener() {
      public void handleEvent( Event event ) {
        mainShell.removeListener( SWT.Activate, this );
        if( isRapTargetInstallWanted() ) {
          executeInstallTargetCommand();
        }
      }
    } );
  }

  private boolean isRapTargetInstallWanted() {
    Shell parentShell = Display.getCurrent().getActiveShell();
    String title = Messages.AbstractRAPWizard_targetQuestionDialogTitle;
    String message = Messages.AbstractRAPWizard_targetQuestionDialogMessage;
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    String preferenceInstallTarget = store.getString( PREFERENCE_INSTALL_TARGET );
    boolean result = false;
    if( isPromptRequired( preferenceInstallTarget ) ) {
      MessageDialogWithToggle dialog =
        MessageDialogWithToggle.openYesNoQuestion( parentShell,
                                                   title,
                                                   message,
                                                   null,
                                                   false,
                                                   store,
                                                   PREFERENCE_INSTALL_TARGET );
      result = dialog.getReturnCode() == IDialogConstants.YES_ID;
    } else {
      result = MessageDialogWithToggle.ALWAYS.equals( preferenceInstallTarget );
    }
    return result;
  }

  private boolean isPromptRequired( String preferenceInstallTarget ) {
    boolean preferenceEmpty = preferenceInstallTarget.length() == 0;
    boolean promptRequired = MessageDialogWithToggle.PROMPT.equals( preferenceInstallTarget );
    return preferenceEmpty || promptRequired;
  }

  private void executeInstallTargetCommand() {
    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IHandlerService handlerService = workbenchWindow.getService( IHandlerService.class );
    try {
      Event event = new Event();
      event.text = getRequiredBundleSuffix();
      handlerService.executeCommand( "org.eclipse.rap.tools.intro.installTarget", event ); //$NON-NLS-1$
    } catch( CommandException e ) {
      ILog log = Activator.getDefault().getLog();
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e );
      log.log( status );
    }
  }

  ///////////////////
  // helping classes

  private static class ResourceChangeListener implements IResourceChangeListener {

    private AbstractRAPWizard wizard;

    public ResourceChangeListener( AbstractRAPWizard wizard ) {
      this.wizard = wizard;
    }

    public void resourceChanged( IResourceChangeEvent event ) {
      try {
        IResourceDelta delta = event.getDelta();
        if( delta != null ) {
          delta.accept( new ManifestModifier( wizard ) );
          if( wizard.shouldModifyActivator() && wizard.getActivatorName() != null ) {
            delta.accept( new ActivatorModifier( wizard ) );
          }
          if( wizard.shouldModifyBuildProperties() ) {
            delta.accept( new BuildPropertiesModifier( wizard ) );
          }
        }
      } catch( CoreException cex ) {
        TemplateUtil.log( cex.getStatus() );
      }
    }

  }

}
