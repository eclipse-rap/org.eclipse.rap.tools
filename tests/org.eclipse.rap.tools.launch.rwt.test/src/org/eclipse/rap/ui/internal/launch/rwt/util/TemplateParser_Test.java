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

import org.eclipse.rap.ui.internal.launch.rwt.util.TemplateParser;

import junit.framework.TestCase;


public class TemplateParser_Test extends TestCase {
  
  public void testConstructorWithNullArgument() {
    try {
      new TemplateParser( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterVariableWithNullVariableName() {
    TemplateParser parser = new TemplateParser( "" );
    try {
      parser.registerVariable( null, "" );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterVariableWithNullVariableValue() {
    TemplateParser parser = new TemplateParser( "" );
    try {
      parser.registerVariable( "", null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testParseWithoutVariables() {
    String template = "template";
    TemplateParser parser = new TemplateParser( template );
    
    String parsedString = parser.parse();
    
    assertEquals( template, parsedString );
  }

  public void testParseWithNonExistingVariable() {
    TemplateParser parser = new TemplateParser( "" );
    parser.registerVariable( "does.not.exist", "value" );
    
    String parsedString = parser.parse();
    
    assertEquals( "", parsedString );
  }
  
  public void testParseWithExistingVariable() {
    TemplateParser parser = new TemplateParser( "${variableName}" ) ;
    String variableValue = "variableValue";
    parser.registerVariable( "variableName", variableValue );
    
    String parsedString = parser.parse();
    
    assertEquals( variableValue, parsedString );
  }

  public void testParseWithEmbeddedVariable() {
    TemplateParser parser = new TemplateParser( "xyz${variableName}123$" ) ;
    String variableValue = "variableValue";
    parser.registerVariable( "variableName", variableValue );
    
    String parsedString = parser.parse();
    
    assertEquals( "xyzvariableValue123$", parsedString );
  }
  
  public void testParseWithSpecialCharactersValue() {
    TemplateParser parser = new TemplateParser( "${className}" ) ;
    String variableValue = "com.foo.bar.Foo$InnerBar";
    parser.registerVariable( "className", variableValue );
    
    String parsedString = parser.parse();
    
    assertEquals( variableValue, parsedString );
  }
  
  public void testParseWithVariableNameAsValue() {
    TemplateParser parser = new TemplateParser( "${className}" ) ;
    String variableValue = "${className}";
    parser.registerVariable( "className", variableValue );
    
    String parsedString = parser.parse();
    
    assertEquals( variableValue, parsedString );
  }
  
  public void testParseWithMultimpleVariables() {
    String template = "${variableName} ${variableName}";
    TemplateParser parser = new TemplateParser( template ) ;
    String variableValue = "x";
    parser.registerVariable( "variableName", variableValue );
    
    String parsedString = parser.parse();
    
    assertEquals( "x x", parsedString );
  }
}
