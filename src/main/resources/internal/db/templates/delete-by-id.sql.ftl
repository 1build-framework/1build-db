delete from
    <#if schemaName?has_content>
    ${schemaName}.
    </#if>
    ${tableName} where ${idName} = :${idName}