package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.WindingBatch;
import fpt.g36.gapms.models.entities.WindingStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WindingBatchRepository extends JpaRepository<WindingBatch, Long> {
    @Query("select wb from WindingBatch wb where wb.windingStage.id = :wsId order by wb.id asc")
    List<WindingBatch> getAllWindingBatchByWindingStageId(Long wsId);

    @Query(value = "SELECT id FROM winding_batch WHERE winding_stage_id IN :stageIds", nativeQuery = true)
    List<Long> findIdsByStageIds(@Param("stageIds") List<Long> stageIds);

    // Thêm phương thức xóa dựa trên danh sách winding_stage_id
    @Modifying
    @Query(value = "DELETE FROM winding_batch WHERE winding_stage_id IN :stageIds", nativeQuery = true)
    void deleteAllByWindingStageIds(@Param("stageIds") List<Long> stageIds);

    List<WindingBatch> findAllByWindingStageIdIn(List<Long> windingStageIds);
}
