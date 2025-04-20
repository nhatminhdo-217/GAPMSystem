package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.PackagingBatch;
import fpt.g36.gapms.models.entities.PackagingStage;
import fpt.g36.gapms.models.entities.WindingBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackagingBatchRepository extends JpaRepository<PackagingBatch, Long> {

    @Query("select pb from PackagingBatch pb where pb.packagingStage.id = :psId order by pb.id asc")
    List<PackagingBatch> getAllWindingBatchByPackagingStageId(Long psId);

    // Thêm phương thức xóa dựa trên danh sách packaging_stage_id
    @Modifying
    @Query(value = "DELETE FROM packaging_batch WHERE packaging_stage_id IN :stageIds", nativeQuery = true)
    void deleteAllByPackagingStageIds(@Param("stageIds") List<Long> stageIds);

    @Query(value = "SELECT id FROM packaging_batch WHERE packaging_stage_id IN :stageIds", nativeQuery = true)
    List<Long> findIdsByStageIds(@Param("stageIds") List<Long> stageIds);

    List<PackagingBatch> findAllByPackagingStageIdIn(List<Long> packagingStageIds);

}
