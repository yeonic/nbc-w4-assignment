package me.yeon.week4.domain.user.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.yeon.week4.domain.user.dao.UserRepository;
import me.yeon.week4.domain.user.domain.User;
import me.yeon.week4.domain.user.domain.UserMapper;
import me.yeon.week4.domain.user.dto.AddUserRequest;
import me.yeon.week4.domain.user.dto.AddUserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserRepository repository;

  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public AddUserResponse addUser(@RequestBody @Valid AddUserRequest req) {
    User userAdded = new User(req.getName(), req.getEmail());
    Long id = repository.save(userAdded);
    userAdded.setGeneratedId(id);

    return UserMapper.toAddResponse(userAdded);
  }
}
