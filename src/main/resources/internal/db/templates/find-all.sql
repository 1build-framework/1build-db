select * from
        <#if schemaName?has_content>
    ${schemaName}.
        </#if>
    ${tableName}
order by ${idName}