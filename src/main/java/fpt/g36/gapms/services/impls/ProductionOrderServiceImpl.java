package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDetailDTO;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.ProductionOrderDetail;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.mapper.ProductionOrderMapper;
import fpt.g36.gapms.repositories.ProductionOrderDetailRepository;
import fpt.g36.gapms.repositories.ProductionOrderRepository;
import fpt.g36.gapms.services.ProductionOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderMapper productionOrderMapper;
    private final ProductionOrderDetailRepository productionOrderDetailRepository;

    public ProductionOrderServiceImpl(ProductionOrderRepository productionOrderRepository, ProductionOrderMapper productionOrderMapper, ProductionOrderDetailRepository productionOrderDetailRepository) {
        this.productionOrderRepository = productionOrderRepository;
        this.productionOrderMapper = productionOrderMapper;
        this.productionOrderDetailRepository = productionOrderDetailRepository;
    }

    @Override
    public Page<ProductionOrderDTO> findPaginatedByRoles(Integer page, Integer pageSize, String sortField, String sortDir, User currUser) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        if (currUser.getRole().getName().equals("SALE_STAFF")) {
            Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
            return productionOrderRepository.findAll(pageable).map(productionOrderMapper::toDTO);
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        Page<ProductionOrder> productionOrders = productionOrderRepository.findAllByStatus(BaseEnum.WAIT_FOR_APPROVAL, pageable);
        return productionOrders.map(productionOrderMapper::toDTO);

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

    @Override
    public ProductionOrderDetailDTO updateProductionOrderDetail(ProductionOrderDetailDTO detailDTO) {

        ProductionOrderDetail existingDetail = productionOrderDetailRepository.findById(detailDTO.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng sản xuất với ID: " + detailDTO.getId()));

        existingDetail.setLight_env(detailDTO.isLightEnv());
        existingDetail.setThread_mass(detailDTO.getThreadMass());
        existingDetail.setUpdateAt(LocalDateTime.now());

        ProductionOrderDetail updatedDetail = productionOrderDetailRepository.save(existingDetail);

        return productionOrderMapper.convertToDTO(updatedDetail);
    }

    @Override
    public ProductionOrderDTO updateStatusByProductionOrderId(Long id, User currUser) {
        ProductionOrder po = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production Order not found"));

        if (getStatusByProductionOrderId(id) == BaseEnum.NOT_APPROVED){
            po.setStatus(BaseEnum.WAIT_FOR_APPROVAL);
        } else if (getStatusByProductionOrderId(id) == BaseEnum.WAIT_FOR_APPROVAL) {
            po.setStatus(BaseEnum.APPROVED);
            po.setApprovedBy(currUser);
        }
        return productionOrderMapper.toDTO(productionOrderRepository.save(po));
    }

    private BaseEnum getStatusByProductionOrderId(Long id) {
        Optional<ProductionOrder> purchaseOrder = productionOrderRepository.findById(id);
        return purchaseOrder.map(ProductionOrder::getStatus).orElse(null);
    }


}
