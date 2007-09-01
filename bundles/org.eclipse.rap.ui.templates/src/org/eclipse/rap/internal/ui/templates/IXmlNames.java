package org.eclipse.rap.internal.ui.templates;

/**
 * Commonly used XML elements, attributes and extension point ids.
 */
public interface IXmlNames {
  String ATT_ALLOWMULTIPLE = "allowMultiple"; //$NON-NLS-1$
  String ATT_CLASS = "class"; //$NON-NLS-1$
  String ATT_DESCRIPTION = "description"; //$NON-NLS-1$
  String ATT_CATID = "categoryId"; //$NON-NLS-1$
  String ATT_ICON = "icon"; //$NON-NLS-1$
  String ATT_ID = "id"; //$NON-NLS-1$
  String ATT_NAME = "name"; //$NON-NLS-1$
  String ATT_PARAMETER = "parameter"; //$NON-NLS-1$
  
  String ELEM_CATEGORY = "category"; //$NON-NLS-1$
  String ELEM_COMMAND = "command"; //$NON-NLS-1$
  String ELEM_ENTRYPOINT = "entrypoint"; //$NON-NLS-1$
  String ELEM_PERSPECTIVE = "perspective"; //$NON-NLS-1$
  String ELEM_VIEW = "view"; //$NON-NLS-1$
  
  String XID_COMMANDS = "org.eclipse.ui.commands"; //$NON-NLS-1$
  String XID_ENTRYPOINT = "org.eclipse.rap.ui.entrypoint"; //$NON-NLS-1$
  String XID_PERSPECTIVES = "org.eclipse.ui.perspectives"; //$NON-NLS-1$
  String XID_VIEWS = "org.eclipse.ui.views"; //$NON-NLS-1$
}
