package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.production_order.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDetailDTO;
import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductionOrderService {

    Page<ProductionOrderDTO> findPaginatedByRoles(Integer page, Integer pageSize , String sortField, String sortDir, User currUser);

    ProductionOrderDTO findById(Long id);

    List<ProductionOrderDetailDTO> findDetailByProductionOrderId(Long id);

    ProductionOrderDetailDTO findDetailById(Long id);

    ProductionOrderDetailDTO updateProductionOrderDetail(ProductionOrderDetailDTO productionOrderDetailDTO);

    ProductionOrderDTO updateStatusByProductionOrderId(Long id, User currUser);
}
