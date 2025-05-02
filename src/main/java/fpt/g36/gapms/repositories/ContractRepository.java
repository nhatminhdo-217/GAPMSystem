package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {

    @Query("SELECT MAX(c.id) FROM Contract c WHERE c.id LIKE 'HD%'")
    String findMaxContractId();

    @Query("SELECT c FROM Contract c WHERE c.name = :code")
    Optional<Contract> findByContractCode(String code);

    @Query("SELECT c FROM Contract c WHERE c.name = :code AND c.id <> :id")
    Optional<Contract> findByContractCodeAndExcludeId(@Param("code") String code, @Param("id") String id);

}
