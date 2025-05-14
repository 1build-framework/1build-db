package dev.onebuild.db.utils;

import dev.onebuild.commons.domain.model.db.DatabaseInfo;
import dev.onebuild.commons.domain.model.db.OneBuildRecord;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class OneBuildDataRepositoryHelper {
  private final freemarker.template.Configuration freemarkerConfiguration;
  public OneBuildDataRepositoryHelper(freemarker.template.Configuration freemarkerConfiguration) {
    this.freemarkerConfiguration = freemarkerConfiguration;
  }

  public String getSql(DatabaseInfo dbInfo, OneBuildRecord record) {
    StringWriter writer = new StringWriter();

    Map<String, Object> model = new HashMap<>();
    model.put("idName", dbInfo.getId());
    model.put("tableName", dbInfo.getTable());
    model.put("schemaName", dbInfo.getSchema());
    model.put("data", record);

    try {
      freemarkerConfiguration
          .getTemplate(dbInfo.getStatement())
          .process(model, writer);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    String sql = writer.toString();

    log.debug("Statement is {}", sql);
    return sql;
  }

  public String getSql(DatabaseInfo dbInfo, Map<String, Object> params) {
    StringWriter writer = new StringWriter();

    Map<String, Object> model = new HashMap<>();
    model.put("idName", dbInfo.getId());
    model.put("tableName", dbInfo.getTable());
    model.put("schemaName", dbInfo.getSchema());
    model.put("dynamicParams", params);

    try {
      freemarkerConfiguration
          .getTemplate(dbInfo.getStatement())
          .process(model, writer);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    String sql = writer.toString();

    log.debug("Statement is {}", sql);
    return sql;
  }

  public String getSql(DatabaseInfo dbInfo, List<OneBuildRecord> records, List<String> conflictColumns) {
    StringWriter writer = new StringWriter();

    Map<String, Object> model = new HashMap<>();
    model.put("schemaName", dbInfo.getSchema());
    model.put("tableName", dbInfo.getTable());
    model.put("columns", records.getFirst().getColumnNames());
    model.put("conflictColumns", conflictColumns);

    try {
      freemarkerConfiguration
          .getTemplate(dbInfo.getStatement())
          .process(model, writer);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    String sql = writer.toString();
    log.debug("Statement is {}", sql);
    return sql;
  }
}