delete from
<#if schemaName?has_content>
${schemaName}.
</#if>
${tableName} where ${idName} in (:ids)