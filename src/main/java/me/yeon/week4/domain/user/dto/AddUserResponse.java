package me.yeon.week4.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddUserResponse {

  private Long id;
  private String name;
  private String email;
}
