package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.entities.WorkOrder;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    @Query("SELECT wo FROM WorkOrder wo " +
            "WHERE  wo.isProduction != fpt.g36.gapms.enums.WorkEnum.FINISHED and (:workOrderId IS NULL OR wo.id = :workOrderId) " +
            "ORDER BY wo.createAt DESC, CASE WHEN wo.isProduction = fpt.g36.gapms.enums.WorkEnum.NOT_STARTED THEN 0 ELSE 1 END")
    Page<WorkOrder> getAllWorkOrderTeamLeader(@Param("workOrderId") Long workOrderId, Pageable pageable);


    WorkOrder findByProductionOrder(ProductionOrder productionOrder);

    @Query("SELECT w FROM WorkOrder w WHERE w.status = :status ORDER BY w.updateAt DESC")
    Page<WorkOrder> findByStatus(@NotNull BaseEnum status, Pageable pageable);

    Page<WorkOrder> findAllByOrderByCreateAt(Pageable pageable);

    Page<WorkOrder> findAllBySendStatus(@NotNull SendEnum sendStatus, Pageable pageable);

    WorkOrder findByIdAndSendStatus(Long id, @NotNull SendEnum sendStatus);

    Page<WorkOrder> findByStatusAndSendStatus(@NotNull BaseEnum status, @NotNull SendEnum sendStatus, Pageable pageable);

    Optional<WorkOrder> findByIdAndCreatedBy(Long id, User createdBy);

    @Query("SELECT wo FROM WorkOrder wo WHERE wo.createdBy = :createdBy ORDER BY wo.updateAt DESC")
    Page<WorkOrder> findByCreatedBy(@Param("createdBy") User createdBy, Pageable pageable);

    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = :status AND wo.createdBy = :createdBy ORDER BY wo.updateAt DESC")
    Page<WorkOrder> findByStatusAndCreatedBy(@Param("status") BaseEnum status, @Param("createdBy") User createdBy, Pageable pageable);
}
