package me.yeon.week4.global.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public enum ErrorCode {
  // Common
  ARGUMENT_NOT_VALID("COMMON_001", "method argument not valid"),

  // Standard
  ILLEGAL_STATE("STANDARD_001", "illegal state"),
  ILLEGAL_ARGUMENT("STANDARD_002", "illegal argument"),

  // Data Access
  DATA_ACCESS_EXCEPTION("DATA_001", "error accessing data"),

  // Finally
  EXCEPTION("EXCEPTION", "exception");

  private final String code;
  private final String message;
}
