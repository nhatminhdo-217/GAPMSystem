package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.WindingBatch;
import fpt.g36.gapms.models.entities.WindingRiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WindingRiskAssessmentRepository extends JpaRepository<WindingRiskAssessment, Long> {

    @Query("SELECT w FROM WindingRiskAssessment w WHERE w.windingBatch.dyeBatch.id = :WindingBatchId ORDER BY w.createAt DESC")
    List<WindingRiskAssessment> getByWindingBatchId(Long WindingBatchId);

    @Modifying
    @Query("DELETE FROM WindingRiskAssessment wra WHERE wra.windingBatch IN :windingBatches")
    void deleteAllByWindingBatchIn(@Param("windingBatches") List<WindingBatch> windingBatches);

    @Modifying
    @Query(value = "DELETE FROM winding_risk_assessment WHERE winding_batch_id IN :batchIds", nativeQuery = true)
    void deleteAllByWindingBatchIds(@Param("batchIds") List<Long> batchIds);
}
