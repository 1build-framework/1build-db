package dev.onebuild.db.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DbWorkflowStep implements WorkflowStep {

  private String name;
  private WorkflowStepType type;

  @JsonProperty("source-db")
  private String sourceDb;

  private String statement;

  @JsonProperty("stop-on-failure")
  private boolean stopOnFailure = true;

  @JsonProperty("fail-on-zero")
  private boolean failOnZero = false;

  @JsonProperty("single-column")
  private boolean singleColumn = false;

  private List<WorkflowStep> dependencies;
}
