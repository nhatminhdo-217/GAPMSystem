package fpt.g36.gapms.controller.purchase_order;

import fpt.g36.gapms.models.dto.contract.ContractDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.entities.Contract;
import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.ContractService;
import fpt.g36.gapms.services.PurchaseOrderService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.services.impls.UserServiceImpl;
import fpt.g36.gapms.utils.UserUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/purchase-order")
public class PurchaseOrderController {

    private final UserUtils userUtils;
    private final PurchaseOrderService purchaseOrderService;
    private final ContractService contractService;
    private final UserService userService;

    public PurchaseOrderController(UserUtils userUtils, PurchaseOrderService purchaseOrderService, ContractService contractService, UserService userService) {
        this.userUtils = userUtils;
        this.purchaseOrderService = purchaseOrderService;
        this.contractService = contractService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public String listPurchaseOrders(Model model) {

        userUtils.getOptionalUser(model);

        User currUser = userUtils.getOptionalUserInfo(model);

        List<PurchaseOrderDTO> ordersByRole = purchaseOrderService.getAllPurchaseOrderByRole(currUser);

        model.addAttribute("orders", ordersByRole);

        return "purchase-order/list_purchase_order";
    }

    @GetMapping("/detail/{id}")
    public String getPurchaseOrderDetailPage(@PathVariable Long id, Model model) {

        userUtils.getOptionalUser(model);

        User currUser = userUtils.getOptionalUserInfo(model);

        Optional<PurchaseOrderInfoDTO> data = purchaseOrderService.getPurchaseOrderInfoDTOById(id);
        List<PurchaseOrderItemsDTO> items = purchaseOrderService.getPurchaseOrderItemsDTOById(id);

        PurchaseOrderInfoDTO purchaseOrderInfoDTO = data.get();

        model.addAttribute("orderInfo", purchaseOrderInfoDTO);
        model.addAttribute("items", items);
        model.addAttribute("currUser", currUser);
        model.addAttribute("purchaseOrderId", id);

        return "purchase-order/purchase_order_detail";
    }

    @PostMapping("/detail/{id}")
    public String postPurchaseOrderDetailPage(@PathVariable Long id) {

        User currUser = userUtils.getOptionalUserInfo();

        PurchaseOrder po = purchaseOrderService.updatePurchaseOrderStatus(id, currUser);

        return "redirect:/purchase-order/detail/" + po.getId();
    }

    @GetMapping("/detail/{purchaseId}/contract/{id}")
    public String getContractPage(
            @PathVariable Long purchaseId,
            @PathVariable String id, Model model) {

        userUtils.getOptionalUser(model);

        User currUser = userUtils.getOptionalUserInfo(model);

        Optional<Contract> contract = contractService.findById(id);

        model.addAttribute("contract", contract.get());
        model.addAttribute("purchaseOrderId", purchaseId);
        model.addAttribute("currUser", currUser);

        return "contract/contracts";
    }

    @GetMapping("/detail/{purchaseId}/contract/{id}/edit")
    public String getEditContractPage(
            @PathVariable Long purchaseId,
            @PathVariable String id, Model model) {

        userUtils.getOptionalUser(model);

        Optional<Contract> contract = contractService.findById(id);
        model.addAttribute("contract", contract.get());
        model.addAttribute("purchaseOrderId", purchaseId);

        return "contract/edit_contract";
    }

    @PostMapping("/detail/{purchaseId}/contract/{id}/edit")
    public String postEditContractPage(
            @PathVariable Long purchaseId,
            @PathVariable String id,
            @Valid @ModelAttribute ContractDTO contractDTO,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
            Model model) throws IOException {

        if (bindingResult.hasErrors()){
            userUtils.getOptionalUser(model);
            return "contract/edit_contract";
        }
        try {
            Contract contract = contractService.updateContract(id, contractDTO, file);
            redirectAttributes.addFlashAttribute("success", "Cập nhật hợp đồng thành công");
            return "redirect:/purchase-order/detail/" + purchaseId + "/contract/" + contract.getId();
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Cập nhật hợp đồng thất bại");
            return "redirect:/purchase-order/detail/" + purchaseId + "/contract/" + id + "/edit";
        }
    }

    @GetMapping("/detail/{id}/contract/upload")
    public String getUploadContractPage(@PathVariable Long id, Model model) {

        userUtils.getOptionalUser(model);

        model.addAttribute("purchaseOrderId", id);
        model.addAttribute("contractDTO", new ContractDTO());

        return "contract/upload_contract";
    }

    @PostMapping("/detail/{id}/contract/upload")
    public String postUploadContractPage(
            @PathVariable Long id,
            @Valid @ModelAttribute ContractDTO contractDTO,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()){
            userUtils.getOptionalUser(model);
            return "contract/upload_contract";
        }
        try {
            Contract contract = contractService.createContract(id, contractDTO, file);
            redirectAttributes.addFlashAttribute("success", "Tạo hợp đồng thành công");
            return "redirect:/purchase-order/detail/" + id + "/contract/" + contract.getId();
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Tạo hợp đồng thất bại");
            return "redirect:/purchase-order/detail/" + id + "/contract/upload";
        }
    }



