package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService  {

    User register(UserDTO userDTO);
    boolean existsByUsername(String username);
    Optional<User> findByEmailOrPhone(String emailOrPhone, String emailOrPhone2);
}
