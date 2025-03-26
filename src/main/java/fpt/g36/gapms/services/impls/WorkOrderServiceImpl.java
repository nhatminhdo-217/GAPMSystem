package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.WorkOrder;
import fpt.g36.gapms.repositories.WorkOrderRepository;
import fpt.g36.gapms.services.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {
    @Autowired
    private WorkOrderRepository workOrderRepository;
    @Override
    public Page<WorkOrder> getAllWorkOrderTeamLeader(Pageable pageable) {
        Page<WorkOrder> workOrders = workOrderRepository.getAllWorkOrderTeamLeader(pageable);
        return workOrders;
    }
}
