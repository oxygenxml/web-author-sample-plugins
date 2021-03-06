<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      exclude-result-prefixes="xs"
      version="2.0">
   <xsl:output indent="yes"/>
   
   <xsl:template match="*[./title]" priority="5">
      <toc-entry>
         <title><xsl:value-of select="./title"/></title>
         <xsl:apply-templates select="*"/>
      </toc-entry>
   </xsl:template>
   
   <!-- Fallback template - just go deeper searching for a title -->
   <xsl:template match="*">
      <xsl:apply-templates select="*"/>
   </xsl:template>
</xsl:stylesheet>