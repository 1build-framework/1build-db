package dev.onebuild.db.domain.model.sql;

public enum DefaultStatement {
  FIND_BY_ID("find-by-id"),
  FIND_ALL("find-all"),
  INSERT("insert"),
  UPDATE("update"),
  DELETE("delete-by-id");

  private final String value;

  DefaultStatement(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static DefaultStatement fromValue(String value) {
    for (DefaultStatement statement : DefaultStatement.values()) {
      if (statement.value.equals(value)) {
        return statement;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }

  @Override
  public String toString() {
    return value;
  }
}