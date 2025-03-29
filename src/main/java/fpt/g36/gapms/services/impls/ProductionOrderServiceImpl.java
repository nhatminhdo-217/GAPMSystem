package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDetailDTO;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.ProductionOrderDetail;
import fpt.g36.gapms.models.entities.PurchaseOrderDetail;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.mapper.ProductionOrderMapper;
import fpt.g36.gapms.repositories.ProductionOrderDetailRepository;
import fpt.g36.gapms.repositories.ProductionOrderRepository;
import fpt.g36.gapms.services.ProductionOrderService;
import fpt.g36.gapms.services.PurchaseOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderMapper productionOrderMapper;
    private final ProductionOrderDetailRepository productionOrderDetailRepository;
    private final PurchaseOrderService purchaseOrderService;

    public ProductionOrderServiceImpl(ProductionOrderRepository productionOrderRepository, ProductionOrderMapper productionOrderMapper, ProductionOrderDetailRepository productionOrderDetailRepository, PurchaseOrderService purchaseOrderService) {
        this.productionOrderRepository = productionOrderRepository;
        this.productionOrderMapper = productionOrderMapper;
        this.productionOrderDetailRepository = productionOrderDetailRepository;
        this.purchaseOrderService = purchaseOrderService;
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

    @Override
    public void createProductionOrder(Long id) {

        ProductionOrder productionOrder = new ProductionOrder();

        productionOrder.setStatus(BaseEnum.DRAFT);
        productionOrder.setPurchaseOrder(purchaseOrderService.getPurchaseOrderById(id).get());
        ProductionOrder savedProductionOrder = productionOrderRepository.save(productionOrder);

        List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderService.getPurchaseOrderById(id).get().getPurchaseOrderDetails();

        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
            ProductionOrderDetail productionOrderDetail = new ProductionOrderDetail();
            productionOrderDetail.setProductionOrder(savedProductionOrder);
            productionOrderDetail.setPurchaseOrderDetail(purchaseOrderDetail);
            productionOrderDetail.setThread_mass(calculateThreadMassByPurchaseOrderDetailId(purchaseOrderDetail.getId()));
            productionOrderDetailRepository.save(productionOrderDetail);
        }
    }

    @Override
    public BaseEnum getStatusByProductionOrder(Long id) {
        Optional<ProductionOrder> productionOrder = productionOrderRepository.findById(id);
        return productionOrder.map(ProductionOrder::getStatus).orElse(null);
    }

    @Override
    public ProductionOrder updateStatus(Long id, User currUser) {
        ProductionOrder po = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production Order not found"));

        if (getStatusByProductionOrderId(id).equals(BaseEnum.DRAFT)) {
            po.setStatus(BaseEnum.NOT_APPROVED);
            po.setCreatedBy(currUser);
        }
        else if (getStatusByProductionOrderId(id) == BaseEnum.NOT_APPROVED){
            po.setStatus(BaseEnum.WAIT_FOR_APPROVAL);
        } else if (getStatusByProductionOrderId(id) == BaseEnum.WAIT_FOR_APPROVAL) {
            po.setStatus(BaseEnum.APPROVED);
            po.setApprovedBy(currUser);
        }
        return productionOrderRepository.save(po);
    }

    @Override
    public boolean cancelProductionOrder(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production Order not found"));

        if (productionOrder.getStatus() == BaseEnum.APPROVED) {
            return false;
        }

        productionOrder.setStatus(BaseEnum.CANCELED);
        productionOrderRepository.save(productionOrder);
        return true;
    }

    private BaseEnum getStatusByProductionOrderId(Long id) {
        Optional<ProductionOrder> purchaseOrder = productionOrderRepository.findById(id);
        return purchaseOrder.map(ProductionOrder::getStatus).orElse(null);
    }

    private BigDecimal calculateThreadMassByPurchaseOrderDetailId(Long id) {
        PurchaseOrderDetail purchaseOrderDetail = purchaseOrderService.getPurchaseOrderDetailById(id);

        return purchaseOrderDetail.getProduct().getThread().getConvert_rate().multiply(new BigDecimal(purchaseOrderDetail.getQuantity()));
    }

}
