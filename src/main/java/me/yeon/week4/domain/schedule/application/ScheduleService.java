package me.yeon.week4.domain.schedule.application;

import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.week4.domain.schedule.dao.ScheduleRepository;
import me.yeon.week4.domain.schedule.domain.Schedule;
import me.yeon.week4.domain.schedule.domain.ScheduleMapper;
import me.yeon.week4.domain.schedule.domain.ScheduleWithUsername;
import me.yeon.week4.domain.schedule.dto.AddScheduleRequest;
import me.yeon.week4.domain.schedule.dto.AddScheduleResponse;
import me.yeon.week4.domain.schedule.dto.DeleteScheduleRequest;
import me.yeon.week4.domain.schedule.dto.GetFilteredScheduleResponse;
import me.yeon.week4.domain.schedule.dto.GetScheduleResponse;
import me.yeon.week4.domain.schedule.dto.UpdateScheduleRequest;
import me.yeon.week4.domain.schedule.dto.UpdateScheduleResponse;
import me.yeon.week4.domain.schedule.dto.UpdateScheduleResponse.UpdateScheduleResponseBuilder;
import me.yeon.week4.domain.user.domain.User;
import me.yeon.week4.global.common.Paging;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

  private final ScheduleRepository repository;

  public List<GetFilteredScheduleResponse> getFilteredSchedule(
      String updatedAt, String writerName, Paging pagingReq
  ) {
    Timestamp ts = null;
    if (updatedAt != null) {
      ts = formatTimeStamp(updatedAt);
    }

    List<ScheduleWithUsername> filteredSchedule =
        repository.findByOptions(ts, writerName, pagingReq);

    return filteredSchedule
        .stream()
        .map(ScheduleMapper::toGetFilteredResponse)
        .toList();
  }

  public GetScheduleResponse getScheduleById(Long scheduleId) {
    return ScheduleMapper.toGetResponseDto(repository.findById(scheduleId));
  }

  public AddScheduleResponse saveScheduleAndGetResult(AddScheduleRequest req) {
    Long savedId = repository.save(req.getUserId(), req.getTodo(), req.getPassword());

    Schedule findSchedule = repository.findById(savedId);
    return ScheduleMapper.toAddResponseDto(findSchedule);
  }

  @Transactional
  public UpdateScheduleResponse updateWithAuthorization(
      Long scheduleId,
      UpdateScheduleRequest req
  ) {
    if (isNotValidPassword(scheduleId, req.getPassword())) {
      log.info("[updateWithAuthorization] password not match of schedule={}", scheduleId);
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    UpdateScheduleResponseBuilder dtoBuilder = UpdateScheduleResponse.builder()
        .schedule_id(scheduleId);

    if (hasField(req.getUsername())) {
      log.info("[updateWithAutorization] Request body contains username field.");

      Long userId = repository.findById(scheduleId).getUserId();
      User updatedUser = repository.updateWriterName(userId, req.getUsername());
      dtoBuilder.username(updatedUser.getName());
    }

    if (hasField(req.getTodo())) {
      log.info("[updateWithAutorization] Request body contains todo field.");

      Schedule updatedSchedule = repository.updateTodoContent(scheduleId, req.getTodo());
      dtoBuilder.todo(updatedSchedule.getTodo());
    }

    return dtoBuilder.build();
  }

  @Transactional
  public void deleteWithAuthorization(Long scheduleId, DeleteScheduleRequest req) {
    if (isNotValidPassword(scheduleId, req.getPassword())) {
      log.info("[deleteWithAuthorization] password not match of schedule={}", scheduleId);
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    repository.delete(scheduleId);
  }

  private boolean hasField(String field) {
    return field != null && !field.isEmpty();
  }

  private boolean isNotValidPassword(Long scheduleId, String password) {
    return !repository.checkPassword(scheduleId, password);
  }

  /*
   * updatedAt의 형식은 yyyy-dd-mm으로 controller에서 검증함
   * Timestamp에 맞게 hh:mm:ss를 추가해 줌
   */
  private Timestamp formatTimeStamp(String updatedAt) {
    String hhmmss = "00:00:00";
    return Timestamp.valueOf(updatedAt + " " + hhmmss);
  }
}
