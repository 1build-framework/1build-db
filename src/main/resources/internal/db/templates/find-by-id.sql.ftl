select * from
<#if schemaName?has_content>
${schemaName}.
</#if>
${tableName}
where id = :id