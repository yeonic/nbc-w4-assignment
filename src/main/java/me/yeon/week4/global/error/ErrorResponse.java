package me.yeon.week4.global.error;

import java.util.List;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
public class ErrorResponse {

  private String code;
  private String message;
  private List<FieldErrorWrapper> errors;

  private ErrorResponse(ErrorCode code, List<FieldError> errors) {
    this.code = code.getCode();
    this.message = code.getMessage();
    this.errors = errors.stream().map(FieldErrorWrapper::new).toList();
  }

  private ErrorResponse(ErrorCode code, String exceptionMessage) {
    this.code = code.getCode();
    this.message = code.getMessage();
    this.errors = List.of(new FieldErrorWrapper("", "", exceptionMessage));
  }

  public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
    return new ErrorResponse(errorCode, bindingResult.getFieldErrors());
  }

  public static ErrorResponse of(ErrorCode errorCode, String exceptionMessage) {
    return new ErrorResponse(errorCode, exceptionMessage);
  }

  @Getter
  static class FieldErrorWrapper {

    private final String field;
    private final String rejectedValue;
    private final String reason;

    private FieldErrorWrapper(String field, String rejectedValue, String reason) {
      this.field = field;
      this.rejectedValue = rejectedValue;
      this.reason = reason;
    }

    private FieldErrorWrapper(FieldError error) {
      field = error.getField();
      reason = error.getDefaultMessage();

      Object rejectedValueObject = error.getRejectedValue();
      rejectedValue = rejectedValueObject == null ? "" : rejectedValueObject.toString();
    }
  }
}
