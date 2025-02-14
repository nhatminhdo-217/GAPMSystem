package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userServiceImpl;

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
            return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                    keyword, keyword, keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> searchAccountsWithoutPaging(String keyword) {
        // Lấy thông tin người dùng đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName(); // Email của người dùng đang đăng nhập

        // Tìm kiếm tài khoản mà không hiển thị tài khoản có email trùng với người dùng hiện tại
        return userRepository.findByKeywordExcludingCurrentUserEmail(keyword, currentEmail);
    }

    @Override
    public List<User> getAllAccountExcept() {
        String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findAllExceptCurrentUser(currentAdminEmail);
    }

}
