package me.yeon.week4.domain.user.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class User {

  private long userId;
  private final String name;
  private final String email;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
