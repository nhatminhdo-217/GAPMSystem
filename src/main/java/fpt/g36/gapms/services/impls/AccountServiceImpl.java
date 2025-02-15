package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.CreateAccountDTO;
import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Page<User> getAccounts(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserById(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        System.out.println(user);
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User createAccount(CreateAccountDTO createAccountDTO, String password) {
        if (existsByEmail(createAccountDTO.getEmail())) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        if (existsByPhoneNumber(createAccountDTO.getPhoneNumber())) {
            throw new RuntimeException("Số điện thoại đã tồn tại!");
        }

        User user = new User();
        String encodedPassword = passwordEncoder.encode(password);

        user.setUsername(createAccountDTO.getUsername());
        user.setEmail(createAccountDTO.getEmail());
        user.setPhoneNumber(createAccountDTO.getPhoneNumber());
        user.setPassword(encodedPassword);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Role role = roleRepository.findById((long) createAccountDTO.getRole())
                .orElseThrow(() -> new RuntimeException("Vai trò không hợp lệ"));
        user.setRole(role);

       return userRepository.save(user);
    }


    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
}
