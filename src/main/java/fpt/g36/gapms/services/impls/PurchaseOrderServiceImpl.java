package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.PurchaseOrderRepository;
import fpt.g36.gapms.services.PurchaseOrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public Optional<PurchaseOrderInfoDTO> getPurchaseOrderInfoDTOById(Long id) {
        return purchaseOrderRepository.getPurchaseOrderInfoDTOById(id);
    }

    @Override
    public List<PurchaseOrderItemsDTO> getPurchaseOrderItemsDTOById(Long id) {
        List<Object[]> result = purchaseOrderRepository.getPurchaseOrderItemsDTOById(id);
        return result.stream().map(item -> new PurchaseOrderItemsDTO(
                (String) item[0],
                (String) item[1],
                (String) item[2],
                (String) item[3],
                ((Number) item[4]).intValue(),
                (BigDecimal) item[5],
                (BigDecimal) item[6]
        )).collect(Collectors.toList());
    }

    @Override
    public PurchaseOrder updatePurchaseOrderStatus(Long id, User currUser) {
        PurchaseOrder po = getPurchaseOrderById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        if (getStatusByPurchaseOrderId(id) == BaseEnum.NOT_APPROVED){
            po.setStatus(BaseEnum.WAIT_FOR_APPROVAL);
        } else if (getStatusByPurchaseOrderId(id) == BaseEnum.WAIT_FOR_APPROVAL) {
            po.setStatus(BaseEnum.APPROVED);
            po.setApprovedBy(currUser);
        }
        return purchaseOrderRepository.save(po);
    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrder() {

        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        //Map PurchaseOrder to PurchaseOrderDTO
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrderByRole(User currUser) {

        //Check if current user is SALE_STAFF then return all purchase order
        if (currUser.getRole().getName().equals("SALE_STAFF")) {
            return getAllPurchaseOrder();
        }

        //Check if current user is SALE_MANAGER then return all not approve purchase order
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createAt").descending());
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.getAllByStatus(BaseEnum.WAIT_FOR_APPROVAL, pageable);

        //Map PurchaseOrder to PurchaseOrderDTO
        return purchaseOrders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<PurchaseOrder> getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    @Override
    public BaseEnum getStatusByPurchaseOrderId(Long id) {
        Optional<PurchaseOrder> purchaseOrder = getPurchaseOrderById(id);
        return purchaseOrder.map(PurchaseOrder::getStatus).orElse(null);
    }

    private PurchaseOrderDTO convertToDTO(PurchaseOrder order) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setPurchaseOrderId(order.getId());
        dto.setCustomerName(order.getQuotation().getRfq().getCreateBy().getUsername());
        dto.setStatus(order.getStatus());
        dto.setQuotationId(order.getQuotation().getId());
        dto.setContractId(order.getContract() != null ? order.getContract().getId() : null);
        dto.setApprovedByUserName(order.getApprovedBy() != null ? order.getApprovedBy().getUsername() : null);
        dto.setCreateByUserName(order.getQuotation().getCreatedBy() != null ? order.getQuotation().getCreatedBy().getUsername() : null);
        dto.setCreateAt(order.getUpdateAt().toLocalDate());
        return dto;
    }

    @Override
    public Page<PurchaseOrder> getAllPurchaseOrderByUserId(Long userId, Pageable pageable, Integer year) {
        return purchaseOrderRepository.getAllPurchaseOrdersByUserIdAndYear(userId, year, pageable);
    }

    @Override
    public PurchaseOrder getPurchaseOrderCustomerDetail(Long purchase_order_id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getPurchaseOrderCustomerDetail(purchase_order_id);
        return purchaseOrder;
    }
}
