package dev.onebuild.db.domain.model.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainInfo {
  private DatabaseInfo dbInfo;
  private List<ActionInfo> actions = new ArrayList<>();

  public ActionInfo findAction(String actionName) {
    if(actions == null) {
      return null;
    }
    return actions.stream().filter(action -> action.getName().equalsIgnoreCase(actionName)).findFirst().orElse(null);
  }
}