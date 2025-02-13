package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
}
