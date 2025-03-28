package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.technical.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.technical.ProductionOrderDetailsDTO;
import fpt.g36.gapms.models.entities.ProductionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import fpt.g36.gapms.models.entities.User;


import java.util.List;

@Service
public interface ProductionOrderService {
    Page<ProductionOrderDTO> getApprovedProductionOrders(Pageable pageable);

    ProductionOrderDetailsDTO getProductionOrderDetails(Long id);

    ProductionOrder getProductionOrderById(Long id);

    Page<ProductionOrderDTO> findPaginatedByRoles(Integer page, Integer pageSize , String sortField, String sortDir, User currUser);

    ProductionOrderDTO findById(Long id);

    List<ProductionOrderDetailDTO> findDetailByProductionOrderId(Long id);

    ProductionOrderDetailDTO findDetailById(Long id);

    ProductionOrderDetailDTO updateProductionOrderDetail(ProductionOrderDetailDTO productionOrderDetailDTO);

    ProductionOrderDTO updateStatusByProductionOrderId(Long id, User currUser);

}
