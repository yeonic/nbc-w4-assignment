package me.yeon.week4.domain.schedule.application;

import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

  private final ScheduleRepository repository;

  public List<GetFilteredScheduleResponse> getFilteredSchedule(
      String updatedAt,
      String writerName
  ) {
    Timestamp ts = null;

    /*
      updatedAt의 형식은 yyyy-dd-mm으로 controller에서 검증함
      Timestamp에 맞게 hh:mm:ss를 추가해 줌
     */
    if (updatedAt != null) {
      String hhmmss = "00:00:00";
      ts = Timestamp.valueOf(updatedAt + " " + hhmmss);
    }

    List<ScheduleWithUsername> filteredSchedule = repository.findByOptions(ts, writerName);
    return filteredSchedule
        .stream()
        .map(ScheduleMapper::toGetFilteredResponse)
        .toList();
  }

  public GetScheduleResponse getScheduleById(long scheduleId) {
    return ScheduleMapper.toGetResponseDto(repository.findById(scheduleId));
  }

  public AddScheduleResponse saveScheduleAndGetResult(AddScheduleRequest req) {
    Long savedId = repository.save(req.getUserId(), req.getTodo(), req.getPassword());

    Schedule findSchedule = repository.findById(savedId);
    return ScheduleMapper.toAddResponseDto(findSchedule);
  }

  @Transactional
  public UpdateScheduleResponse updateWithAuthorization(
      long scheduleId,
      UpdateScheduleRequest req
  ) {
    if (isNotValidPassword(scheduleId, req.getPassword())) {
      // TODO: 메시지 공통 처리
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

  @Transactional
  public void deleteWithAuthorization(long scheduleId, DeleteScheduleRequest req) {
    if (isNotValidPassword(scheduleId, req.getPassword())) {
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
}
