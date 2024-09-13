package dev.onebuild.db.domain.model.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionInfo {
  private String name;
  private String actionType;
  private DatabaseInfo dbInfo;
}