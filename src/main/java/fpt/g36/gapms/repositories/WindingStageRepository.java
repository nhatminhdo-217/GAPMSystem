package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.WindingStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WindingStageRepository extends JpaRepository<WindingStage, Long> {

    @Query("select ws from WindingStage ws where ws.workOrderDetail.workOrder.id = :woId order by ws.id asc")
    List<WindingStage> getAllWindingStageByWorkOrderId(Long woId);

    @Query("SELECT ws FROM WindingStage ws " +
            "WHERE ws.windingMachine.id = :machineId " +
            "AND ws.workStatus != :finishedStatus " +
            "AND ((ws.plannedStart <= :deadline AND ws.deadline >= :plannedStart) " +
            "OR (ws.startAt IS NOT NULL AND ws.completeAt IS NULL))")
    List<WindingStage> findActiveWindingStagesByMachine(
            @Param("machineId") Long machineId,
            @Param("plannedStart") LocalDateTime plannedStart,
            @Param("deadline") LocalDateTime deadline,
            @Param("finishedStatus") WorkEnum finishedStatus);
}
