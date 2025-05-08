package dev.onebuild.db.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkflowStepType {
  DB_INSERT("db-insert"),
  DB_UPDATE("db-update"),
  DB_DELETE("db-delete"),
  DB_SELECT_ONE("db-select-one"),
  DB_SELECT_MANY("db-select-many"),
  HTTP_GET("http-get"),
  HTTP_POST("http-post"),
  HTTP_PATCH("http-patch"),
  HTTP_DELETE("http-delete");

  private final String jsonValue;

  WorkflowStepType(String jsonValue) {
    this.jsonValue = jsonValue;
  }

  @JsonValue
  public String toJson() {
    return this.jsonValue;
  }

  @JsonCreator
  public static WorkflowStepType fromJson(String value) {
    for (WorkflowStepType type : values()) {
      if (type.jsonValue.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid WorkflowStepType: " + value);
  }
}