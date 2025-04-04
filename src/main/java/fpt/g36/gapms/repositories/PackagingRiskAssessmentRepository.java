package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.PackagingRiskAssessment;
import fpt.g36.gapms.models.entities.WindingRiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackagingRiskAssessmentRepository extends JpaRepository<PackagingRiskAssessment, Long> {
    @Query("SELECT p FROM PackagingRiskAssessment p WHERE p.packagingBatch.id = :packagingBatchId ORDER BY p.createAt DESC")
    List<PackagingRiskAssessment> getByPackagingBatchId(Long packagingBatchId);
}
