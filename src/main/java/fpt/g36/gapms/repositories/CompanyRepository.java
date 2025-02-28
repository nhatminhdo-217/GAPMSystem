package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.CompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    @Query("SELECT c FROM Company c JOIN c.users u WHERE u.id = :userId")
    Optional<Company> findCompanyByUserId(Long userId);

}