    @GetMapping("/customer/list")
    public String getAllPurchaseOrderByUserId(Model model,
                                              @RequestParam(value = "page", defaultValue = "0") String pageStr,
                                              @RequestParam(value = "size", defaultValue = "5") String sizeStr,
                                              @RequestParam(value = "year", required = false, defaultValue = "2025") String yearStr) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Xử lý page
        int page;
        try {
            page = Integer.parseInt(pageStr);
            if (page < 0) { // Không cho phép page âm
                page = 0; // Đặt về mặc định nếu không hợp lệ
            }
        } catch (NumberFormatException e) {
            page = 0; // Nếu không parse được (ví dụ: "l"), đặt về 0
        }

        // Xử lý size
        int size;
        try {
            size = Integer.parseInt(sizeStr);
            if (size <= 0 || size > 100) { // Giới hạn size từ 1 đến 100
                size = 5; // Đặt về mặc định nếu không hợp lệ
            }
        } catch (NumberFormatException e) {
            size = 5; // Nếu không parse được (ví dụ: "l"), đặt về 5
        }

        Integer year = null;
        if (yearStr != null && !yearStr.trim().isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
                if (year < 2025 || year > 2035) {
                    year = null;
                }
            } catch (NumberFormatException e) {
                year = null;
            }
        }
        Page<PurchaseOrder> purchaseOrders = Page.empty(); // Khởi tạo danh sách rỗng mặc định
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (optionalUser.isPresent()) {
                Pageable pageable = PageRequest.of(page, size);
                purchaseOrders = purchaseOrderService.getAllPurchaseOrderByUserId(
                        optionalUser.get().getId(), pageable, year); // Gọi service với year có thể null
            }
        }
        model.addAttribute("currentPage", purchaseOrders.getNumber());
        model.addAttribute("totalPages", purchaseOrders.getTotalPages());
        model.addAttribute("totalItems", purchaseOrders.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("purchaseOrders", purchaseOrders);
        model.addAttribute("selectedYear", year != null ? year : ""); // Trả về năm đã xử lý hoặc rỗng // Trả về năm đã xử lý hoặc rỗng

        userUtils.getOptionalUser(model);
        return "purchase-order/purchase-order-list-customer";
    }


    @GetMapping("/customer/detail/{poi}")
    public String getPurchaseOrderCustomerDetail(@PathVariable(value = "poi") Long poi, Model model){
        PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrderCustomerDetail(poi);
        userUtils.getOptionalUser(model);
        model.addAttribute("purchaseOrder",purchaseOrder);
        return "purchase-order/purchase-order-customer-detail";
    }
}
