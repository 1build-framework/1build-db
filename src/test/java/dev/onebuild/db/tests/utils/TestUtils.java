package dev.onebuild.db.tests.utils;

import dev.onebuild.db.domain.model.config.DatabaseInfo;
import dev.onebuild.db.domain.model.sql.DefaultStatement;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtils {
  public static void validateDbInfo(String datasource,
                                    String schema,
                                    String table,
                                    String id,
                                    String statement,
                                    String actionType,
                                    DatabaseInfo dbInfo) {
    assertEquals(datasource, dbInfo.getDataSource());
    assertEquals(statement, dbInfo.getStatement());
    assertEquals(schema, dbInfo.getSchema());
    assertEquals(table, dbInfo.getTable());
    assertEquals(id, dbInfo.getId());
    if(dbInfo.getActionType() != null) {
      assertEquals(actionType, dbInfo.getActionType().toString());
    }
  }

  public static void validateDefaultStatements(Map<String, String> statements) {
    assertNotNull(statements);
    assertEquals(5, statements.size());

    assertTrue(statements.containsKey(DefaultStatement.FIND_BY_ID.getValue()));
    assertNotNull(statements.get(DefaultStatement.FIND_BY_ID.getValue()));

    assertTrue(statements.containsKey(DefaultStatement.FIND_ALL.getValue()));
    assertNotNull(statements.get(DefaultStatement.FIND_ALL.getValue()));

    assertTrue(statements.containsKey(DefaultStatement.INSERT.getValue()));
    assertNotNull(statements.get(DefaultStatement.INSERT.getValue()));

    assertTrue(statements.containsKey(DefaultStatement.UPDATE.getValue()));
    assertNotNull(statements.get(DefaultStatement.UPDATE.getValue()));

    assertTrue(statements.containsKey(DefaultStatement.DELETE.getValue()));
    assertNotNull(statements.get(DefaultStatement.DELETE.getValue()));
  }
}