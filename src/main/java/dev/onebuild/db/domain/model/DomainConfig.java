package dev.onebuild.db.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainConfig {
  private DbInfoConfig dbInfo;
  private Map<String, ActionConfig> actions = new HashMap<>();
}