package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.WindingBatch;
import fpt.g36.gapms.models.entities.WindingStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WindingBatchRepository extends JpaRepository<WindingBatch, Long> {
    @Query("select wb from WindingBatch wb where wb.windingStage.id = :wsId order by wb.id asc")
    List<WindingBatch> getAllWindingBatchByWindingStageId(Long wsId);
}
