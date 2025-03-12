package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.repositories.PurchaseOrderRepository;
import fpt.g36.gapms.services.PurchaseOrderService;
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
    public PurchaseOrder updatePurchaseOrderStatus(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));
        po.setStatus(BaseEnum.NOT_APPROVED);
        return purchaseOrderRepository.save(po);
    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrder() {

        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        //Map PurchaseOrder to PurchaseOrderDTO
        return orders.stream().map(order -> {
            PurchaseOrderDTO dto = new PurchaseOrderDTO();
            dto.setPurchaseOrderId(order.getId());
            dto.setCustomerName(order.getQuotation().getRfq().getCreateBy().getUsername());
            dto.setStatus(order.getStatus());
            dto.setQuotationId(order.getQuotation().getId());
            dto.setContractId(order.getContract() != null ? order.getContract().getId() : null);
            dto.setApprovedByUserName(order.getApprovedBy() != null ? order.getApprovedBy().getUsername() : null);
            return dto;
        }).collect(Collectors.toList());
    }
}
