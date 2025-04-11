package fpt.g36.gapms.services;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.entities.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WorkOrderService {
    Page<WorkOrder> getAllWorkOrderTeamLeader(Pageable pageable, String workOrderId);

    Page<WorkOrder> getAllWorkOrders(Pageable pageable);

    WorkOrder getWorkOrderById(Long id);

    WorkOrder getWorkOrderByProductionOrder(ProductionOrder productionOrder);

    Page<WorkOrder> getWorkOrdersByStatus(BaseEnum status, Pageable pageable);

    WorkOrder createWorkOrder(ProductionOrder productionOrder, User createBy,
                              List<Long> selectedDyeMachineIds,
                              List<Long> selectedWindingMachineIds);

    WorkOrder findWorkOrderByProductionOrder(ProductionOrder productionOrder);
}
