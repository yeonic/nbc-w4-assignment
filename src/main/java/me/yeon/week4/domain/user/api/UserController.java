package me.yeon.week4.domain.user.api;

import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import me.yeon.week4.domain.user.dao.UserRepository;
import me.yeon.week4.domain.user.domain.User;
import me.yeon.week4.domain.user.dto.UserAddRequest;
import me.yeon.week4.domain.user.dto.UserAddResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

  private final UserRepository repository;

  public UserController(DataSource dataSource) {
    repository = new UserRepository(dataSource);
  }

  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public UserAddResponse addUser(@RequestBody UserAddRequest req) throws SQLException {
    User userAdded = new User(req.getName(), req.getEmail());
    Long id = repository.save(userAdded);
    return new UserAddResponse(id, userAdded.getEmail(), userAdded.getName());
  }
}
