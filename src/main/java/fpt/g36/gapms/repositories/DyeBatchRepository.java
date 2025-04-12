package fpt.g36.gapms.repositories;


import fpt.g36.gapms.models.entities.DyeBatch;
import fpt.g36.gapms.models.entities.DyeRiskAssessment;
import fpt.g36.gapms.models.entities.DyeStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DyeBatchRepository extends JpaRepository<DyeBatch, Long> {

    @Query("select db from DyeBatch db where db.dyeStage.id = :dsId order by db.id asc")
    List<DyeBatch> getAllDyeBatchByDyeStageId(Long dsId);

}
