package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.WorkOrder;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    @Query("SELECT wo FROM WorkOrder wo " +
            "WHERE (:workOrderId IS NULL OR wo.id = :workOrderId) " +
            "ORDER BY wo.createAt DESC")
    Page<WorkOrder> getAllWorkOrderTeamLeader(@Param("workOrderId") Long workOrderId, Pageable pageable);


    WorkOrder findByProductionOrder(ProductionOrder productionOrder);

    Page<WorkOrder> findByStatus(@NotNull BaseEnum status, Pageable pageable);

    Page<WorkOrder> findAllByOrderByCreateAt(Pageable pageable);

    Page<WorkOrder> findAllByStatus(@NotNull BaseEnum status, Pageable pageable);

    WorkOrder findByIdAndStatus(Long id, @NotNull BaseEnum status);

    Page<WorkOrder> findAllBySendStatus(@NotNull SendEnum sendStatus, Pageable pageable);

    WorkOrder findByIdAndSendStatus(Long id, @NotNull SendEnum sendStatus);

    Page<WorkOrder> findByStatusAndSendStatus(@NotNull BaseEnum status, @NotNull SendEnum sendStatus, Pageable pageable);
}
