package dev.onebuild.db.domain.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkflowResult {
  private final Map<String, Object> stepResults = new ConcurrentHashMap<>();

  public Object getStepResult(String key) {
    return stepResults.get(key);
  }

  public void set(String key, Object value) {
    stepResults.put(key, value);
  }
}