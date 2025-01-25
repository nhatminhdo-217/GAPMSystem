package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> getRole(String roleName);
}
