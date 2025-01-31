package me.yeon.week4.domain.schedule.dao;

import static me.yeon.week4.global.util.ConnectionUtil.close;
import static me.yeon.week4.global.util.ConnectionUtil.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.week4.domain.schedule.domain.Schedule;
import me.yeon.week4.domain.schedule.domain.ScheduleWithUsername;
import me.yeon.week4.domain.user.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ScheduleRepository {

  private final DataSource dataSource;
  private final PasswordEncoder passwordEncoder;

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
      pstmt.setString(3, passwordEncoder.encode(password));
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
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
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

  public List<ScheduleWithUsername> findByOptions(Timestamp updatedAt, String writerName)
      throws SQLException {
    List<ScheduleWithUsername> result = new ArrayList<>();
    List<Object> params = new ArrayList<>();

    StringBuilder sb = new StringBuilder();
    if (writerName != null) {
      sb.append(
          "select s.*, u.name from schedule as s inner join user as u on s.user_id = u.user_id where u.name = ?");
      params.add(writerName);
    } else {
      sb.append("select * from schedule as s where 1=1");
    }

    if (updatedAt != null) {
      sb.append(" and DATE(s.updated_at) = ?");
      params.add(updatedAt);
    }

    sb.append(" order by s.updated_at desc");

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      con = getConnection(dataSource);
      pstmt = con.prepareStatement(sb.toString());
      for (int i = 0; i < params.size(); i++) {
        pstmt.setObject(i + 1, params.get(i));
      }
      rs = pstmt.executeQuery();

      while (rs.next()) {
        result.add(new ScheduleWithUsername(
            rs.getLong("schedule_id"),
            rs.getLong("user_id"),
            writerName != null ? rs.getString("name") : null,
            rs.getString("todo"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
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

  public Schedule updateTodoContent(Long scheduleId, String todo) throws SQLException {
    String updateQuery = "update schedule set todo = ? where schedule_id = ?";
    String getQuery = "select * from schedule where schedule_id = ?";

    Connection con = null;
    PreparedStatement pstmt1 = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    try {
      con = getConnection(dataSource);
      pstmt1 = con.prepareStatement(updateQuery);
      pstmt1.setString(1, todo);
      pstmt1.setLong(2, scheduleId);
      pstmt1.executeUpdate();

      pstmt2 = con.prepareStatement(getQuery);
      pstmt2.setLong(1, scheduleId);
      rs = pstmt2.executeQuery();

      if (rs.next()) {
        return new Schedule(
            rs.getLong("schedule_id"),
            rs.getLong("user_id"),
            rs.getString("todo"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        );
      }
      // TODO 메시지 공통 처리 필요
      throw new IllegalStateException("스케줄을 업데이트 하는데 문제가 생겼습니다.");
    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    } finally {
      close(con, rs, pstmt1, pstmt2);
    }
  }

  public User updateWriterName(Long writerId, String writerName) throws SQLException {
    String updateQuery = "update user set name = ? where user_id = ?";
    String getQuery = "select * from user where user_id = ?";

    Connection con = null;
    PreparedStatement pstmt1 = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    try {
      con = getConnection(dataSource);
      pstmt1 = con.prepareStatement(updateQuery);
      pstmt1.setString(1, writerName);
      pstmt1.setLong(2, writerId);
      pstmt1.executeUpdate();

      pstmt2 = con.prepareStatement(getQuery);
      pstmt2.setLong(1, writerId);
      rs = pstmt2.executeQuery();

      if (rs.next()) {
        return new User(
            rs.getLong("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        );
      }
      // TODO 메시지 공통 처리 필요
      throw new IllegalStateException("작성자를 업데이트 하는데 문제가 생겼습니다.");
    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    } finally {
      close(con, rs, pstmt1, pstmt2);
    }
  }

  public boolean checkPassword(Long scheduleId, String password) throws SQLException {
    String sql = "select password from schedule where schedule_id = ?";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      con = getConnection(dataSource);
      pstmt = con.prepareStatement(sql);
      pstmt.setLong(1, scheduleId);

      rs = pstmt.executeQuery();
      if (rs.next()) {
        String hashedPassword = rs.getString(1);
        return passwordEncoder.matches(password, hashedPassword);
      }
      throw new IllegalStateException("잘못된 접근입니다.");
    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    } finally {
      close(con, pstmt, rs);
    }
  }

  public void delete(Long schedule_id) throws SQLException {
    String deleteQuery = "delete from schedule where schedule_id = ?";
    String getQuery = "select * from schedule where schedule_id = ?";

    Connection con = null;
    PreparedStatement pstmt1 = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    try {
      con = getConnection(dataSource);
      pstmt1 = con.prepareStatement(deleteQuery);
      pstmt1.setLong(1, schedule_id);
      pstmt1.executeUpdate();

      pstmt2 = con.prepareStatement(getQuery);
      pstmt2.setLong(1, schedule_id);
      rs = pstmt2.executeQuery();

      if (rs.next()) {
        // Todo 에러 메시지 공통 처리
        throw new IllegalStateException("스케줄 삭제가 정상적으로 이루어지지 않았습니다.");
      }

    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    } finally {
      close(con, rs, pstmt1, pstmt2);
    }
  }

}
