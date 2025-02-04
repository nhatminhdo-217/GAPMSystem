package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAccounts() {
        List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
