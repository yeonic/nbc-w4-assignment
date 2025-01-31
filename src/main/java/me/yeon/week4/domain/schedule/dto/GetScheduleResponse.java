package me.yeon.week4.domain.schedule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderMethodName = "create")
public class GetScheduleResponse {

  private long scheduleId;
  private long userId;
  private String todo;
}
