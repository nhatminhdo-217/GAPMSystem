package fpt.g36.gapms.services;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderService {

    Optional<PurchaseOrderInfoDTO> getPurchaseOrderInfoDTOById(Long id);

    List<PurchaseOrderItemsDTO> getPurchaseOrderItemsDTOById(Long id);

    PurchaseOrder updatePurchaseOrderStatus(Long id, User currUser);

    List<PurchaseOrderDTO> getAllPurchaseOrder();

    Optional<PurchaseOrder> getPurchaseOrderById(Long id);

    BaseEnum getStatusByPurchaseOrderId(Long id);

    List<PurchaseOrderDTO> getAllPurchaseOrderByRole(User currUser);

    Page<PurchaseOrder> getAllPurchaseOrderByUserId(Long userId, Pageable pageable, Integer year);

    PurchaseOrder getPurchaseOrderCustomerDetail(Long purchase_order_id);
}
