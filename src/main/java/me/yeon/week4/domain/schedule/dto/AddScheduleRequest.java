package me.yeon.week4.domain.schedule.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddScheduleRequest {

  private long userId;
  private String todo;
  private String password;
}
