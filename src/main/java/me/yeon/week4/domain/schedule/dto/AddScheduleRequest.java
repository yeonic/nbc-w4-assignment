package me.yeon.week4.domain.schedule.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddScheduleRequest {

  @NotBlank
  private long userId;

  @NotBlank(message = "할일을 입력해주세요.")
  @Size(max = 200, message = "할일은 최대 200자까지 입력 가능힙니다.")
  private String todo;

  @NotBlank(message = "비밀번호를 입력해 주세요.")
  private String password;
}
