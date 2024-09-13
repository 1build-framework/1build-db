update
<#if schemaName?has_content>
  ${schemaName}.
</#if>
  ${tableName} set
<#list data.getColumnNames() as key>
<#if key != idName>
${key} = :${key} <#if key_has_next>,</#if>
</#if>
</#list>
where ${idName} = :${idName}