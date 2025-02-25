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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Page<User> searchAccounts(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {

            return userRepository
                    .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                            keyword, keyword, keyword, pageable);

        }
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> searchAccountsWithoutPaging(String keyword) {
        // Lấy thông tin người dùng đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName(); // Email của người dùng đang đăng nhập


        // Tìm kiếm tài khoản mà không hiển thị tài khoản có email trùng với người dùng
        // hiện tại

        return userRepository.findByKeywordExcludingCurrentUserEmail(keyword, currentEmail);
    }

    @Override
    public List<User> getAllAccountExcept() {
        String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findAllExceptCurrentUser(currentAdminEmail);
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
        System.err.println("DEBUG_Service: " + user.getUsername());
        user.setEmail(createAccountDTO.getEmail());
        user.setPhoneNumber(createAccountDTO.getPhoneNumber());
        user.setAvatar("default_avatar.png");
        user.setPassword(encodedPassword);
        user.setVerified(true);
        user.setActive(true);

        Role role = roleRepository.findById( createAccountDTO.getRole().getId())
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
