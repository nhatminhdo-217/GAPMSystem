package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.WorkOrderDetail;
import fpt.g36.gapms.repositories.WorkOrderDetailsRepository;
import fpt.g36.gapms.services.WorkOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkOrderDetailServiceImpl implements WorkOrderDetailService {
    @Autowired
    private WorkOrderDetailsRepository workOrderDetailsRepository;

    @Override
    public List<WorkOrderDetail> getAllByWoId(Long woId) {
        List<WorkOrderDetail> workOrderDetails = workOrderDetailsRepository.getAllByWorkOrderId(woId);
        return workOrderDetails;
    }
}
