package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.WindingMachine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WindingMachineRepository extends JpaRepository<WindingMachine, Long> {
    Page<WindingMachine> findAllByOrderByCreateAtDesc(Pageable pageable);

    Page<WindingMachine> findByWindingStageIsNull(Pageable pageable);
}
