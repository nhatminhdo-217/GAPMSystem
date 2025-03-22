package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.production_order.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDetailDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductionOrderService {

    Page<ProductionOrderDTO> findPaginated(Integer page, Integer pageSize , String sortField, String sortDir);

    ProductionOrderDTO findById(Long id);

    List<ProductionOrderDetailDTO> findDetailByProductionOrderId(Long id);

    ProductionOrderDetailDTO findDetailById(Long id);
}
