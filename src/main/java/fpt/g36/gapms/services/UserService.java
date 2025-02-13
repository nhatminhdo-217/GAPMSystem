package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.UpdateProfileDTO;
import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService {

    User register(UserDTO userDTO);

    boolean existsByUsername(String username);

    Optional<User> findByEmailOrPhone(String emailOrPhone, String emailOrPhone2);

    boolean checkPassword(String rawPassword, String encryptedPassword);

    void updatePassword(User user, String newPassword);

    String updateUser(Long userId, UserDTO userDTO);

    String updatePersonalUser(Long userId, UpdateProfileDTO userDTO);
  
    void updateUserStatus(Long id, boolean active);

    User getUserById(Long id);
}
