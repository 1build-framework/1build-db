insert into
<#if schemaName?has_content>
  ${schemaName}.
</#if>
  ${tableName} (
<#list data.getColumnNames() as key>
<#if key != idName>
${key}<#if key_has_next>,</#if>
</#if>
</#list>
)
values
(
<#list data.getColumnNames() as key>
  <#if key != idName>
:${key}<#if key_has_next>,</#if>
  </#if>
</#list>
)
