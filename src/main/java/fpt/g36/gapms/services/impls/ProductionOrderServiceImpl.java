package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.technical.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.technical.ProductionOrderDetailsDTO;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.ProductionOrderDetail;
import fpt.g36.gapms.models.entities.WorkOrder;
import fpt.g36.gapms.repositories.ProductionOrderRepository;
import fpt.g36.gapms.repositories.WorkOrderRepository;
import fpt.g36.gapms.services.ProductionOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductionOrderServiceImpl implements ProductionOrderService {
    private final ProductionOrderRepository productionOrderRepository;
    private final WorkOrderRepository workOrderRepository;

    public ProductionOrderServiceImpl(ProductionOrderRepository productionOrderRepository, WorkOrderRepository workOrderRepository) {
        this.productionOrderRepository = productionOrderRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Override
    public Page<ProductionOrderDTO> getApprovedProductionOrders(Pageable pageable) {
        Page<ProductionOrder> productionOrders = productionOrderRepository.findByStatus(BaseEnum.APPROVED, pageable);
        List<ProductionOrderDTO> dtos = productionOrders.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, productionOrders.getTotalElements());
    }

    @Transactional
    @Override
    public ProductionOrderDetailsDTO getProductionOrderDetails(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy Production Order với ID: " + id));
        ProductionOrderDetailsDTO dto = new ProductionOrderDetailsDTO();
        dto.setId(productionOrder.getId());
        dto.setStatus(productionOrder.getStatus());
        dto.setCreatedBy(productionOrder.getCreatedBy());
        dto.setApprovedBy(productionOrder.getApprovedBy());
        dto.setPurchaseOrder(productionOrder.getPurchaseOrder());
        dto.setCreateAt(productionOrder.getCreateAt());
        dto.setUpdateAt(productionOrder.getUpdateAt());
        dto.setProductionOrderDetails(productionOrder.getProductionOrderDetails().stream().map(this::convertToDetailItem).collect(Collectors.toList()));
        WorkOrder workOrder = workOrderRepository.findByProductionOrder(productionOrder);
        dto.setWorkOrder(workOrder);
        return dto;
    }

    @Override
    public ProductionOrder getProductionOrderById(Long id) {
        return productionOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy Production Order với ID: " + id));
    }

    private ProductionOrderDTO convertToDTO(ProductionOrder productionOrder) {
        ProductionOrderDTO dto = new ProductionOrderDTO();
        dto.setId(productionOrder.getId());
        dto.setStatus(productionOrder.getStatus());
        dto.setCreatedBy(productionOrder.getCreatedBy());
        dto.setApprovedBy(productionOrder.getApprovedBy());
        dto.setPurchaseOrder(productionOrder.getPurchaseOrder());
        dto.setCreateAt(productionOrder.getCreateAt());
        dto.setUpdateAt(productionOrder.getUpdateAt());
        WorkOrder workOrder = workOrderRepository.findByProductionOrder(productionOrder);
        dto.setHasWorkOrder(workOrder != null);
        return dto;
    }

    private ProductionOrderDetailsDTO.ProductionOrderDetailItem convertToDetailItem(ProductionOrderDetail detail) {
        ProductionOrderDetailsDTO.ProductionOrderDetailItem item = new ProductionOrderDetailsDTO.ProductionOrderDetailItem();
        item.setId(detail.getId());
        item.setThreadMass(detail.getThread_mass());
        item.setLightEnv(detail.getLight_env());
        item.setDescription(detail.getDescription());
        item.setHasWorkOrderDetail(detail.getWorkOrderDetail() != null);
        return item;
    }
}
