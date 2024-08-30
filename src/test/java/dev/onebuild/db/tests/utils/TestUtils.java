package dev.onebuild.db.tests.utils;

import dev.onebuild.db.domain.model.DbInfoConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {
  public static void validateDbInfo(String datasource, String schema, String table, String id, String statement, DbInfoConfig rootDbInfo) {
    assertEquals(datasource, rootDbInfo.getDataSource());
    assertEquals(schema, rootDbInfo.getSchema());
    assertEquals(table, rootDbInfo.getTable());
    assertEquals(id, rootDbInfo.getId());
    assertEquals(statement, rootDbInfo.getStatement());
  }
}
