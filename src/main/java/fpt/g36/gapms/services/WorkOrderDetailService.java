package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.WorkOrderDetail;

import java.util.List;

public interface WorkOrderDetailService {

    List<WorkOrderDetail> getAllByWoId(Long woId);
}
