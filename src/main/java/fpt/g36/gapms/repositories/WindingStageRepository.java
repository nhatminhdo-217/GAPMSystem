package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.WindingStage;
import fpt.g36.gapms.models.entities.WorkOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("DELETE FROM WindingStage ws WHERE ws.workOrderDetail IN :workOrderDetails")
    void deleteAllByWorkOrderDetailIn(@Param("workOrderDetails") List<WorkOrderDetail> workOrderDetails);

    @Modifying
    @Query(value = "DELETE FROM winding_stage_team_leaders WHERE winding_stage_id IN :stageIds", nativeQuery = true)
    void deleteTeamLeadersByWindingStageIds(@Param("stageIds") List<Long> stageIds);

    // Xóa bản ghi trong bảng winding_stage_qa
    @Modifying
    @Query(value = "DELETE FROM winding_stage_qa WHERE winding_stage_id IN :stageIds", nativeQuery = true)
    void deleteQaByWindingStageIds(@Param("stageIds") List<Long> stageIds);
}
