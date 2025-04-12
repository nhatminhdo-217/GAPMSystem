package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.DyeMachine;
import fpt.g36.gapms.models.entities.WindingMachine;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WindingMachineRepository extends JpaRepository<WindingMachine, Long> {
    Page<WindingMachine> findAllByOrderByCreateAtDesc(Pageable pageable);

    @Query("SELECT wm FROM WindingMachine wm WHERE wm.isActive = true")
    List<WindingMachine> findWindingMachineByActiveIsTrue();

    @Query("SELECT wm FROM WindingMachine wm WHERE wm.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    WindingMachine findByIdWithLock(@Param("id") Long id);
}
