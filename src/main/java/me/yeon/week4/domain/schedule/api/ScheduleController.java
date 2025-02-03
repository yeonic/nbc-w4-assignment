package me.yeon.week4.domain.schedule.api;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.yeon.week4.domain.schedule.application.ScheduleService;
import me.yeon.week4.domain.schedule.dto.AddScheduleRequest;
import me.yeon.week4.domain.schedule.dto.AddScheduleResponse;
import me.yeon.week4.domain.schedule.dto.DeleteScheduleRequest;
import me.yeon.week4.domain.schedule.dto.GetFilteredScheduleResponse;
import me.yeon.week4.domain.schedule.dto.GetScheduleResponse;
import me.yeon.week4.domain.schedule.dto.UpdateScheduleRequest;
import me.yeon.week4.domain.schedule.dto.UpdateScheduleResponse;
import me.yeon.week4.global.common.Paging;
import me.yeon.week4.global.common.Response;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

  private final ScheduleService service;

  @GetMapping
  public Response<List<GetFilteredScheduleResponse>> schedulesByOptions(
      @RequestParam(value = "updatedAt", required = false) String updatedAt,
      @RequestParam(value = "writerName", required = false) String writerName,
      @RequestParam(value = "pageSize") int pageSize,
      @RequestParam(value = "pageNum") int pageNum
  ) {

    Paging pagingReq = new Paging(pageSize, pageNum);
    return new Response<>(service.getFilteredSchedule(updatedAt, writerName, pagingReq));
  }

  @GetMapping("/{scheduleId}")
  public Response<GetScheduleResponse> schedule(@PathVariable("scheduleId") Long scheduleId) {
    return new Response<>(service.getScheduleById(scheduleId));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Response<AddScheduleResponse> addSchedule(
      @RequestBody @Valid AddScheduleRequest req
  ) {

    return new Response<>(service.saveScheduleAndGetResult(req));
  }

  @PatchMapping("/{scheduleId}/update")
  public Response<UpdateScheduleResponse> update(
      @PathVariable("scheduleId") Long scheduleId,
      @RequestBody UpdateScheduleRequest req
  ) {
    return new Response<>(service.updateWithAuthorization(scheduleId, req));
  }

  @DeleteMapping("/{scheduleId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @PathVariable Long scheduleId,
      @RequestBody DeleteScheduleRequest req
  ) {
    service.deleteWithAuthorization(scheduleId, req);
  }


}
