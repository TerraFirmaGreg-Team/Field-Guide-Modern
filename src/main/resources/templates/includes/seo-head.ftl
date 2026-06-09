<meta name="title" content="${long_title?html}" />
<meta name="description" content="${short_description?html}" />
<#if canonicalUrl?has_content>
<link rel="canonical" href="${canonicalUrl?html}" />
<meta name="robots" content="index, follow" />
<meta property="og:type" content="website" />
<meta property="og:site_name" content="${title?html}" />
<meta property="og:url" content="${canonicalUrl?html}" />
<meta property="og:title" content="${long_title?html}" />
<meta property="og:description" content="${short_description?html}" />
<#if ogImageUrl?has_content>
<meta property="og:image" content="${ogImageUrl?html}" />
<meta name="twitter:card" content="summary" />
<meta name="twitter:title" content="${long_title?html}" />
<meta name="twitter:description" content="${short_description?html}" />
<meta name="twitter:image" content="${ogImageUrl?html}" />
</#if>
<#if jsonLd?has_content>
<script type="application/ld+json">${jsonLd}</script>
</#if>
</#if>
