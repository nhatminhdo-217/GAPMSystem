package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.PackagingStage;
import fpt.g36.gapms.models.entities.WorkOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackagingStageRepository extends JpaRepository<PackagingStage, Long> {

    @Query("select ps from PackagingStage ps where ps.workOrderDetail.workOrder.id = :woId order by ps.id asc")
    List<PackagingStage> getAllPackagingStageByWorkOrderId(Long woId);

    @Modifying
    @Query("DELETE FROM PackagingStage ps WHERE ps.workOrderDetail IN :workOrderDetails")
    void deleteAllByWorkOrderDetailIn(@Param("workOrderDetails") List<WorkOrderDetail> workOrderDetails);

    @Modifying
    @Query(value = "DELETE FROM packaging_stage_team_leaders WHERE packaging_stage_id IN :stageIds", nativeQuery = true)
    void deleteTeamLeadersByPackagingStageIds(@Param("stageIds") List<Long> stageIds);

    // Xóa bản ghi trong bảng packaging_stage_qa
    @Modifying
    @Query(value = "DELETE FROM packaging_stage_qa WHERE packaging_stage_id IN :stageIds", nativeQuery = true)
    void deleteQaByPackagingStageIds(@Param("stageIds") List<Long> stageIds);
}
