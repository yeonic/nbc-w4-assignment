package me.yeon.week4.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddUserRequest {

  @NotBlank(message = "이름을 입력해주세요.")
  private String name;

  @NotBlank(message = "이메일을 입력해주세요.")
  @Email(message = "이메일 형식이 잘못되었습니다.", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
  private String email;
}
