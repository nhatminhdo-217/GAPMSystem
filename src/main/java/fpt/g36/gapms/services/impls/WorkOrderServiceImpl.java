package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.WorkOrder;
import fpt.g36.gapms.repositories.WorkOrderRepository;
import fpt.g36.gapms.services.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {
    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Override
    public Page<WorkOrder> getAllWorkOrderTeamLeader(Pageable pageable) {
        Page<WorkOrder> workOrders = workOrderRepository.getAllWorkOrderTeamLeader(pageable);
        return workOrders;
    }

    @Override
    public Page<WorkOrder> getAllWorkOrders(Pageable pageable) {
        return workOrderRepository.findAllByOrderByCreateAt(pageable);
    }

    @Transactional
    @Override
    public WorkOrder getWorkOrderById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Work Order với ID: " + id));
    }

    @Override
    public WorkOrder getWorkOrderByProductionOrder(ProductionOrder productionOrder) {
        return workOrderRepository.findByProductionOrder(productionOrder);
    }

    @Override
    public Page<WorkOrder> getWorkOrdersByStatus(BaseEnum status, Pageable pageable) {
        return workOrderRepository.findByStatus(status, pageable);
    }

}
