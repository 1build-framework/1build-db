package dev.onebuild.db.domain.service;

import dev.onebuild.db.domain.model.WorkflowStep;

import java.util.Map;

public interface WorkflowExecutor {
  void execute(
      Map<String, Object> parameters,
      WorkflowStep step,
      WorkflowResult result
  );
}