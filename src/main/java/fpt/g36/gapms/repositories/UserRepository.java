package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
            String username, String email, String phoneNumber, Pageable pageable);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.email <> :email")
    List<User> findAllExceptCurrentUser(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE (u.username LIKE %:keyword% " +
            "OR u.email LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%) " +
            "AND u.email != :currentEmail")
    List<User> findByKeywordExcludingCurrentUserEmail
            (@Param("keyword") String keyword, @Param("currentEmail") String currentEmail);

    @Query("SELECT u from Rfq r JOIN r.createBy u WHERE r.id = :rfqId")
    Optional<User> findUsersByRfqId(long rfqId);
}
