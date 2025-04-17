package fpt.g36.gapms.services;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.entities.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface WorkOrderService {
    Page<WorkOrder> getAllWorkOrderTeamLeader(Pageable pageable, String workOrderId);

    Page<WorkOrder> getAllWorkOrders(Pageable pageable);

    WorkOrder getWorkOrderById(Long id);

    WorkOrder getSubmittedWorkOrderById(Long id);

    Page<WorkOrder> getAllSubmittedWorkOrders(Pageable pageable);

    WorkOrder getWorkOrderByProductionOrder(ProductionOrder productionOrder);

    Page<WorkOrder> getWorkOrdersByStatus(BaseEnum status, Pageable pageable);

    Page<WorkOrder> getSubmittedWorkOrdersByStatus(BaseEnum status, Pageable pageable);

    WorkOrder submitWorkOrder(Long workOrderId);

    WorkOrder approveWorkOrder(Long workOrderId);

    WorkOrder rejectWorkOrder(Long workOrderId);

    WorkOrder createWorkOrder(ProductionOrder productionOrder, User createBy,
                              List<Long> selectedDyeMachineIds,
                              List<Long> selectedWindingMachineIds,
                              List<BigDecimal> additionalWeight);

    WorkOrder findWorkOrderByProductionOrder(ProductionOrder productionOrder);

    WorkOrder updateWorkOrder(Long workOrderId,
                              List<Long> selectedDyeMachineIds,
                              List<Long> selectedWindingMachineIds,
                              List<BigDecimal> additionalWeight);

    void deleteWorkOrderDetails(Long workOrderId);

}
