package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.DyeBatch;
import fpt.g36.gapms.models.entities.DyeRiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DyeRiskAssessmentRepository extends JpaRepository<DyeRiskAssessment, Long> {

    /*@Query("select dra from DyeRiskAssessment dra where dra.dyeStage.id = :dyeId ORDER BY dra.createAt DESC")*/
    @Query("SELECT d FROM DyeRiskAssessment d WHERE d.dyeBatch.id = :dyeBatchId ORDER BY d.createAt DESC")
    List<DyeRiskAssessment> getByDyeBatchId(Long dyeBatchId);

    @Modifying
    @Query("DELETE FROM DyeRiskAssessment dra WHERE dra.dyeBatch IN :dyeBatches")
    void deleteAllByDyeBatchIn(@Param("dyeBatches") List<DyeBatch> dyeBatches);

    @Modifying
    @Query(value = "DELETE FROM dye_risk_assessment WHERE dye_batch_id IN :batchIds", nativeQuery = true)
    void deleteAllByDyeBatchIds(@Param("batchIds") List<Long> batchIds);
}
