<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:mule="http://www.mulesource.org/schema/mule/core/2.2"
	xmlns:http="http://www.mulesource.org/schema/mule/http/2.2"
    xmlns:restlet="http://www.mulesource.org/schema/mule/restlet/2.2"
    xmlns:api="http://api.iplantcollaborative.org/apidoc"
	version="2.0">
	<xsl:output method="text" />
	
<!--	<xsl:variable name="baseUrl">
		<xsl:value-of select="concat(/mule:mule/mule:model/mule:service[@name=&quot;RestfulServices&quot;]/mule:inbound/http:inbound-endpoint/@host,':',
		/mule:mule/mule:model/mule:service[@name=&quot;RestfulServices&quot;]/mule:inbound/http:inbound-endpoint/@port)" />
	</xsl:variable>-->

	<xsl:template match="/">
		<!-- File header -->
		<xsl:text>iPlant Collaborative Discovery Environment API

</xsl:text>

		<xsl:apply-templates select="/mule:mule/mule:model/mule:service[@name=&quot;RestfulServices&quot;]/mule:inbound/http:inbound-endpoint" />
		<xsl:apply-templates select="/mule:mule/mule:model/mule:service[@name=&quot;RestfulServices&quot;]/mule:outbound//api:apidoc" />

		<!-- File footer -->
		<xsl:text></xsl:text>
	</xsl:template>
	
	<xsl:template match="http:inbound-endpoint">
		<xsl:text>* Services accessible on port </xsl:text>
		<xsl:value-of select="@port" />
		<xsl:text>

</xsl:text>
	</xsl:template>

	<xsl:template match="api:apidoc">
		<!-- Entry header -->
		<xsl:text>** </xsl:text>
		
		<xsl:value-of select="api:name" />
		<xsl:text>

</xsl:text>

		<xsl:text>*--:--+
Method|</xsl:text>
		
		<xsl:text>&lt;&lt;</xsl:text>
		<xsl:value-of select="../restlet:uri-template-filter/@verbs" />
		<xsl:text>&gt;&gt;</xsl:text>
		<xsl:text> </xsl:text>
		<xsl:value-of select="replace(replace(replace(../restlet:uri-template-filter/@pattern,
			'set-(payload|property).', ''),
			'\{', '\\{'),
			'\}', '\\}')" />

		<xsl:text>
*--:--+
Request Body|</xsl:text>
		
		<xsl:value-of select="api:request" />

		<xsl:text>
*--:--+
Possible Response Bodies</xsl:text>

		<xsl:apply-templates select="//api:response" />

		<xsl:text>*--:--+

	</xsl:text>

		<xsl:value-of select="api:description" />
		<xsl:text>

</xsl:text>

		<!-- Entry footer -->
		<xsl:text></xsl:text>
	</xsl:template>
	
	<xsl:template match="api:response">
		<xsl:text> |</xsl:text>
		<xsl:value-of select="." />
		<xsl:text>
</xsl:text>
	</xsl:template>
</xsl:stylesheet>
