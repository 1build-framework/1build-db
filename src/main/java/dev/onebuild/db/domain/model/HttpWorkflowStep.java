package dev.onebuild.db.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class HttpWorkflowStep implements WorkflowStep {
  private String name;
  private WorkflowStepType type;

  private String url;
  private boolean stopOnFailure = true;

  @Override
  public boolean isFailOnZero() {
    return false;
  }

  @Override
  public List<WorkflowStep> getDependencies() {
    return List.of();
  }
}