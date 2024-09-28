package dev.onebuild.db.utils;

import dev.onebuild.domain.model.db.DatabaseInfo;
import dev.onebuild.domain.model.db.OneBuildRecord;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class OneBuildDataRepositoryHelper {

  private final freemarker.template.Configuration freemarkerConfiguration;
  public OneBuildDataRepositoryHelper(freemarker.template.Configuration freemarkerConfiguration) {
    this.freemarkerConfiguration = freemarkerConfiguration;
  }

  public String getSql(DatabaseInfo dbInfo, OneBuildRecord record) {
    Map<String, Object> model = new HashMap<>();
    model.put("data", record);
    return getSql(model, dbInfo);
  }

  public String getSql(DatabaseInfo dbInfo) {
    Map<String, Object> model = new HashMap<>();
    return getSql(model, dbInfo);
  }

  public String getSql(Map<String, Object> model, DatabaseInfo dbInfo) {
    StringWriter writer = new StringWriter();
    model.put("idName", dbInfo.getId());
    model.put("tableName", dbInfo.getTable());
    model.put("schemaName", dbInfo.getSchema());

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