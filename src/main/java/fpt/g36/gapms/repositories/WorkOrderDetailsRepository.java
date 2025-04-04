package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.WorkOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderDetailsRepository extends JpaRepository<WorkOrderDetail, Long> {

    @Query(value = "SELECT wod FROM WorkOrderDetail wod where wod.workOrder.id = :id")
    List<WorkOrderDetail> getAllByWorkOrderId(Long id);

}
