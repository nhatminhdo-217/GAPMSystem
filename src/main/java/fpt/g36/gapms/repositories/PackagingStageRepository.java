package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.DyeStage;
import fpt.g36.gapms.models.entities.PackagingStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackagingStageRepository extends JpaRepository<PackagingStage, Long> {

    @Query("select ps from PackagingStage ps where ps.workOrderDetail.workOrder.id = :woId order by ps.id asc")
    List<PackagingStage> getAllPackagingStageByWorkOrderId(Long woId);
}
