package org.eclipse.rap.ui.internal.launch.rwt;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class RWTLaunchActivator extends AbstractUIPlugin {

  public static RWTLaunchActivator getDefault() {
    return plugin;
  }
  
  public static String getPluginId() {
    return RWTLaunchActivator.getDefault().getBundle().getSymbolicName();
  }
  
  private static RWTLaunchActivator plugin;

  public void start( BundleContext context ) throws Exception {
    super.start( context );
    plugin = this;
  }

  public void stop( BundleContext context ) throws Exception {
    plugin = null;
    super.stop( context );
  }
}
