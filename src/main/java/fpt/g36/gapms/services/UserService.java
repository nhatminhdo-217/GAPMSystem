package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User register(UserDTO userDTO);
    boolean existsByUsername(String username);
}
