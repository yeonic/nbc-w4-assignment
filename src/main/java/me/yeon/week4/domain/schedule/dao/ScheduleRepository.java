package me.yeon.week4.domain.schedule.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import me.yeon.week4.domain.schedule.domain.Schedule;
import me.yeon.week4.domain.schedule.domain.ScheduleWithUsername;
import me.yeon.week4.domain.user.domain.User;
import me.yeon.week4.global.common.Paging;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ScheduleRepository {

  //  private final DataSource dataSource;
  private final JdbcTemplate template;
  private final PasswordEncoder passwordEncoder;

  public ScheduleRepository(DataSource dataSource, PasswordEncoder passwordEncoder) {
    this.template = new JdbcTemplate(dataSource);
    this.passwordEncoder = passwordEncoder;
  }

  public Long save(Long user_id, String todo, String password) {
    String sql = "insert into schedule(user_id, todo, password) values (?, ?, ?)";

    KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    template.update((con) -> {
      PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setLong(1, user_id);
      pstmt.setString(2, todo);
      pstmt.setString(3, passwordEncoder.encode(password));
      return pstmt;
    }, generatedKeyHolder);

    return Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
  }

  public Schedule findById(Long scheduleId) {
    String sql = "select * from schedule where schedule_id = ?";

    return template.queryForObject(sql,
        (rs, rowNum) -> new Schedule(
            rs.getLong("schedule_id"),
            rs.getLong("user_id"),
            rs.getString("todo"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        ),
        scheduleId);
  }

  public List<ScheduleWithUsername> findByOptions(
      Timestamp updatedAt, String writerName, Paging pagingReq
  ) {
    List<Object> params = new ArrayList<>();
    StringBuilder sb = new StringBuilder(
        "select s.*, u.name from schedule as s inner join user as u on s.user_id = u.user_id where 1=1");

    if (writerName != null) {
      sb.append(" and u.name = ?");
      params.add(writerName);
    }

    if (updatedAt != null) {
      sb.append(" and DATE(s.updated_at) = ?");
      params.add(updatedAt);
    }

    sb.append(" order by s.updated_at desc");

    sb.append(" limit ? offset ?");
    params.add(pagingReq.getPageSize());
    params.add(pagingReq.getOffset());

    String sql = sb.toString();
    return template.query(sql, (rs, rowNum) -> new ScheduleWithUsername(
        rs.getLong("schedule_id"),
        rs.getLong("user_id"),
        rs.getString("name"),
        rs.getString("todo"),
        rs.getTimestamp("created_at").toLocalDateTime(),
        rs.getTimestamp("updated_at").toLocalDateTime()
    ), params.toArray(new Object[params.size()]));
  }

  @Transactional
  public Schedule updateTodoContent(Long scheduleId, String todo) {
    String updateQuery = "update schedule set todo = ? where schedule_id = ?";
    String getQuery = "select * from schedule where schedule_id = ?";

    template.update(updateQuery, todo, scheduleId);
    return template.queryForObject(getQuery,
        (rs, rowNum) -> new Schedule(
            rs.getLong("schedule_id"),
            rs.getLong("user_id"),
            rs.getString("todo"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        ), scheduleId);
  }

  @Transactional
  public User updateWriterName(Long writerId, String writerName) {
    String updateQuery = "update user set name = ? where user_id = ?";
    String getQuery = "select * from user where user_id = ?";

    template.update(updateQuery, writerName, writerId);
    return template.queryForObject(getQuery,
        (rs, rowNum) -> new User(
            rs.getLong("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        ), writerId);
  }

  public void delete(Long schedule_id) {
    if (findById(schedule_id) == null) {
      throw new IllegalStateException("삭제할 수 없습니다");
    }
    String sql = "delete from schedule where schedule_id = ?";
    template.update(sql, schedule_id);
  }

  public boolean checkPassword(Long scheduleId, String password) {
    String sql = "select password from schedule where schedule_id = ?";

    String hashedPassword = template.queryForObject(sql,
        (rs, rowNum) -> rs.getString(1)
        , scheduleId);

    return passwordEncoder.matches(password, hashedPassword);
  }

}
