package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.entities.WorkOrder;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    /*@Query("SELECT wo FROM WorkOrder wo " +
            "WHERE  wo.isProduction != fpt.g36.gapms.enums.WorkEnum.FINISHED and (:workOrderId IS NULL OR wo.id = :workOrderId)" +
            "ORDER BY wo.createAt DESC, CASE WHEN wo.isProduction = fpt.g36.gapms.enums.WorkEnum.NOT_STARTED THEN 0 ELSE 1 END")
    Page<WorkOrder> getAllWorkOrderTeamLeader(@Param("workOrderId") Long workOrderId, Pageable pageable);*/

    @Query("SELECT DISTINCT wo FROM WorkOrder wo " + "JOIN wo.workOrderDetails wod " + "JOIN wod.dyeStage ds " + "JOIN ds.dyebatches db " +
            "WHERE wo.isProduction != fpt.g36.gapms.enums.WorkEnum.FINISHED " + "AND (:workOrderId IS NULL OR wo.id = :workOrderId) " +
            "AND db.technologyProcess IS NOT NULL ORDER BY  CASE WHEN wo.isProduction = fpt.g36.gapms.enums.WorkEnum.NOT_STARTED THEN 0 WHEN wo.isProduction = fpt.g36.gapms.enums.WorkEnum.IN_PROGRESS Then 1 ELSE 2 END, wo.createAt DESC")
    Page<WorkOrder> getAllWorkOrderTeamLeader(@Param("workOrderId") Long workOrderId, Pageable pageable);

    @Query("SELECT wo FROM WorkOrder wo " +
            "WHERE  (:workOrderId IS NULL OR wo.id = :workOrderId) " +
            "ORDER BY  CASE WHEN wo.isProduction = fpt.g36.gapms.enums.WorkEnum.NOT_STARTED THEN 0 WHEN wo.isProduction = fpt.g36.gapms.enums.WorkEnum.IN_PROGRESS Then 1  WHEN wo.isProduction = fpt.g36.gapms.enums.WorkEnum.FINISHED Then 2 ELSE 3 END, wo.createAt DESC")
    Page<WorkOrder> getAllWorkOrderPo(@Param("workOrderId") Long workOrderId, Pageable pageable);

    WorkOrder findByProductionOrder(ProductionOrder productionOrder);

    @Query("SELECT w FROM WorkOrder w WHERE w.status = :status ORDER BY w.updateAt DESC")
    Page<WorkOrder> findByStatus(@NotNull BaseEnum status, Pageable pageable);

    Page<WorkOrder> findAllByOrderByCreateAt(Pageable pageable);

    Page<WorkOrder> findAllBySendStatus(@NotNull SendEnum sendStatus, Pageable pageable);

    WorkOrder findByIdAndSendStatus(Long id, @NotNull SendEnum sendStatus);

    Page<WorkOrder> findByStatusAndSendStatus(@NotNull BaseEnum status,
                                              @NotNull SendEnum sendStatus,
                                              Pageable pageable);

    Optional<WorkOrder> findByIdAndCreatedBy(Long id, User createdBy);

    @Query("SELECT wo FROM WorkOrder wo WHERE wo.createdBy = :createdBy ORDER BY wo.updateAt DESC")
    Page<WorkOrder> findByCreatedBy(@Param("createdBy") User createdBy, Pageable pageable);

    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = :status AND wo.createdBy = :createdBy ORDER BY wo.updateAt DESC")
    Page<WorkOrder> findByStatusAndCreatedBy(@Param("status") BaseEnum status, @Param("createdBy") User createdBy, Pageable pageable);

    @Query("SELECT w FROM WorkOrder w WHERE w.status = :status ORDER BY w.updateAt DESC")
    Page<WorkOrder> getAllByStatus(BaseEnum status, Pageable pageable);

    @Query("SELECT wo FROM WorkOrder wo " +
            "LEFT JOIN wo.workOrderDetails wod " +
            "LEFT JOIN wod.dyeStage ds " +
            "LEFT JOIN ds.dyebatches db " +
            "LEFT JOIN db.technologyProcess tp " +
            "WHERE wo.status = :status " +
            "AND tp IS NULL")
    Page<WorkOrder> findApprovedWorkOrdersWithoutTechnologyProcess(@Param("status") BaseEnum status, Pageable pageable);

    @Query("SELECT DISTINCT wo FROM WorkOrder wo " +
            "WHERE NOT EXISTS (" +
            "    SELECT db FROM WorkOrderDetail wod " +
            "    JOIN wod.dyeStage ds " +
            "    JOIN ds.dyebatches db " +
            "    WHERE wod.workOrder = wo " +
            "    AND db.technologyProcess IS NULL" +
            ") " +
            "AND EXISTS (" +
            "    SELECT tp FROM WorkOrderDetail wod2 " +
            "    JOIN wod2.dyeStage ds2 " +
            "    JOIN ds2.dyebatches db2 " +
            "    JOIN db2.technologyProcess tp " +
            "    WHERE wod2.workOrder = wo " +
            "    AND tp.createdBy = :createdBy" +
            ") " +
            "ORDER BY (SELECT MAX(tp2.updateAt) FROM WorkOrderDetail wod3 " +
            "          JOIN wod3.dyeStage ds3 " +
            "          JOIN ds3.dyebatches db3 " +
            "          JOIN db3.technologyProcess tp2 " +
            "          WHERE wod3.workOrder = wo) DESC")
    Page<WorkOrder> findWorkOrdersWithTechnologyProcessByCreatedBy(@Param("createdBy") User createdBy, Pageable pageable);

    Optional<WorkOrder> findByIdAndStatus(Long id, BaseEnum status);

}
