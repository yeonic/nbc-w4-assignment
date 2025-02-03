package me.yeon.week4.domain.schedule.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ScheduleWithUsername {

  private Long scheduleId;
  private final Long userId;
  private final String username;
  private final String todo;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
