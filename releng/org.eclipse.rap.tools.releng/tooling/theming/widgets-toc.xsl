<?xml version="1.0" encoding="UTF-8" ?>
<!--
  Copyright (c) 2010 Innoopract Informationssysteme GmbH.
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html

  Contributors:
      Innoopract Informationssysteme GmbH - initial API and implementation
  -->

<!--
  Creates the widgets-toc.xml for the RWT theming reference.
  -->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

  <xsl:output method="xml" />

  <xsl:template match="/">
    <toc label="Widgets Theming Reference">
      <topic label="Widgets" href="help/html/reference/theming/index.html">
        <xsl:apply-templates select="index/file[ element ]" />
      </topic>
    </toc>  
  </xsl:template>

  <xsl:template match="file">
    <xsl:variable name="prefix" select="substring-before(@name, '.theme.xml')" />
    <topic>
      <xsl:attribute name="label">
        <xsl:value-of select="element[ 1 ]/@name" />
      </xsl:attribute>  
      <xsl:attribute name="href">
        <xsl:value-of select="concat( 'help/html/reference/theming/', $prefix, '.html' )" />
      </xsl:attribute>
      <xsl:apply-templates select="element[ position() &gt; 1 ][ @name != '*' ]">
        <xsl:with-param name="prefix" select="$prefix" />
      </xsl:apply-templates>
    </topic>
  </xsl:template>

  <xsl:template match="element">
    <xsl:param name="prefix" />
    <topic>
      <xsl:attribute name="label">
        <xsl:value-of select="@name" />
      </xsl:attribute>  
      <xsl:attribute name="href">
        <xsl:value-of select="concat( 'help/html/reference/theming/', $prefix, '.html#', @name )" />
      </xsl:attribute>
    </topic>
  </xsl:template>

</xsl:stylesheet>
