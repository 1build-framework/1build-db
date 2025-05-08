insert into
<#if schemaName?has_content>${schemaName}.</#if>${tableName} (
<#list data.getColumnNames()?filter(key -> key != idName) as key>
    ${key}<#if key_has_next>,</#if>
</#list>
)
values
(
<#list data.getColumnNames()?filter(key -> key != idName) as key>
  :${key}<#if key_has_next>,</#if>
</#list>
)