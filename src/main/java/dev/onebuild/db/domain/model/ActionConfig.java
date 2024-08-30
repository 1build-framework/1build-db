package dev.onebuild.db.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionConfig {
  private String type;
  private DbInfoConfig info;
}