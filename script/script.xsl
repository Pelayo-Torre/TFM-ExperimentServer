<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" encoding="utf-8" indent="yes"/>

	<xsl:param name="experimento"/>
	<xsl:param name="uriBase" />

	<xsl:template match="consts">
		<xsl:value-of select="."/>
	</xsl:template>
	
	<xsl:template match="variables">
		<xsl:value-of select="."/>
		<xsl:apply-templates select="experimento" />
	</xsl:template>
	
	<xsl:template match="url">
		<xsl:apply-templates select="urlBase" />
		<xsl:value-of select="."/>
	</xsl:template>
	
	<xsl:template match="functions">
		<xsl:value-of select="."/>
		<xsl:apply-templates select="demographicData" />
	</xsl:template>
	
	<xsl:template match="urlBase">
		<xsl:text>var urlBase=</xsl:text><xsl:value-of select="$uriBase" />
	</xsl:template>
	
	<xsl:template match="experimento">
		<xsl:text>var idExperiment = </xsl:text><xsl:value-of select="$experimento" /><xsl:text>;</xsl:text>
	</xsl:template>	
	
	<xsl:template match="demographicData">
		<xsl:for-each select="entry">
			<xsl:text>function register</xsl:text><xsl:value-of select="value" /><xsl:text>(value) {</xsl:text>
			<xsl:choose>
				<xsl:when test="type = 'STRING'">
					<xsl:text>postStringDD(</xsl:text><xsl:value-of select="key" /><xsl:text>, value);</xsl:text>
				</xsl:when>
				<xsl:when test="type = 'NUMBER'">
					<xsl:text>postNumberDD(</xsl:text><xsl:value-of select="key" /><xsl:text>, value);</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>postDateDD(</xsl:text><xsl:value-of select="key" /><xsl:text>, value);</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>}</xsl:text>
		</xsl:for-each>
	</xsl:template>	
	
</xsl:stylesheet>