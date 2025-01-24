package me.yeon.week4.domain.schedule.dao;

import static me.yeon.week4.global.util.ConnectionUtil.close;
import static me.yeon.week4.global.util.ConnectionUtil.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.week4.domain.schedule.domain.Schedule;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ScheduleRepository {

  private final DataSource dataSource;

  public Long save(Long user_id, String todo, String password) throws SQLException {
    String sql = "insert into schedule(user_id, todo, password) values (?, ?, ?)";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      con = getConnection(dataSource);
      pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setLong(1, user_id);
      pstmt.setString(2, todo);
      pstmt.setString(3, password);
      pstmt.executeUpdate();

      rs = pstmt.getGeneratedKeys();
      if (rs.next()) {
        return rs.getLong(1);
      }
      // TODO: 메시지 공통처리
      throw new IllegalStateException("스케줄이 올바르게 생성되지 않았습니다.");
    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    } finally {
      close(con, pstmt, rs);
    }
  }

  public Schedule findById(Long scheduleId) throws SQLException {
    String sql = "select * from schedule where schedule_id = ?";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      con = getConnection(dataSource);
      pstmt = con.prepareStatement(sql);
      pstmt.setLong(1, scheduleId);
      rs = pstmt.executeQuery();
      if (rs.next()) {
        return new Schedule(
            rs.getLong("schedule_id"),
            rs.getLong("user_id"),
            rs.getString("todo"),
            rs.getString("password")
        );
      }
      // TODO 메시지 공통 처리 필요
      throw new NoSuchElementException("해당 아이디의 스케줄이 존재하지 않습니다: id = " + scheduleId);
    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    } finally {
      close(con, pstmt, rs);
    }
  }

  public List<Schedule> findAll() throws SQLException {
    List<Schedule> result = new ArrayList<>();

    String sql = "select * from schedule";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      con = getConnection(dataSource);
      pstmt = con.prepareStatement(sql);
      rs = pstmt.executeQuery();

      while (rs.next()) {
        result.add(new Schedule(
            rs.getLong("schedule_id"),
            rs.getLong("user_id"),
            rs.getString("todo"),
            rs.getString("password")
        ));
      }
      return result;
    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    } finally {
      close(con, pstmt, rs);
    }
  }
}
