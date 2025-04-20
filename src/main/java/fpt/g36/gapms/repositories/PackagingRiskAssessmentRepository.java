package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.PackagingBatch;
import fpt.g36.gapms.models.entities.PackagingRiskAssessment;
import fpt.g36.gapms.models.entities.WindingRiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackagingRiskAssessmentRepository extends JpaRepository<PackagingRiskAssessment, Long> {
    @Query("SELECT p FROM PackagingRiskAssessment p WHERE p.packagingBatch.windingBatch.dyeBatch.id = :packagingBatchId ORDER BY p.createAt DESC")
    List<PackagingRiskAssessment> getByPackagingBatchId(Long packagingBatchId);

    @Modifying
    @Query("DELETE FROM PackagingRiskAssessment pra WHERE pra.packagingBatch IN :packagingBatches")
    void deleteAllByPackagingBatchIn(@Param("packagingBatches") List<PackagingBatch> packagingBatches);

    @Modifying
    @Query(value = "DELETE FROM packaging_risk_assessment WHERE packaging_batch_id IN :batchIds", nativeQuery = true)
    void deleteAllByPackagingBatchIds(@Param("batchIds") List<Long> batchIds);
}
