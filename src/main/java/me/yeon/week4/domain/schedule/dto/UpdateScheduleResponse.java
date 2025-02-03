package me.yeon.week4.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
@Builder
public class UpdateScheduleResponse {

  private Long schedule_id;
  private String todo;
  private String username;
}
