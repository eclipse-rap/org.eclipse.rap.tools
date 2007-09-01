package $packageName$;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 * and is contributed through the plugin.xml.
 */
public class $applicationClass$ implements IEntryPoint {

	public Display createUI() {
		Display display = PlatformUI.createDisplay();
		PlatformUI.createAndRunWorkbench( display, new ApplicationWorkbenchAdvisor() );
		return display;
	}
}
