package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    Page<User> getAccounts(Pageable pageable);

    User getUserById(long userId);

    Page<User> searchAccounts(String keyword, Pageable pageable);

    List<User> searchAccountsWithoutPaging(String keyword);

    List<User> getAllAccountExcept();
}
