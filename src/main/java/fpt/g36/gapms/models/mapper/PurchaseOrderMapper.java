package fpt.g36.gapms.models.mapper;

import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurchaseOrderMapper {

    public List<PurchaseOrderDTO> toListDTO(Page<PurchaseOrder> purchaseOrder) {
        List<PurchaseOrder> listPurchaseOrder = purchaseOrder.getContent();

        return listPurchaseOrder.stream()
                .map(this::toDTO)
                .toList();
    }

    private PurchaseOrderDTO toDTO(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }

        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setPurchaseOrderId(purchaseOrder.getId());
        dto.setCustomerName(purchaseOrder.getCustomer().getUsername());
        dto.setStatus(purchaseOrder.getStatus());
        dto.setQuotationId(purchaseOrder.getQuotation().getId());
        dto.setContractId(purchaseOrder.getContract() != null ? purchaseOrder.getContract().getId() : null);
        dto.setApprovedByUserName(purchaseOrder.getApprovedBy() != null ? purchaseOrder.getApprovedBy().getUsername() : null);
        dto.setCreateByUserName(purchaseOrder.getManageBy() != null ? purchaseOrder.getManageBy().getUsername() : null);
        dto.setCreateAt(purchaseOrder.getUpdateAt().toLocalDate());

        return dto;
    }
}
