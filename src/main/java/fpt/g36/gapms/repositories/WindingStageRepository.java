package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.WindingStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WindingStageRepository extends JpaRepository<WindingStage, Long> {

    @Query("select ws from WindingStage ws where ws.workOrderDetail.workOrder.id = :woId order by ws.id asc")
    List<WindingStage> getAllWindingStageByWorkOrderId(Long woId);
}
