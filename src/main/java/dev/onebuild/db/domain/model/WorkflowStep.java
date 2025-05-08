package dev.onebuild.db.domain.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DbWorkflowStep.class, name = "db-insert"),
    @JsonSubTypes.Type(value = DbWorkflowStep.class, name = "db-update"),
    @JsonSubTypes.Type(value = DbWorkflowStep.class, name = "db-delete"),
    @JsonSubTypes.Type(value = DbWorkflowStep.class, name = "db-select-one"),
    @JsonSubTypes.Type(value = DbWorkflowStep.class, name = "db-select-many"),

    @JsonSubTypes.Type(value = HttpWorkflowStep.class, name = "http-get"),
    @JsonSubTypes.Type(value = HttpWorkflowStep.class, name = "http-post"),
    @JsonSubTypes.Type(value = HttpWorkflowStep.class, name = "http-patch"),
    @JsonSubTypes.Type(value = HttpWorkflowStep.class, name = "http-delete")
})
public interface WorkflowStep {
  String getName();
  WorkflowStepType getType();

  boolean isStopOnFailure();

  boolean isFailOnZero();

  List<WorkflowStep> getDependencies();
}