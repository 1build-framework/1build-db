package dev.onebuild.db.domain.model.config;

import dev.onebuild.db.domain.model.sql.DefaultStatement;

public enum ActionType {
  CUSTOM("custom"),
  DEFAULT("default");

  private final String value;

  ActionType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static ActionType fromValue(String value) {
    for (ActionType actionType : ActionType.values()) {
      if (actionType.value.equals(value)) {
        return actionType;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }

  @Override
  public String toString() {
    return value;
  }
}
