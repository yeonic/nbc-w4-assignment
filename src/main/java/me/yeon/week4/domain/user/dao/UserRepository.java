package me.yeon.week4.domain.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.week4.domain.user.domain.User;
import org.springframework.jdbc.support.JdbcUtils;

@Slf4j
@RequiredArgsConstructor
public class UserRepository {

  private final DataSource dataSource;

  public Long save(User author) throws SQLException {
    String sql = "insert into user(name, email) values (?, ?)";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

      pstmt.setString(1, author.getName());
      pstmt.setString(2, author.getEmail());
      pstmt.executeUpdate();

      rs = pstmt.getGeneratedKeys();

      if (rs.next()) {
        return rs.getLong(1);
      }
      throw new IllegalStateException("객체가 올바르게 생성되지 않았습니다.");
    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    } finally {
      close(con, pstmt, rs);
    }
  }

  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  private void close(Connection con, Statement stmt, ResultSet rs) {
    JdbcUtils.closeResultSet(rs);
    JdbcUtils.closeStatement(stmt);
    JdbcUtils.closeConnection(con);
  }
}
