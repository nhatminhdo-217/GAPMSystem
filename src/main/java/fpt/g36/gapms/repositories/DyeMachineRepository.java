package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.DyeMachine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DyeMachineRepository extends JpaRepository<DyeMachine, Long> {
    Page<DyeMachine> findAllByOrderByCreateAtDesc(Pageable pageable);

    Page<DyeMachine> findByDyeStageIsNull(Pageable pageable);
}
