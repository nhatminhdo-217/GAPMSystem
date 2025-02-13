package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public User createAccount(User user, String password) {
        // Mã hoá mật khẩu
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        // Thực hiện lưu đối tượng user vào cơ sở dữ liệu
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
