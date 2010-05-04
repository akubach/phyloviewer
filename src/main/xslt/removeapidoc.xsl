<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:mule="http://www.mulesource.org/schema/mule/core/2.2"
	xmlns:http="http://www.mulesource.org/schema/mule/http/2.2"
    xmlns:restlet="http://www.mulesource.org/schema/mule/restlet/2.2"
    xmlns:api="http://api.iplantcollaborative.org/apidoc"
	version="2.0">
	<xsl:output method="xml" />
	
	<xsl:template match="api:apidoc">
	</xsl:template>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>