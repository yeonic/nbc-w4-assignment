package me.yeon.week4.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddUserResponse {

  private long id;
  private String email;
  private String name;
}
