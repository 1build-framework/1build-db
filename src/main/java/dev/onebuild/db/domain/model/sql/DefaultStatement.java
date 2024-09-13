package dev.onebuild.db.domain.model.sql;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public enum DefaultStatement {
  FIND_BY_ID("FIND_BY_ID", "find-by-id.sql.ftl"),
  FIND_ALL("FIND_ALL", "find-all.sql.ftl"),
  INSERT_ONE("INSERT_ONE", "insert-one.sql.ftl"),
  UPDATE_BY_ID("UPDATE_BY_ID", "update-by-id.sql.ftl"),
  DELETE_BY_ID("DELETE_BY_ID", "delete-by-id.sql.ftl");

  private final String name;
  private final String value;

  DefaultStatement(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public static DefaultStatement fromName(String name) {
    for (DefaultStatement statement : DefaultStatement.values()) {
      if (statement.name.equals(name)) {
        return statement;
      }
    }
    return null;
  }

  public static String listAllNames() {
    return Arrays.stream(DefaultStatement.values()).map(DefaultStatement::getName).collect(Collectors.joining(", "));
  }

  @Override
  public String toString() {
    return value;
  }
}