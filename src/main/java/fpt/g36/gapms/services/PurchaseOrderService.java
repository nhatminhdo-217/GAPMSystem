package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.entities.PurchaseOrder;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderService {

    Optional<PurchaseOrderInfoDTO> getPurchaseOrderInfoDTOById(Long id);

    List<PurchaseOrderItemsDTO> getPurchaseOrderItemsDTOById(Long id);

    PurchaseOrder updatePurchaseOrderStatus(Long id);

    List<PurchaseOrderDTO> getAllPurchaseOrder();
}
