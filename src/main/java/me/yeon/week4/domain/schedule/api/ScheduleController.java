package me.yeon.week4.domain.schedule.api;

import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.yeon.week4.domain.schedule.dao.ScheduleRepository;
import me.yeon.week4.domain.schedule.domain.Schedule;
import me.yeon.week4.domain.schedule.domain.ScheduleMapper;
import me.yeon.week4.domain.schedule.dto.AddScheduleRequest;
import me.yeon.week4.domain.schedule.dto.AddScheduleResponse;
import me.yeon.week4.domain.schedule.dto.GetScheduleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

  private final ScheduleRepository repository;
  private final PasswordEncoder passwordEncoder;

  @GetMapping
  public List<GetScheduleResponse> schedules() throws SQLException {
    return repository.findAll()
        .stream()
        .map(ScheduleMapper::toGetResponseDto)
        .toList();
  }

  @GetMapping("/{scheduleId}")
  public GetScheduleResponse schedule(@PathVariable long scheduleId) throws SQLException {
    return ScheduleMapper.toGetResponseDto(repository.findById(scheduleId));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AddScheduleResponse addSchedule(@RequestBody AddScheduleRequest req) throws SQLException {
    String hashedPassword = passwordEncoder.encode(req.getPassword());
    Long savedId = repository.save(req.getUserId(), req.getTodo(), hashedPassword);

    Schedule findSchedule = repository.findById(savedId);
    return ScheduleMapper.toAddResponseDto(findSchedule);
  }
}
