package me.yeon.week4.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
@Builder(builderMethodName = "create")
public class GetFilteredScheduleResponse {

  private Long scheduleId;
  private Long userId;
  private String todo;
  private String username;

}
