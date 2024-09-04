package dev.onebuild.db.domain.model.types;

import dev.onebuild.db.domain.model.types.DatabaseType;
import dev.onebuild.db.domain.model.types.OneBuildType;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class OneBuildTypes {
  private static final Map<OneBuildType, Integer> oneBuildToSqlTypeMap = new HashMap<>();
  private static final Map<Integer, OneBuildType> sqlToOneBuildTypeMap = new HashMap<>();
  private static final Map<Class<?>, Integer> javaToSqlTypeMap = new HashMap<>();
  private static final Map<Integer, Map<DatabaseType, String>> sqlToDbTypeMap = new HashMap<>();

  static {
    // OneBuildType --> JDBC SQL Types
    oneBuildToSqlTypeMap.put(OneBuildType.STRING, Types.VARCHAR);
    oneBuildToSqlTypeMap.put(OneBuildType.TEXT, Types.CLOB);
    oneBuildToSqlTypeMap.put(OneBuildType.INTEGER, Types.BIGINT);
    oneBuildToSqlTypeMap.put(OneBuildType.DATE, Types.DATE);
    oneBuildToSqlTypeMap.put(OneBuildType.DATETIME, Types.TIMESTAMP);
    oneBuildToSqlTypeMap.put(OneBuildType.DECIMAL, Types.DECIMAL);
    oneBuildToSqlTypeMap.put(OneBuildType.BOOLEAN, Types.BOOLEAN);
    oneBuildToSqlTypeMap.put(OneBuildType.BINARY, Types.BLOB);

    // JDBC SQL Types --> OneBuildType
    sqlToOneBuildTypeMap.put(Types.VARCHAR, OneBuildType.STRING);
    sqlToOneBuildTypeMap.put(Types.CLOB, OneBuildType.TEXT);
    sqlToOneBuildTypeMap.put(Types.INTEGER, OneBuildType.INTEGER);
    sqlToOneBuildTypeMap.put(Types.BIGINT, OneBuildType.INTEGER);
    sqlToOneBuildTypeMap.put(Types.DATE, OneBuildType.DATE);
    sqlToOneBuildTypeMap.put(Types.TIMESTAMP, OneBuildType.DATETIME);
    sqlToOneBuildTypeMap.put(Types.DECIMAL, OneBuildType.DECIMAL);
    sqlToOneBuildTypeMap.put(Types.BOOLEAN, OneBuildType.BOOLEAN);
    sqlToOneBuildTypeMap.put(Types.BLOB, OneBuildType.BINARY);

    // Java types --> JDBC SQL types
    javaToSqlTypeMap.put(String.class, Types.VARCHAR);
    javaToSqlTypeMap.put(Integer.class, Types.BIGINT);
    javaToSqlTypeMap.put(Long.class, Types.BIGINT);
    javaToSqlTypeMap.put(Float.class, Types.DOUBLE);
    javaToSqlTypeMap.put(Double.class, Types.DOUBLE);
    javaToSqlTypeMap.put(BigDecimal.class, Types.DOUBLE);
    javaToSqlTypeMap.put(Boolean.class, Types.BOOLEAN);
    javaToSqlTypeMap.put(java.util.Date.class, Types.TIMESTAMP);
    javaToSqlTypeMap.put(java.sql.Date.class, Types.DATE);
    javaToSqlTypeMap.put(java.sql.Timestamp.class, Types.TIMESTAMP);
    javaToSqlTypeMap.put(java.sql.Blob.class, Types.BLOB);

    // Mapping SQL types to database-specific types
    Map<DatabaseType, String> varcharMapping = Map.of(
        DatabaseType.H2, "VARCHAR",
        DatabaseType.POSTGRESQL, "TEXT"
    );
    Map<DatabaseType, String> clobMapping = Map.of(
        DatabaseType.H2, "CLOB",
        DatabaseType.POSTGRESQL, "TEXT"
    );
    Map<DatabaseType, String> integerMapping = Map.of(
        DatabaseType.H2, "INT",
        DatabaseType.POSTGRESQL, "INTEGER"
    );
    Map<DatabaseType, String> dateMapping = Map.of(
        DatabaseType.H2, "DATE",
        DatabaseType.POSTGRESQL, "DATE"
    );
    Map<DatabaseType, String> timestampMapping = Map.of(
        DatabaseType.H2, "TIMESTAMP",
        DatabaseType.POSTGRESQL, "TIMESTAMPTZ"
    );
    Map<DatabaseType, String> decimalMapping = Map.of(
        DatabaseType.H2, "DECIMAL",
        DatabaseType.POSTGRESQL, "NUMERIC"
    );
    Map<DatabaseType, String> booleanMapping = Map.of(
        DatabaseType.H2, "BOOLEAN",
        DatabaseType.POSTGRESQL, "BOOLEAN"
    );
    Map<DatabaseType, String> blobMapping = Map.of(
        DatabaseType.H2, "BLOB",
        DatabaseType.POSTGRESQL, "BYTEA"
    );

    sqlToDbTypeMap.put(Types.VARCHAR, varcharMapping);
    sqlToDbTypeMap.put(Types.CLOB, clobMapping);
    sqlToDbTypeMap.put(Types.INTEGER, integerMapping);
    sqlToDbTypeMap.put(Types.BIGINT, integerMapping);
    sqlToDbTypeMap.put(Types.DATE, dateMapping);
    sqlToDbTypeMap.put(Types.TIMESTAMP, timestampMapping);
    sqlToDbTypeMap.put(Types.DECIMAL, decimalMapping);
    sqlToDbTypeMap.put(Types.BOOLEAN, booleanMapping);
    sqlToDbTypeMap.put(Types.BLOB, blobMapping);
  }

  public static OneBuildType getOneBuildType(int sqlType) {
    return sqlToOneBuildTypeMap.get(sqlType);
  }

  public static OneBuildType getOneBuildType(Class<?> javaType) {
    int sqlType = getSqlType(javaType);
    return getOneBuildType(sqlType);
  }

  public static int getSqlType(OneBuildType oneBuildType) {
    return oneBuildToSqlTypeMap.get(oneBuildType);
  }

  public static int getSqlType(Class<?> javaType) {
    return javaToSqlTypeMap.get(javaType);
  }

  public static String getDatabaseType(int sqlType, DatabaseType dbType) {
    Map<DatabaseType, String> dbTypeMap = sqlToDbTypeMap.get(sqlType);
    if (dbTypeMap != null) {
      return dbTypeMap.get(dbType);
    }
    return null;
  }
}
