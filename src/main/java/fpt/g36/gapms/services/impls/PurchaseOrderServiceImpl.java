package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationDTO;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.models.entities.PurchaseOrderDetail;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.mapper.PurchaseOrderMapper;
import fpt.g36.gapms.repositories.PurchaseOrderRepository;
import fpt.g36.gapms.services.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, PurchaseOrderMapper purchaseOrderMapper) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
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

        if (getStatusByPurchaseOrderId(id).equals(BaseEnum.DRAFT)) {
            po.setStatus(BaseEnum.NOT_APPROVED);
            po.setManageBy(currUser);
        }
        else if (getStatusByPurchaseOrderId(id) == BaseEnum.NOT_APPROVED){
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

    @Override
    public boolean checkContractWithStatus(BaseEnum status, Long id) {

        return status.equals(BaseEnum.NOT_APPROVED) && isPurchaseOrderContract(id);

    }

    @Override
    public boolean cancelPurchaseOrder(Long id) {
        Optional<PurchaseOrder> purchaseOrder = getPurchaseOrderById(id);
        if (purchaseOrder.isPresent()) {
            PurchaseOrder po = purchaseOrder.get();
            if (po.getStatus().equals(BaseEnum.WAIT_FOR_APPROVAL)) {
                po.setStatus(BaseEnum.CANCELED);
                purchaseOrderRepository.save(po);
                return true;
            }
        }
        return false;
    }

    @Override
    public PurchaseOrderDetail getPurchaseOrderDetailById(Long id) {
        Optional<PurchaseOrderDetail> purchaseOrderDetail = purchaseOrderRepository.getPurchaseOrderDetailById(id);
        if (purchaseOrderDetail.isPresent()) {
            return purchaseOrderDetail.get();
        } else {
            throw new RuntimeException("Purchase Order Detail not found");
        }
    }

    @Override
    public PurchaseOrder getPurchaseOrderDetailByQuotationId(Long Id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getPurchaseOrderByQuotationId(Id);
        return purchaseOrder;
    }

    @Override
    public Page<PurchaseOrderDTO> getAllPurchaseOrderWithSearchFilter(String search, BaseEnum status, int page, int size, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortField));

        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepository.searchAndFilter(search, status, pageable);

        List<PurchaseOrderDTO> purchaseOrderDTOS = new ArrayList<>(purchaseOrderMapper.toListDTO(purchaseOrders));

        sortPurchaseOrderDTOs(purchaseOrderDTOS, sortDir);

        return new PageImpl<>(purchaseOrderDTOS, pageable, purchaseOrders.getTotalElements());
    }

    @Override
    public Page<PurchaseOrderDTO> getAllByRole(User currUser, String search, BaseEnum status, int page, int size, String sortField, String sortDir) {;

        if (Objects.equals(currUser.getRole().getName(), "SALE_STAFF")){
            return getAllPurchaseOrderWithSearchFilter(search, status, page, size, sortField, sortDir);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending().and(Sort.by("status").ascending()));

        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepository.searchAndFilter(search, BaseEnum.WAIT_FOR_APPROVAL, pageable);

        List<PurchaseOrderDTO> purchaseOrderDTOS = new ArrayList<>(purchaseOrderMapper.toListDTO(purchaseOrders));
        sortPurchaseOrderDTOs(purchaseOrderDTOS, sortDir);

        return new PageImpl<>(purchaseOrderDTOS, pageable, purchaseOrders.getTotalElements());
    }

    private boolean isPurchaseOrderContract(Long id){
        Optional<PurchaseOrder> purchaseOrder = getPurchaseOrderById(id);
        return purchaseOrder.filter(order -> order.getContract() != null).isPresent();
    }

    private void sortPurchaseOrderDTOs(List<PurchaseOrderDTO> content, String sortDir) {
        // Sort by isAccepted priority: DRAFT -> NOT_APPROVED -> WAIT_FOR_APPROVAL -> APPROVED -> CANCELED
        // and then by createAt
        content.sort((q1, q2) -> {
            int statusCompare = compareStatus(q1.getStatus(), q2.getStatus());
            if (statusCompare != 0) {
                return statusCompare;
            }

            // Then sort by createAt (ascending or descending based on sortDir)
            if ("asc".equals(sortDir)) {
                return q1.getCreateAt().compareTo(q2.getCreateAt());
            } else {
                return q2.getCreateAt().compareTo(q1.getCreateAt());
            }
        });
    }

    private int compareStatus(BaseEnum status1, BaseEnum status2) {
        if (status1 == status2) return 0;
        if (status1 == null) return -1;
        if (status2 == null) return 1;

        // Define priority order
        Map<BaseEnum, Integer> priorityMap = new HashMap<>();
        priorityMap.put(BaseEnum.DRAFT, 1);
        priorityMap.put(BaseEnum.NOT_APPROVED, 2);
        priorityMap.put(BaseEnum.WAIT_FOR_APPROVAL, 3);
        priorityMap.put(BaseEnum.APPROVED, 4);
        priorityMap.put(BaseEnum.CANCELED, 5);

        Integer priority1 = priorityMap.getOrDefault(status1, 99);
        Integer priority2 = priorityMap.getOrDefault(status2, 99);

        return priority1.compareTo(priority2);
    }
}
