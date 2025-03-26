package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkOrderService {
    Page<WorkOrder> getAllWorkOrderTeamLeader(Pageable pageable);
}
