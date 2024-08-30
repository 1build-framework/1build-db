package dev.onebuild.db.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbInfoConfig {
  private String dataSource;
  private String schema;
  private String table;
  private String id;
  private String statement;
}