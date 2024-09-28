update
<#if schemaName?has_content>${schemaName}.</#if>${tableName} set
<#list data.getColumnNames()?filter(key -> key != idName) as key>
    ${key} = :${key}<#if key_has_next>,</#if>
</#list>
where ${idName} = :${idName}