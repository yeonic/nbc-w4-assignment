package me.yeon.week4.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserAddRequest {

  private String name;
  private String email;
}
