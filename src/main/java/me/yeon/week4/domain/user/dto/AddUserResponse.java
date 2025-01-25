package me.yeon.week4.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderMethodName = "create")
public class AddUserResponse {

  private long id;
  private String name;
  private String email;
}
