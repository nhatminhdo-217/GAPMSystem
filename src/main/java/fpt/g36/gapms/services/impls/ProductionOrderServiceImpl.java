package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.production_order.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDetailDTO;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.mapper.ProductionOrderMapper;
import fpt.g36.gapms.repositories.ProductionOrderRepository;
import fpt.g36.gapms.services.ProductionOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderMapper productionOrderMapper;

    public ProductionOrderServiceImpl(ProductionOrderRepository productionOrderRepository, ProductionOrderMapper productionOrderMapper) {
        this.productionOrderRepository = productionOrderRepository;
        this.productionOrderMapper = productionOrderMapper;
    }

    @Override
    public Page<ProductionOrderDTO> findPaginated(Integer page, Integer pageSize, String sortField, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        return productionOrderRepository.findAll(pageable).map(productionOrderMapper::toDTO);
    }

    @Override
    public ProductionOrderDTO findById(Long id) {

        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production Order not found"));
        return productionOrderMapper.toDTO(productionOrder);
    }

    @Override
    public List<ProductionOrderDetailDTO> findDetailByProductionOrderId(Long id) {

        return productionOrderRepository.findAllByProductionOrderId(id)
                .stream()
                .map(productionOrderMapper::toDetailDTO)
                .toList();
    }

    @Override
    public ProductionOrderDetailDTO findDetailById(Long id) {

        return productionOrderRepository.findByProductionOrderId(id)
                .map(productionOrderMapper::toDetailDTO)
                .orElseThrow(() -> new RuntimeException("Production Order Detail not found"));
    }
}
