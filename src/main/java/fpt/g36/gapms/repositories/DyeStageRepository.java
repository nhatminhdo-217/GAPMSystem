package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.DyeStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DyeStageRepository extends JpaRepository<DyeStage, Long> {

    @Query("select ds from DyeStage ds where ds.workOrderDetail.workOrder.id = :woId order by ds.id asc")
    List<DyeStage> getAllDyeStageByWorkOrderId(Long woId);
}
