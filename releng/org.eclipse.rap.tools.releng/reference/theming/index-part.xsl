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
  Creates parts of an index.xml file which is then used as input for creating
  the index.html of the RWT theming reference.
  -->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

  <xsl:output method="xml" omit-xml-declaration="yes" />

  <xsl:param name="filename"></xsl:param>

  <xsl:template match="/">
  <file>
    <xsl:attribute name="name">
      <xsl:value-of select="$filename"/>
    </xsl:attribute>
    <xsl:apply-templates select="theme/element" />
  </file>
  </xsl:template>

  <xsl:template match="element">
  <element>
    <xsl:attribute name="name">
      <xsl:value-of select="@name"/>
    </xsl:attribute>
  </element>
  <xsl:apply-templates select="element" />
  </xsl:template>

</xsl:stylesheet>
