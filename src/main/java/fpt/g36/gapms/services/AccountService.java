package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.User;

import java.util.List;

public interface AccountService {
    List<User> getAccounts();
    User getUserById(long userId);

}
