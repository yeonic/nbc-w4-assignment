package me.yeon.week4.domain.user.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import javax.sql.DataSource;
import me.yeon.week4.domain.user.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

  private final JdbcTemplate template;

  public UserRepository(DataSource dataSource) {
    this.template = new JdbcTemplate(dataSource);
  }

  public Long save(User author) {
    String sql = "insert into user(name, email) values (?, ?)";

    KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    template.update((con) -> {
      PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, author.getName());
      pstmt.setString(2, author.getEmail());
      return pstmt;
    }, generatedKeyHolder);

    return Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
  }


}
