package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.CreateAccountDTO;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.repositories.*;
import fpt.g36.gapms.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userServiceImpl;
   @Autowired
   private CompanyUserRepository companyUserRepository;
    @Autowired
    private CompanyRepository companyRepository;
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
        Optional<User> user = userRepository.findByEmailOrPhoneNumber(currentEmail, currentEmail);

        // Tìm kiếm tài khoản mà không hiển thị tài khoản có email trùng với người dùng
        // hiện tại

        return userRepository.findByKeywordExcludingCurrentUserEmail(keyword, user.get().getEmail(), user.get().getPhoneNumber());
    }

    @Override
    public List<User> getAllAccountExcept() {
        String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmailOrPhoneNumber(currentAdminEmail, currentAdminEmail);
        return userRepository.findAllByEmailNotAndPhoneNumberNot(user.get().getEmail(), user.get().getPhoneNumber());
        /*return userRepository.findAllExceptCurrentUser(currentAdminEmail);*/
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
        user.setAvatar("default-avatar.png");
        user.setPassword(encodedPassword);
        user.setVerified(true);
        user.setActive(true);

        Role role = roleRepository.findById( createAccountDTO.getRole().getId())
                .orElseThrow(() -> new RuntimeException("Vai trò không hợp lệ"));
        user.setRole(role);
        user = userRepository.save(user);
        if(!Objects.equals(user.getRole().getName(), "CUSTOMER")) {
            Company company = companyRepository.getCompanyByEmail("dntndungdong@gmail.com");
            // ✅ Tạo đối tượng CompanyUserId
            CompanyUserId companyUserId = new CompanyUserId();
            companyUserId.setCompanyId(company.getId());  // Đảm bảo company không null
            companyUserId.setUserId(user.getId());        // Lưu user trước để có ID


            CompanyUser companyUser = new CompanyUser();
            companyUser.setId(companyUserId);  // Gán khóa chính
            companyUser.setUser(user);
            companyUser.setCompany(company);

            companyUserRepository.save(companyUser);
        }
        return user;
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
