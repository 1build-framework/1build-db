select * from
<#if schemaName?has_content>
    ${schemaName}.
</#if>
${tableName}

<#-- Build the WHERE clause only if dynamicParams is not empty -->
<#if dynamicParams?has_content>
  where
    <#list dynamicParams?keys as paramName>
        <#assign paramValue = dynamicParams[paramName]>

    <#-- Check if the value is a list for multi-value parameters -->
        <#if paramValue?is_sequence>
            ${paramName} in ( :${paramName} )
        <#else>
            ${paramName} = :${paramName}
        </#if>

    <#-- Add "and" if there are more parameters to add -->
        <#if paramName?index < (dynamicParams?size - 1)>
          and
        </#if>
    </#list>
</#if>

order by ${idName}
