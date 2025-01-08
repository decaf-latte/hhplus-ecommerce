package kr.hhplus.be.server.service.user;

import kr.hhplus.be.server.domain.user.entity.User;

import java.util.Optional;

public interface UserService {

  Optional<User> getUserById(Long userId);
}
