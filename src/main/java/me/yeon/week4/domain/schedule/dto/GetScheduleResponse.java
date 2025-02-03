package me.yeon.week4.domain.schedule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderMethodName = "create")
public class GetScheduleResponse {

  private Long scheduleId;
  private Long userId;
  private String todo;
}
