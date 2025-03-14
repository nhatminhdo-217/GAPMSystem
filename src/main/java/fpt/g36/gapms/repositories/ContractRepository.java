package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, String> {
}
