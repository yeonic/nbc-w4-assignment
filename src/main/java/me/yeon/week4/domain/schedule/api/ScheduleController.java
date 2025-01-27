package me.yeon.week4.domain.schedule.api;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.week4.domain.schedule.dao.ScheduleRepository;
import me.yeon.week4.domain.schedule.domain.Schedule;
import me.yeon.week4.domain.schedule.domain.ScheduleMapper;
import me.yeon.week4.domain.schedule.dto.AddScheduleRequest;
import me.yeon.week4.domain.schedule.dto.AddScheduleResponse;
import me.yeon.week4.domain.schedule.dto.DeleteScheduleRequest;
import me.yeon.week4.domain.schedule.dto.GetScheduleResponse;
import me.yeon.week4.domain.schedule.dto.UpdateScheduleRequest;
import me.yeon.week4.domain.schedule.dto.UpdateScheduleResponse;
import me.yeon.week4.domain.schedule.dto.UpdateScheduleResponse.UpdateScheduleResponseBuilder;
import me.yeon.week4.domain.user.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

  private final ScheduleRepository repository;

  @GetMapping
  public List<GetScheduleResponse> schedulesByOptions(
      @RequestParam(value = "updatedAt", required = false) String updatedAt,
      @RequestParam(value = "writerName", required = false) String writerName
  )
      throws SQLException {

    Timestamp update_time = updatedAt != null ? Timestamp.valueOf(updatedAt + " 00:00:00") : null;

    return repository.findByOptions(update_time, writerName)
        .stream()
        .map(ScheduleMapper::toGetResponseDto)
        .toList();
  }

  @GetMapping("/{scheduleId}")
  public GetScheduleResponse schedule(@PathVariable("scheduleId") long scheduleId)
      throws SQLException {

    return ScheduleMapper.toGetResponseDto(repository.findById(scheduleId));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AddScheduleResponse addSchedule(@RequestBody AddScheduleRequest req) throws SQLException {
    Long savedId = repository.save(req.getUserId(), req.getTodo(), req.getPassword());

    Schedule findSchedule = repository.findById(savedId);
    return ScheduleMapper.toAddResponseDto(findSchedule);
  }

  @PatchMapping("/{scheduleId}/update")
  public UpdateScheduleResponse update(
      @PathVariable("scheduleId") Long scheduleId,
      @RequestBody UpdateScheduleRequest req
  ) throws SQLException {

    if (!isValidPassword(scheduleId, req.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    UpdateScheduleResponseBuilder dtoBuilder = UpdateScheduleResponse.create()
        .schedule_id(scheduleId);

    if (hasField(req.getUsername())) {
      Long userId = repository.findById(scheduleId).getUserId();
      User updatedUser = repository.updateWriterName(userId, req.getUsername());
      dtoBuilder.username(updatedUser.getName());
    }

    if (hasField(req.getTodo())) {
      Schedule updatedSchedule = repository.updateTodoContent(scheduleId, req.getTodo());
      dtoBuilder.todo(updatedSchedule.getTodo());
    }

    return dtoBuilder.build();
  }

  @DeleteMapping("/{scheduleId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long scheduleId, @RequestBody DeleteScheduleRequest req)
      throws SQLException {

    if (!isValidPassword(scheduleId, req.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    repository.delete(scheduleId);
  }

  private boolean isValidPassword(Long scheduleId, String password) throws SQLException {
    return repository.checkPassword(scheduleId, password);
  }

  private boolean hasField(String field) {
    return field != null && !field.isEmpty();
  }
}
