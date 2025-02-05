package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    Page<User> getAccounts(Pageable pageable);
    User getUserById(long userId);

}
