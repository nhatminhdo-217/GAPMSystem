package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findById(int Id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Optional<User> findByEmailOrPhoneNumber(String email, String phone);



}
