package dev.onebuild.db.domain.model.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseInfo {
  private String dataSource;
  private String schema;
  private String table;
  private String id;
  private String statement;
  private ActionType actionType;
}