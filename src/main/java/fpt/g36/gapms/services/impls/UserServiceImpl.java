package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.UpdateProfileDTO;
import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.MailService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private MailServiceImpl mailService;
   private UserUtils userUtils;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, UserUtils userUtils) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userUtils = userUtils;
    }

    @Override
    public User register(UserDTO userDTO) {
        // Set role to user
        Role userRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));

        User user = new User();
        user.setUsername(userUtils.cleanSpaces(userDTO.getUsername()));
        user.setPassword(userUtils.cleanSpaces(passwordEncoder.encode(userDTO.getPassword())));
        user.setEmail(userUtils.cleanSpaces(userDTO.getEmail()));
        user.setPhoneNumber(userUtils.cleanSpaces(userDTO.getPhoneNumber()));
        user.setRole(userRole);
        if (activeProfile.equals("prod")) {
            user.setAvatar("https://gapmsstorage.blob.core.windows.net/gapms-upload-container/profile-default-icon-1024x1023-4u5mrj2v.png");
        } else {
            user.setAvatar("default-avatar.png");
        }
        user.setVerified(false);
        user.setActive(true);

        return userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findByEmailOrPhone(String emailOrPhone, String emailOrPhone2) {
        Optional<User> user = userRepository.findByEmailOrPhoneNumber(emailOrPhone, emailOrPhone2);
        return user;
    }

    @Override
    public boolean checkPassword(String rawPassword, String encryptedPassword) {
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public String updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.isActive());
        userRepository.save(user);
        return "User updated successfully";
    }

    public String updatePersonalUser(Long userId, UpdateProfileDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Gán trực tiếp giá trị từ DTO vào User
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAvatar(userDTO.getAvatar());

        userRepository.save(user);
        return "User updated successfully";
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, boolean active) {
        User user = getUserById(id);
        user.setActive(active);
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

//    @Override
//    public String updateUser(Long userId, UserDTO userDTO) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//
//        user.setRole(userDTO.getRole());
//        user.setActive(userDTO.isActive());
//        user.setUpdatedAt(LocalDateTime.now());
//        userRepository.save(user);
//        return "User updated successfully";
//    }

    @Override
    public Page<User> getAccounts(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> findUsersByRfqId(long rfqId) {
        return userRepository.findUsersByRfqId(rfqId);
    }
}
