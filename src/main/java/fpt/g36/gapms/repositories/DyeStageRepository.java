package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.DyeStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DyeStageRepository extends JpaRepository<DyeStage, Long> {

    @Query("select ds from DyeStage ds where ds.workOrderDetail.workOrder.id = :woId order by ds.id asc")
    List<DyeStage> getAllDyeStageByWorkOrderId(Long woId);

    @Query("SELECT ds FROM DyeStage ds " +
            "WHERE ds.dyeMachine.id = :machineId " +
            "AND ds.workStatus != :finishedStatus " +
            "AND ((ds.plannedStart <= :deadline AND ds.deadline >= :plannedStart) " +
            "OR (ds.startAt IS NOT NULL AND ds.completeAt IS NULL))")
    List<DyeStage> findActiveDyeStagesByMachine(
            @Param("machineId") Long machineId,
            @Param("plannedStart") LocalDateTime plannedStart,
            @Param("deadline") LocalDateTime deadline,
            @Param("finishedStatus") WorkEnum finishedStatus);
}
