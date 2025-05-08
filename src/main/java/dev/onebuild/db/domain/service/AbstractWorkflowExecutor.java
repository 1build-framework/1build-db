package dev.onebuild.db.domain.service;

import dev.onebuild.db.domain.model.DbWorkflowStep;
import dev.onebuild.db.domain.model.WorkflowStep;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Log4j2
public abstract class AbstractWorkflowExecutor implements WorkflowExecutor {

  private final ExecutorService executorService;

  public AbstractWorkflowExecutor(ExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public final void execute(
    Map<String, Object> parameters,
    WorkflowStep step,
    WorkflowResult result
  ) {
    executeWorkflow(new ConcurrentHashMap<>(parameters), step, result);
  }

  protected void executeWorkflow(
      Map<String, Object> parameters,
      WorkflowStep step,
      WorkflowResult result
  ) {
    if (step instanceof DbWorkflowStep dbStep) {
      try {
        // 1. Run dependencies in parallel
        List<WorkflowStep> dependencies = dbStep.getDependencies();
        if (dependencies != null && !dependencies.isEmpty()) {
          List<Callable<Void>> tasks = new ArrayList<>();
          for (WorkflowStep dependency : dependencies) {
            tasks.add(() -> {
              executeWorkflow(parameters, dependency, result);
              return null;
            });
          }

          List<Future<Void>> futures = executorService.invokeAll(tasks);
          for (Future<Void> future : futures) {
            try {
              future.get(); // trigger exception if any
            } catch (ExecutionException e) {
              if (dbStep.isStopOnFailure()) {
                throw new RuntimeException("Dependency failed for: " + dbStep.getName(), e.getCause());
              }
            }
          }
        }

        // 2. Execute the current DB step if it has a statement
        executeInternal(parameters, step, result);

      } catch (Exception ex) {
        if (dbStep.isStopOnFailure()) {
          throw new RuntimeException("Execution failed at step: " + dbStep.getName(), ex);
        } else {
          log.error("Exception while running workflow step " + step.getName() + ". Continue with the flow", ex);
        }
      }
    } else {
      throw new UnsupportedOperationException("Unsupported WorkflowStep type: " + step.getClass().getSimpleName());
    }
  }

  protected abstract void executeInternal(Map<String, Object> parameters, WorkflowStep step, WorkflowResult result);
}
