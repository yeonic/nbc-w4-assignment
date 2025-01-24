package me.yeon.week4.domain.user.dao;

import static me.yeon.week4.global.util.ConnectionUtil.close;
import static me.yeon.week4.global.util.ConnectionUtil.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.week4.domain.user.domain.User;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final DataSource dataSource;

  public Long save(User author) throws SQLException {
    String sql = "insert into user(name, email) values (?, ?)";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      con = getConnection(dataSource);
      pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

      pstmt.setString(1, author.getName());
      pstmt.setString(2, author.getEmail());
      pstmt.executeUpdate();

      rs = pstmt.getGeneratedKeys();

      if (rs.next()) {
        return rs.getLong("user_id");
      }
      throw new IllegalStateException("객체가 올바르게 생성되지 않았습니다.");
    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    } finally {
      close(con, pstmt, rs);
    }
  }


}
