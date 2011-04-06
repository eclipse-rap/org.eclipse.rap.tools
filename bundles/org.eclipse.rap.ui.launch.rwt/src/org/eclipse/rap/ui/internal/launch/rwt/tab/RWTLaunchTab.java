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
package org.eclipse.rap.ui.internal.launch.rwt.tab;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.rap.ui.internal.launch.rwt.config.RWTLaunchConfig;
import org.eclipse.rap.ui.internal.launch.rwt.config.ValidationRunner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public abstract class RWTLaunchTab extends JavaLaunchTab {

  public abstract void initializeFrom( RWTLaunchConfig config );
  public abstract void performApply( RWTLaunchConfig config );
  
  public void setDefaults( ILaunchConfigurationWorkingCopy config ) {
    RWTLaunchConfig.setDefaults( config );
  }
  
  public final void initializeFrom( ILaunchConfiguration config ) {
    RWTLaunchConfig launchConfig = new RWTLaunchConfig( config );
    initializeFrom( launchConfig );
    setDirty( false );
  }

  public final void performApply( ILaunchConfigurationWorkingCopy config ) {
    RWTLaunchConfig launchConfig = new RWTLaunchConfig( config );
    performApply( launchConfig );
    validate( launchConfig );
    setDirty( true );
  }
  
  public boolean isValid( ILaunchConfiguration config ) {
    return getErrorMessage() == null;
  }
  
  protected final Label createLabel( Composite parent, String text ) {
    Label result = new Label( parent, SWT.NONE );
    result.setText( text );
    return result;
  }
  
  private void validate( RWTLaunchConfig config ) {
    ValidationRunner validationRunner = new ValidationRunner( config );
    validationRunner.validate();
    setMessage( null );
    IStatus[] warnings = validationRunner.getWarnings();
    if( warnings.length > 0 ) {
      setMessage( warnings[ 0 ].getMessage() );
    }
    setErrorMessage( null );
    IStatus[] errors = validationRunner.getErrors();
    if( errors.length > 0 ) {
      setErrorMessage( errors[ 0 ].getMessage() );
    }
  }
}
