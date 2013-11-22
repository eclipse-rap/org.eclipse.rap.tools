/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch.rwt.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TemplateParser_Test {

  @Test( expected = NullPointerException.class )
  public void testConstructor_failsWithNullArgument() {
    new TemplateParser( null );
  }

  @Test( expected = NullPointerException.class )
  public void testRegisterVariable_failsWithNullVariableName() {
    TemplateParser parser = new TemplateParser( "" );

    parser.registerVariable( null, "" );
  }

  @Test( expected = NullPointerException.class )
  public void testRegisterVariable_failsWithNullVariableValue() {
    TemplateParser parser = new TemplateParser( "" );

    parser.registerVariable( "", null );
  }

  @Test
  public void testParse_withoutVariables() {
    String template = "template";
    TemplateParser parser = new TemplateParser( template );

    String parsedString = parser.parse();

    assertEquals( template, parsedString );
  }

  @Test
  public void testParse_withNonExistingVariable() {
    TemplateParser parser = new TemplateParser( "" );
    parser.registerVariable( "does.not.exist", "value" );

    String parsedString = parser.parse();

    assertEquals( "", parsedString );
  }

  @Test
  public void testParse_withExistingVariable() {
    TemplateParser parser = new TemplateParser( "${variableName}" ) ;
    String variableValue = "variableValue";
    parser.registerVariable( "variableName", variableValue );

    String parsedString = parser.parse();

    assertEquals( variableValue, parsedString );
  }

  @Test
  public void testParse_withEmbeddedVariable() {
    TemplateParser parser = new TemplateParser( "xyz${variableName}123$" ) ;
    String variableValue = "variableValue";
    parser.registerVariable( "variableName", variableValue );

    String parsedString = parser.parse();

    assertEquals( "xyzvariableValue123$", parsedString );
  }

  @Test
  public void testParse_withSpecialCharactersValue() {
    TemplateParser parser = new TemplateParser( "${className}" ) ;
    String variableValue = "com.foo.bar.Foo$InnerBar";
    parser.registerVariable( "className", variableValue );

    String parsedString = parser.parse();

    assertEquals( variableValue, parsedString );
  }

  @Test
  public void testParse_withVariableNameAsValue() {
    TemplateParser parser = new TemplateParser( "${className}" ) ;
    String variableValue = "${className}";
    parser.registerVariable( "className", variableValue );

    String parsedString = parser.parse();

    assertEquals( variableValue, parsedString );
  }

  @Test
  public void testParse_withMultimpleVariables() {
    String template = "${variableName} ${variableName}";
    TemplateParser parser = new TemplateParser( template ) ;
    String variableValue = "x";
    parser.registerVariable( "variableName", variableValue );

    String parsedString = parser.parse();

    assertEquals( "x x", parsedString );
  }

}
