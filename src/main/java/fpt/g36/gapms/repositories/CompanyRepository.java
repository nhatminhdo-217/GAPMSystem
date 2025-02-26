package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.CompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("select c from Company c where c.user.id = :userId")
    Company getCompanyByUserId(Long userId);

}
