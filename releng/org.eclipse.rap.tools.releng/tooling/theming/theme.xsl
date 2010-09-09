<?xml version="1.0" encoding="UTF-8" ?>
<!--
  Copyright (c) 2008 Innoopract Informationssysteme GmbH.
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html

  Contributors:
      Innoopract Informationssysteme GmbH - initial API and implementation
  -->

<!--
  Transforms *.theme.xml files to *.html files to create the RWT theming
  reference.
  -->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

  <xsl:output method="xhtml" />

  <xsl:template match="/">
    <html>
      <head>
        <title>
        <xsl:value-of select="/theme/element[1]/@name" />
        </title>
        <link rel="stylesheet" type="text/css" href="../../style.css" />
        <style type="text/css">
        <xsl:text>
        h2 {
          margin: 20px 0 5px;
          padding: 5px 10px;
          background-color: #ccccff;
          border: 1px solid black;
        }
        h3 {
          margin: 15px 0 5px;
          padding: 5px 10px;
          background-color: #eeeeff;
          border: 1px solid black;
        }
        ul {
          list-style-type: none;
          padding: 0;
          margin: 8px 0 8px 18px;
        }
        li {
          margin: -1px 0 0;
          padding: 2px 5px;
          border: 1px solid black;
        }
        h2 + p {
          margin: 10px 0 5px 12px;
        }
        h3 + p {
          margin: 8px 0 3px 24px;
        }
        .deprecated {
          text-decoration: line-through;
        }
        .css-name {
          font-weight: bold;
          color: #0000aa;
        }
        .css-desc {
          margin: 1px 0 2px 20px;
        }
        </xsl:text>
        </style>
      </head>
      <body>
        <span>
          <a href="index.html">RWT Theming Reference</a>
          <xsl:text> &gt; </xsl:text>
          <xsl:value-of select="theme/element[@name!='*']/@name" />
        </span>
        <xsl:apply-templates select="theme/element[@name!='*']" />
      </body>
    </html>
  </xsl:template>

  <xsl:template match="element">
    <a>
      <xsl:attribute name="name">
        <xsl:value-of select="@name" />
      </xsl:attribute>
      <xsl:text> <!-- text must not be empty --> </xsl:text>
    </a>
    <h2><xsl:value-of select="@name" /></h2>
    <p><xsl:value-of select="description|@description" /></p>

    <!-- Properties -->
    <h3>Available CSS Properties</h3>
    <xsl:if test="property">
      <p>
      Properties that can be defined for this element:
      </p>
      <ul>
      <xsl:apply-templates select="property">
        <xsl:sort select="@name" />
      </xsl:apply-templates>
      </ul>
    </xsl:if>
    <xsl:if test="not( property )">
      <p><em>none</em></p>
    </xsl:if>

    <!-- Styles -->
    <h3>Applicable SWT Styles</h3>
    <xsl:if test="style">
      <p>
      SWT style flags that can be referred to in a selector for this element
      (as CSS attributes):
      </p>
      <ul>
      <xsl:apply-templates select="style">
        <xsl:sort select="@name" />
      </xsl:apply-templates>
      </ul>
    </xsl:if>
    <xsl:if test="not( style )">
      <p><em>none</em></p>
    </xsl:if>

    <!-- States -->
    <h3>Applicable States</h3>
    <xsl:if test="state">
      <p>
      Widget states that can be referred to in a selector for this element
      (as CSS pseudo-classes):
      </p>
      <ul>
      <xsl:apply-templates select="state">
        <xsl:sort select="@name" />
      </xsl:apply-templates>
      </ul>
    </xsl:if>
    <xsl:if test="not( state )">
      <p><em>none</em></p>
    </xsl:if>

    <!-- Sub-elements -->
    <xsl:apply-templates select="element" />
  </xsl:template>

  <xsl:template match="property">
    <li>
      <a>
        <xsl:attribute name="href">
          <xsl:value-of select="concat( 'Properties.html#', @name )" />
        </xsl:attribute>
        <xsl:attribute name="class">
          <xsl:text>css-name</xsl:text>
          <xsl:if test="translate( @deprecated, 'TRUE', 'true' ) = 'true'">
            <xsl:text> deprecated</xsl:text>
          </xsl:if>
        </xsl:attribute>
        <xsl:value-of select="@name" />
      </a>
      <p class="css-desc">
        <xsl:value-of select="description|@description" />
      </p>
    </li>
    <xsl:if test="not( description|@description )">
      <xsl:message>
        Description missing for property <xsl:value-of select="@name" />
      </xsl:message>
    </xsl:if>
  </xsl:template>

  <xsl:template match="style">
    <li>
      <span class="css-name">
        <xsl:value-of select="concat( '[', @name, ']' )" />
      </span>
      <p class="css-desc">
        <xsl:value-of select="description|@description" />
      </p>
    </li>
    <xsl:if test="not( description|@description )">
      <xsl:message>
        Description missing for style <xsl:value-of select="@name" />
      </xsl:message>
    </xsl:if>
  </xsl:template>

  <xsl:template match="state">
    <li>
      <span class="css-name">
        <xsl:value-of select="concat( ':', @name )" />
      </span>
      <p class="css-desc">
        <xsl:value-of select="description|@description" />
      </p>
    </li>
    <xsl:if test="not( description|@description )">
      <xsl:message>
        Description missing for state <xsl:value-of select="@name" />
      </xsl:message>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
