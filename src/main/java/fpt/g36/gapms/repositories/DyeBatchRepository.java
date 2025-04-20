package fpt.g36.gapms.repositories;


import fpt.g36.gapms.models.entities.DyeBatch;
import fpt.g36.gapms.models.entities.DyeRiskAssessment;
import fpt.g36.gapms.models.entities.DyeStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DyeBatchRepository extends JpaRepository<DyeBatch, Long> {

    @Query("select db from DyeBatch db where db.dyeStage.id = :dsId order by db.id asc")
    List<DyeBatch> getAllDyeBatchByDyeStageId(Long dsId);

    @Query(value = "SELECT id FROM dye_batch WHERE dye_stage_id IN :stageIds", nativeQuery = true)
    List<Long> findIdsByStageIds(@Param("stageIds") List<Long> stageIds);

    // Thêm phương thức xóa dựa trên danh sách dye_stage_id
    @Modifying
    @Query(value = "DELETE FROM dye_batch WHERE dye_stage_id IN :stageIds", nativeQuery = true)
    void deleteAllByDyeStageIds(@Param("stageIds") List<Long> stageIds);

    List<DyeBatch> findAllByDyeStageIdIn(List<Long> dyeStageIds);

}
