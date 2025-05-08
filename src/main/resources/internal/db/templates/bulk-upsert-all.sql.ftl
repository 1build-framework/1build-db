<#-- FreeMarker template for bulk upsert (insert or update on conflict) -->
<#-- Accepts the following parameters:
     tableName: The name of the table
     columns: A list of column names
     conflictColumns: A list of column names for the conflict clause -->
INSERT INTO <#if schemaName?has_content>${schemaName}.</#if>${tableName} (
<#list columns as column>
    ${column}<#if column_has_next>,</#if>
</#list>
) VALUES
(
<#list columns as column>
    :${column}<#if column_has_next>,</#if>
</#list>
)
ON CONFLICT (${conflictColumns?join(', ')})
DO UPDATE SET
<#list columns as column>
${column} = EXCLUDED.${column}<#if column_has_next>,</#if>
</#list>;