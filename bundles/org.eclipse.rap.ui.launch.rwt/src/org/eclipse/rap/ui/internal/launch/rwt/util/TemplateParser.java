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
package org.eclipse.rap.ui.internal.launch.rwt.util;

import java.util.*;



public class TemplateParser {
  private final String template;
  private final Map variables;

  public TemplateParser( String template ) {
    checkNotNull( template, "template" ); //$NON-NLS-1$
    this.template = template;
    this.variables = new HashMap();
  }

  public void registerVariable( String variableName, String variableValue ) {
    checkNotNull( variableName, "variableName" ); //$NON-NLS-1$
    checkNotNull( variableValue, "variableValue" ); //$NON-NLS-1$
    variables.put( variableName, variableValue );
  }

  public String parse() {
    StringBuffer result = new StringBuffer( template );
    Iterator iter = variables.keySet().iterator();
    while( iter.hasNext() ) {
      String variableName = ( String )iter.next();
      String variableValue = getVariableValue( variableName );
      String variableToken = variableToken( variableName );
      replaceAll( result, variableToken, variableValue );
    }
    return result.toString();
  }

  private static void replaceAll( StringBuffer buffer, String occurence, String replacement ) {
    int index = buffer.indexOf( occurence, 0 );
    while( index != -1 ) {
      buffer.replace( index, index + occurence.length(), replacement );
      index = buffer.indexOf( occurence, index + replacement.length() );
    }
  }

  private String getVariableValue( String variableName ) {
    String result = ""; //$NON-NLS-1$
    if( variables.containsKey( variableName ) ) {
      result = ( String )variables.get( variableName );
    }
    return result;
  }

  private static String variableToken( String variableName ) {
    return "${" + variableName + "}"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  private static void checkNotNull( String argument, String argumentName ) {
    if( argument == null ) {
      throw new NullPointerException( argumentName );
    }
  }
}
