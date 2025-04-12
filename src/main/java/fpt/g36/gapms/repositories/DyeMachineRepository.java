package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.DyeMachine;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface DyeMachineRepository extends JpaRepository<DyeMachine, Long> {
    Page<DyeMachine> findAllByOrderByCreateAtDesc(Pageable pageable);

    @Query("SELECT dm FROM DyeMachine dm WHERE dm.isActive = true")
    List<DyeMachine> findDyeMachineByActiveIsTrue();

    @Query("SELECT dm FROM DyeMachine dm WHERE dm.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    DyeMachine findByIdWithLock(@Param("id") Long id);

    Page<DyeMachine> findByDyeStageIsNull(Pageable pageable);

    @Query("SELECT dm FROM DyeMachine dm " +
            "WHERE dm.id NOT IN (SELECT ds.dyeMachine.id FROM DyeStage ds " +
            "WHERE ds.plannedStart BETWEEN :plannedStart AND :plannedEnd) " +
            "ORDER BY dm.createAt DESC")
    Page<DyeMachine> findAvailableDyeMachines(LocalDate plannedStart, LocalDate plannedEnd, Pageable pageable);


}
