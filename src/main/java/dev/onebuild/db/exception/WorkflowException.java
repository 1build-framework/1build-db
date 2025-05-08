package dev.onebuild.db.exception;

import dev.onebuild.db.domain.model.WorkflowStep;
import dev.onebuild.errors.ErrorCode;
import dev.onebuild.errors.ErrorReason;
import dev.onebuild.errors.OneBuildException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(of = { "errorCode", "errorReason", "step" })
public class WorkflowException extends RuntimeException implements OneBuildException {
  private final ErrorCode errorCode;
  private final ErrorReason errorReason;
  private final WorkflowStep step;
  private final String exceptionMessage;

  public WorkflowException(ErrorCode errorCode, Throwable e, WorkflowStep step) {
    super(e);
    this.errorCode = errorCode;
    this.errorReason = ErrorReason.SYSTEM;
    this.step = step;
    this.exceptionMessage = ExceptionUtils.getStackTrace(e);
  }

  public WorkflowException(ErrorCode errorCode, ErrorReason errorReason, WorkflowStep step) {
    super();
    this.errorCode = errorCode;
    this.errorReason = errorReason;
    this.step = step;
    this.exceptionMessage = "";
  }
}