package org.eclipse.rap.internal.ui.templates;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	// The shared instance
	private static Activator plugin;

	public static Activator getDefault() {
		return plugin;
	}

	public static String getPluginId() {
		return getDefault().getBundle().getSymbolicName();
	}

	/////////////////////////////
	// AbstractUIPlugin overrides

	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

}
