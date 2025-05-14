package dev.onebuild.db.domain.service;

import dev.onebuild.commons.domain.model.db.DefaultOneBuildRecord;
import dev.onebuild.commons.domain.model.db.OneBuildRecord;
import dev.onebuild.commons.errors.ErrorCode;
import dev.onebuild.commons.errors.ErrorReason;
import dev.onebuild.db.domain.model.DbWorkflowStep;
import dev.onebuild.db.domain.model.HttpWorkflowStep;
import dev.onebuild.db.domain.model.WorkflowStep;
import dev.onebuild.db.domain.model.WorkflowStepType;
import dev.onebuild.db.exception.WorkflowException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static dev.onebuild.commons.domain.model.db.OneBuildTypeMappings.convertToSqlParameters;

@Slf4j
public class WorkflowExecutorImpl extends AbstractWorkflowExecutor {

  private final Map<String, NamedParameterJdbcTemplate> jdbcTemplates;
  private final freemarker.template.Configuration templateConfiguration;

  public WorkflowExecutorImpl(freemarker.template.Configuration templateConfiguration,
                              Map<String, NamedParameterJdbcTemplate> jdbcTemplates,
                              ExecutorService executorService) {
    super(executorService);
    this.templateConfiguration = templateConfiguration;
    this.jdbcTemplates = jdbcTemplates;
  }

  @Override
  protected void executeInternal(Map<String, Object> parameters,
                                 WorkflowStep step,
                                 WorkflowResult result) {
    if(step instanceof DbWorkflowStep) {
      executeDbWorkflow(parameters, (DbWorkflowStep) step, result);
    } else if(step instanceof HttpWorkflowStep) {
      executeHttpWorkflow(parameters, (HttpWorkflowStep) step);
    }
  }

  private void executeDbWorkflow(Map<String, Object> parameters, DbWorkflowStep step, WorkflowResult result) {
    if (step.getStatement() != null && step.getSourceDb() != null) {
      String jdbcKey = step.getSourceDb() + "-jdbc-template";
      NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplates.get(jdbcKey);

      if (jdbcTemplate == null) {
        throw new IllegalArgumentException("JdbcTemplate not found for: " + jdbcKey);
      }

      String sql;
      try {
        sql = renderTemplate(step.getStatement(), parameters);
      } catch(Exception e) {
        log.error("Error occurred while rendering template for step: {}", step, e);
        throw new WorkflowException(ErrorCode.DB_ERROR, e, step);
      }

      if(StringUtils.isBlank(sql)) {
        log.warn("No statement generated from template for step: {}", step);
        return;
      }

      //DML
      if (step.getType() == WorkflowStepType.DB_INSERT ||
          step.getType() == WorkflowStepType.DB_UPDATE ||
          step.getType() == WorkflowStepType.DB_DELETE) {

        @SuppressWarnings("SqlSourceToSinkFlow")
        int update = jdbcTemplate.update(sql, convertToSqlParameters(parameters));
        log.debug("SQL name: {}, Total impacted: {} ", step.getName(), update);

        if(update == 0 && step.isFailOnZero() ) {
          throw new WorkflowException(ErrorCode.DB_ERROR, ErrorReason.ZERO_UPDATE, step);
        }

        result.set(step.getName(), update);
        parameters.put(step.getName(), update);

      //Projection
      } else {
        List<OneBuildRecord> records = jdbcTemplate.query(
            sql,
            parameters,
            (rs, rowNum) -> DefaultOneBuildRecord.from(rs)
        );

        if(records.isEmpty() && step.isFailOnZero()) {
          throw new WorkflowException(ErrorCode.DB_ERROR, ErrorReason.ZERO_SELECT, step);
        }

        if (step.getType() == WorkflowStepType.DB_SELECT_ONE) {
          if(!records.isEmpty()) {
            if(step.isSingleColumn()) {
              Object value = records.getFirst().getFirst();
              result.set(step.getName(), value);
              parameters.put(step.getName(), value);
            } else {
              result.set(step.getName(), records.getFirst());
              parameters.put(step.getName(), records.getFirst());
            }
          }
        } else {
          result.set(step.getName(), records);
          parameters.put(step.getName(), records);
        }
      }
    }
  }

  private void executeHttpWorkflow(Map<String, Object> parameters, HttpWorkflowStep step) {
  }

  private String renderTemplate(String template, Map<String, Object> parameters) {
    StringWriter writer = new StringWriter();
    try {
      templateConfiguration.getTemplate(template).process(parameters, writer);
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
    return writer.toString();
  }
}