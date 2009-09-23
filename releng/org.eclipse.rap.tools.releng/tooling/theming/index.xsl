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
  Creates the index.html for the RWT theming reference. Expects an input XML
  file of the following form:
  <index>
    <file name="...">
      <element name="..." />
      ...
    </file>
    ...
  </index>
  -->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

  <xsl:output method="xhtml" />

  <xsl:template match="/">
    <html>
      <head>
        <title>RWT Theming Reference</title>
        <link rel="stylesheet" type="text/css" href="../../style.css" />
      </head>
      <body>
        <h1>RWT Theming Reference</h1>
        <h2>Themeable RWT Widgets</h2>
        <du>
          <xsl:apply-templates select="index/file[ element ]" />
        </du>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="file">
    <xsl:variable name="prefix" select="substring-before(@name, '.theme.xml')" />
    <li>
    <a>
    <xsl:attribute name="href">
      <xsl:value-of select="concat( $prefix, '.html' )" />
    </xsl:attribute>
    <xsl:value-of select="element[ 1 ]/@name" />
    </a>
    <xsl:apply-templates select="element[ position() &gt; 1 ][ @name != '*' ]">
      <xsl:with-param name="prefix" select="$prefix" />
    </xsl:apply-templates>
    </li>
  </xsl:template>

  <xsl:template match="element">
    <xsl:param name="prefix" />
    <xsl:text>, </xsl:text>
    <a>
    <xsl:attribute name="href">
      <xsl:value-of select="concat( $prefix, '.html#', @name )" />
    </xsl:attribute>
    <xsl:value-of select="@name" />
    </a>
  </xsl:template>

</xsl:stylesheet>
