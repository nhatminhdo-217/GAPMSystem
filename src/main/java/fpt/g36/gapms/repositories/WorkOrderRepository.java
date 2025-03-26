package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    @Query("select wo from WorkOrder wo order by wo.createAt desc")
    Page<WorkOrder> getAllWorkOrderTeamLeader(Pageable pageable);
}
