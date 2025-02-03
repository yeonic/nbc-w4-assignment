package me.yeon.week4.domain.user.domain;

import me.yeon.week4.domain.user.dto.AddUserResponse;

public class UserMapper {

  public static AddUserResponse toAddResponse(User user) {
    return AddUserResponse.builder()
        .id(user.getUserId())
        .name(user.getName())
        .email(user.getEmail())
        .build();
  }

}
