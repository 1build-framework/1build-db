package dev.onebuild.db.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.onebuild.db.domain.model.DbWorkflowStep;
import dev.onebuild.db.domain.model.WorkflowStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public final class WorkflowStepBuilder {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static WorkflowStep buildForDatabase(String path) {
    ClassPathResource resource = new ClassPathResource(path);
    try {
      return MAPPER.readValue(resource.getInputStream(), DbWorkflowStep.class);
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
}
