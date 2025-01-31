package me.yeon.week4.domain.schedule.domain;

import me.yeon.week4.domain.schedule.dto.AddScheduleResponse;
import me.yeon.week4.domain.schedule.dto.GetFilteredScheduleResponse;
import me.yeon.week4.domain.schedule.dto.GetScheduleResponse;

public class ScheduleMapper {

  public static GetScheduleResponse toGetResponseDto(Schedule schedule) {
    return GetScheduleResponse.create()
        .scheduleId(schedule.getScheduleId())
        .userId(schedule.getUserId())
        .todo(schedule.getTodo())
        .build();
  }

  public static AddScheduleResponse toAddResponseDto(Schedule schedule) {
    return AddScheduleResponse.create()
        .scheduleId(schedule.getScheduleId())
        .userId(schedule.getUserId())
        .todo(schedule.getTodo())
        .build();
  }

  public static GetFilteredScheduleResponse toGetFilteredResponse(ScheduleWithUsername schedule) {
    return GetFilteredScheduleResponse.create()
        .scheduleId(schedule.getScheduleId())
        .userId(schedule.getUserId())
        .username(schedule.getUsername())
        .todo(schedule.getTodo())
        .build();
  }

}
