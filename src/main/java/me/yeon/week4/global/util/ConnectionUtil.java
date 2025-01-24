package me.yeon.week4.global.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.jdbc.support.JdbcUtils;

public class ConnectionUtil {

  public static Connection getConnection(DataSource dataSource) throws SQLException {
    return dataSource.getConnection();
  }

  public static void close(Connection con, Statement stmt, ResultSet rs) {
    JdbcUtils.closeResultSet(rs);
    JdbcUtils.closeStatement(stmt);
    JdbcUtils.closeConnection(con);
  }
}
