package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationDTO;
import fpt.g36.gapms.models.entities.Contract;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.models.entities.PurchaseOrderDetail;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.mapper.PurchaseOrderMapper;
import fpt.g36.gapms.repositories.ContractRepository;
import fpt.g36.gapms.repositories.PurchaseOrderRepository;
import fpt.g36.gapms.services.*;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
   private final ImageService imageService;
   private final ContractRepository contractRepository;
   private final UserUtils userUtils;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, PurchaseOrderMapper purchaseOrderMapper, ImageService imageService, ContractRepository contractRepository, UserUtils userUtils) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.imageService = imageService;
        this.contractRepository = contractRepository;
        this.userUtils = userUtils;
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
    public Page<PurchaseOrder> getAllPurchaseOrderByUserId(Long userId, Pageable pageable, Integer year, String sanitizedSearchQuery) {
        return purchaseOrderRepository.getAllPurchaseOrdersByUserIdAndSearch(userId, year,sanitizedSearchQuery, pageable);
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
    public PurchaseOrder uploadContract(PurchaseOrder purchaseOrder,String contractCode,  Long purchaseOrderId, User uploadBy, MultipartFile contractImage) throws IOException {
        String contractFileSave = imageService.saveImageMultiFile(contractImage);

        Contract contract = new Contract();
        contract.setId(generateNewContractId());
        contract.setName(userUtils.cleanSpaces(contractCode));
        contract.setPath(contractFileSave);
        contract.setStatus(BaseEnum.NOT_APPROVED);
        contract.setCreateBy(uploadBy);
        Contract contract_save = contractRepository.save(contract);

        PurchaseOrder purchaseOrder_save = purchaseOrderRepository.findById(purchaseOrderId).orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        purchaseOrder_save.setContracts(contract_save);
        purchaseOrder_save.setStatus(BaseEnum.WAIT_FOR_APPROVAL);
        purchaseOrderRepository.save(purchaseOrder_save);
        return purchaseOrder_save;
    }

    @Override
    public PurchaseOrder reUploadContract(String contractCode, Long purchaseOrderId, MultipartFile contractImage) throws IOException {
        String contractFileSave = imageService.saveImageMultiFile(contractImage);
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId).orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        Contract contract = contractRepository.findById(purchaseOrder.getContract().getId()).orElseThrow(() -> new RuntimeException("Contract not found"));
        contract.setName(userUtils.cleanSpaces(contractCode));
        contract.setPath(contractFileSave);
        Contract contract_save = contractRepository.save(contract);
        return purchaseOrder;
    }

    @Override
    public PurchaseOrder reUploadContract(String contractCode, Long purchaseOrderId) throws IOException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId).orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        Contract contract = contractRepository.findById(purchaseOrder.getContract().getId()).orElseThrow(() -> new RuntimeException("Contract not found"));
        contract.setName(userUtils.cleanSpaces(contractCode));
        Contract contract_save = contractRepository.save(contract);
        return purchaseOrder;
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

//        sortPurchaseOrderDTOs(purchaseOrderDTOS, sortDir);

        return new PageImpl<>(purchaseOrderDTOS, pageable, purchaseOrders.getTotalElements());
    }

    @Override
    public Page<PurchaseOrderDTO> getAllByRole(User currUser, String search, BaseEnum status, int page, int size, String sortField, String sortDir) {;

        if (Objects.equals(currUser.getRole().getName(), "SALE_STAFF")){
            return getAllPurchaseOrderWithSearchFilter(search, status, page, size, sortField, sortDir);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending().and(Sort.by("status").ascending()));

        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepository.searchAndFilterByStatus(search, status, pageable);

        List<PurchaseOrderDTO> purchaseOrderDTOS = new ArrayList<>(purchaseOrderMapper.toListDTO(purchaseOrders));
//        sortPurchaseOrderDTOs(purchaseOrderDTOS, sortDir);

        return new PageImpl<>(purchaseOrderDTOS, pageable, purchaseOrders.getTotalElements());
    }

    private boolean isPurchaseOrderContract(Long id){
        Optional<PurchaseOrder> purchaseOrder = getPurchaseOrderById(id);
        return purchaseOrder.filter(order -> order.getContract() != null).isPresent();
    }




    private String generateNewContractId() {
        // Lấy ID lớn nhất hiện có
        String maxId = contractRepository.findMaxContractId();

        // Sinh ID tiếp theo
        return createNextId(maxId);
    }

    private String createNextId(String currentMaxId) {

        if (currentMaxId == null || currentMaxId.isEmpty()) {
            return "HD0001";
        }

        // currentMaxId ví dụ: "HD0003"
        // Tách phần số ra: "0003"
        String numericPart = currentMaxId.substring(2); // Bỏ 'HD'

        // Chuyển sang int để +1
        int num = Integer.parseInt(numericPart);
        num++;

        // Format lại thành 4 chữ số: 4 -> "0004"
        String nextNumeric = String.format("%04d", num);

        // Ghép chuỗi
        return "HD" + nextNumeric;
    }
}
