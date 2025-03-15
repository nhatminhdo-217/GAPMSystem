package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContractRepository extends JpaRepository<Contract, String> {

    @Query("SELECT MAX(c.id) FROM Contract c WHERE c.id LIKE 'HD%'")
    String findMaxContractId();
}
