package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.WindingRiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WindingRiskAssessmentRepository extends JpaRepository<WindingRiskAssessment, Long> {

    @Query("SELECT w FROM WindingRiskAssessment w WHERE w.windingBatch.id = :WindingBatchId ORDER BY w.createAt DESC")
    List<WindingRiskAssessment> getByWindingBatchId(Long WindingBatchId);
}
