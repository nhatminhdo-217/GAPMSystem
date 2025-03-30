package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.DyeRiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DyeRiskAssessmentRepository extends JpaRepository<DyeRiskAssessment, Long> {

    @Query("select dra from DyeRiskAssessment dra where dra.dyeStage.id = :dyeId ORDER BY dra.createAt DESC")
    List<DyeRiskAssessment> getByDyeId(Long dyeId);
}
