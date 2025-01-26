package me.yeon.week4.global.base.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ResponseDto {

  private final HttpStatus httpStatus;
}
