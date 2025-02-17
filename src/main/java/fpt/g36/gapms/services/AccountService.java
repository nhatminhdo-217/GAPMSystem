package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.CreateAccountDTO;
import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    Page<User> getAccounts(Pageable pageable);

    User getUserById(long userId);

    User createAccount(CreateAccountDTO createAccountDTO, String password);
    Page<User> searchAccounts(String keyword, Pageable pageable);

    List<User> searchAccountsWithoutPaging(String keyword);

    List<User> getAllAccountExcept();

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
