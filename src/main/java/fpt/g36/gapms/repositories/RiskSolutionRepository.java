package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.RiskSolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskSolutionRepository extends JpaRepository<RiskSolution, Long> {

    @Query("select rs from RiskSolution rs order by CASE WHEN rs.approveStatus = fpt.g36.gapms.enums.BaseEnum.NOT_APPROVED THEN 0 ELSE 1 END ,rs.createAt desc")
    Page<RiskSolution> getAllRiskSolution(Pageable pageable);


    @Query("select rs from RiskSolution rs where rs.approveStatus != fpt.g36.gapms.enums.BaseEnum.NOT_APPROVED order by CASE WHEN rs.approveStatus = fpt.g36.gapms.enums.BaseEnum.WAIT_FOR_APPROVAL THEN 0 ELSE 1 END ,rs.createAt desc")
    Page<RiskSolution> getAllRiskSolutionManager(Pageable pageable);

    @Modifying
    @Query("DELETE FROM RiskSolution rs WHERE rs.packagingRiskAssessment.packagingBatch.id IN :packagingBatchIds")
    void deleteByPackagingBatchIds(@Param("packagingBatchIds") List<Long> packagingBatchIds);

    @Modifying
    @Query("DELETE FROM RiskSolution rs WHERE rs.windingRiskAssessment.windingBatch.id IN :windingBatchIds")
    void deleteByWindingBatchIds(@Param("windingBatchIds") List<Long> windingBatchIds);

    @Modifying
    @Query("DELETE FROM RiskSolution rs WHERE rs.dyeRiskAssessment.dyeBatch.id IN :dyeBatchIds")
    void deleteByDyeBatchIds(@Param("dyeBatchIds") List<Long> dyeBatchIds);
}
