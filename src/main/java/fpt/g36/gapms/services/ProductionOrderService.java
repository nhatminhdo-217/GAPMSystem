package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.technical.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.technical.ProductionOrderDetailsDTO;
import fpt.g36.gapms.models.entities.ProductionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ProductionOrderService {
    Page<ProductionOrderDTO> getApprovedProductionOrders(Pageable pageable);

    ProductionOrderDetailsDTO getProductionOrderDetails(Long id);

    ProductionOrder getProductionOrderById(Long id);
}
