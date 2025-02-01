package me.yeon.week4.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.error("handleMethodArgumentNotValidException", e);
    BindingResult bindingResult = e.getBindingResult();
    return ErrorResponse.of(ErrorCode.ARGUMENT_NOT_VALID, bindingResult);
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalStateException(IllegalStateException e) {
    log.error("handleIllegalStateException", e);
    return ErrorResponse.of(ErrorCode.ILLEGAL_STATE, e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("handleIllegalArgumentException", e);
    return ErrorResponse.of(ErrorCode.ILLEGAL_ARGUMENT, e.getMessage());
  }

  @ExceptionHandler(DataAccessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleDataAccessException(DataAccessException e) {
    log.error("handleDataAccessException", e);
    return ErrorResponse.of(ErrorCode.DATA_ACCESS_EXCEPTION, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleException(Exception e) {
    log.error("handleException");
    return ErrorResponse.of(ErrorCode.EXCEPTION, e.getMessage());
  }
}
