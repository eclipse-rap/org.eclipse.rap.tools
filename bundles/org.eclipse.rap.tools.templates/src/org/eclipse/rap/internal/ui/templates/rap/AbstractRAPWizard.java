/*******************************************************************************
 * Copyright (c) 2007, 2013 EclipseSource and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.internal.ui.templates.rap;

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
import org.eclipse.rap.internal.ui.templates.Activator;
import org.eclipse.rap.internal.ui.templates.TemplateUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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

  private static final String WORKBENCH_LAUNCH_TEMPLATE = "workbench_launch.template"; //$NON-NLS-1$
  private static final String BASIC_LAUNCH_TEMPLATE = "basic_launch.template"; //$NON-NLS-1$
  private static final String CHARSET = "ISO-8859-1"; //$NON-NLS-1$
  private static final String PREFERENCE_INSTALL_TARGET = "installTarget"; //$NON-NLS-1$

  private static final String TAG_SERVLET_PATH = "${servletPath}"; //$NON-NLS-1$
  private static final String TAG_PLUGIN_ID = "${pluginId}"; //$NON-NLS-1$
  private static final String TAG_PROJECT_NAME = "${projectName}"; //$NON-NLS-1$
  private static final String TAG_PACKAGE_NAME = "${packageName}"; //$NON-NLS-1$

  static final String SERVICE_COMPONENT_FILE = "OSGI-INF/contribution.xml"; //$NON-NLS-1$

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
        InputStream templete = AbstractRAPWizard.class.getResourceAsStream( getLaunchTemplete( project ) );
        InputStream stream = readTemplete( templete, project.getName(), pluginId );
        launchConfig.create( stream, true, new NullProgressMonitor() );
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

  protected abstract boolean shouldModifyActivator();

  private String getLaunchTemplete( IProject project ) {
    IFile serviceComponentXml = project.getFile( SERVICE_COMPONENT_FILE );
    return serviceComponentXml.exists() ? BASIC_LAUNCH_TEMPLATE : WORKBENCH_LAUNCH_TEMPLATE;
  }

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
    if( !containsRapUi() ) {
      handleRapTargetInstalation();
    }
  }

  private boolean containsRapUi() {
    IPluginModelBase rapUiPluginModel = PluginRegistry.findModel( "org.eclipse.rap.ui" ); //$NON-NLS-1$
    return rapUiPluginModel != null;
  }

  private void handleRapTargetInstalation() {
    final Display currentDisplay = Display.getCurrent();
    currentDisplay.asyncExec( new Runnable() {

      public void run() {
        boolean isRapTargetInstallWanted = isRapTargetInstallWanted();
        if( isRapTargetInstallWanted ) {
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
    IHandlerService handlerService
      = ( IHandlerService )workbenchWindow.getService( IHandlerService.class );
    try {
      handlerService.executeCommand( "org.eclipse.rap.ui.intro.installTarget", null ); //$NON-NLS-1$
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
        }
      } catch( CoreException cex ) {
        TemplateUtil.log( cex.getStatus() );
      }
    }

  }

}
