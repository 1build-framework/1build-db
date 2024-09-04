package dev.onebuild.db.domain.model.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainInfo {
  private DatabaseInfo dbInfo;
  private Map<String, ActionInfo> actions = new HashMap<>();
}