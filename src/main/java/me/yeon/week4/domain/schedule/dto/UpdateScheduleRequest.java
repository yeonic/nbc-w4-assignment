package me.yeon.week4.domain.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateScheduleRequest {

  private String todo;
  private String username;
  private String password;
}
