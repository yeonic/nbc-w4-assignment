package me.yeon.week4.domain.schedule.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Schedule {

  private long scheduleId;

  private final long userId;
  private final String todo;

  @JsonIgnore
  private final String password;
}
