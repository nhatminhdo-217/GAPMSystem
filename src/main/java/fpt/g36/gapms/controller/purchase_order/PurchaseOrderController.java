package fpt.g36.gapms.controller.purchase_order;

import fpt.g36.gapms.enums.BaseEnum;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/purchase-order")
public class PurchaseOrderController {

    private final UserUtils userUtils;
    private final PurchaseOrderService purchaseOrderService;
    private final ContractService contractService;
    private final UserService userService;
    private static String latestImagePath = null;

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
    public String postPurchaseOrderDetailPage(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {

        User currUser = userUtils.getOptionalUserInfo();

        BaseEnum status = purchaseOrderService.getStatusByPurchaseOrderId(id);

        if (status.equals(BaseEnum.NOT_APPROVED)) {
            boolean isPurchaseOrderContract = purchaseOrderService.checkContractWithStatus(status, id);
            if (!isPurchaseOrderContract) {
                redirectAttributes.addFlashAttribute("error", "Không thể gửi đơn hàng khi chưa có hợp đồng");
                return "redirect:/purchase-order/detail/" + id;
            }

            PurchaseOrder po = purchaseOrderService.updatePurchaseOrderStatus(id, currUser);
            redirectAttributes.addFlashAttribute("success", "Cập nhật đơn hàng thành công");
            return "redirect:/purchase-order/detail/" + po.getId();
        }else {
            if (status.equals(BaseEnum.WAIT_FOR_APPROVAL)) {
                contractService.updateContractStatus(id, currUser);
                redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được phê duyệt");
            } else {
                redirectAttributes.addFlashAttribute("success", "Cập nhật đơn hàng thành công");
            }
            PurchaseOrder po = purchaseOrderService.updatePurchaseOrderStatus(id, currUser);
            return "redirect:/purchase-order/detail/" + po.getId();
        }
    }

    @GetMapping("/detail/{id}/cancel")
    public String getPurchaseOrderCancelPage(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {

        return postPurchaseOrderCancelPage(id, redirectAttributes, model);
    }

    @PostMapping("/detail/{id}/cancel")
    public String postPurchaseOrderCancelPage(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {

        boolean isCancel = purchaseOrderService.cancelPurchaseOrder(id);

        if (isCancel) {
            redirectAttributes.addFlashAttribute("success", "Hủy đơn hàng thành công");
        } else {
            redirectAttributes.addFlashAttribute("error", "Đơn hàng đã được phê duyệt, không thể hủy");
        }
        return "redirect:/purchase-order/detail/" + id;
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
            if (!contractService.updateContractToPurchaseOrder(id, contract)){
                System.err.println("Update contract to purchase order failed");
            }
            redirectAttributes.addFlashAttribute("success", "Tạo hợp đồng thành công");
            return "redirect:/purchase-order/detail/" + id + "/contract/" + contract.getId();
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Tạo hợp đồng thất bại");
            return "redirect:/purchase-order/detail/" + id + "/contract/upload";
        }
    }

    @GetMapping("/detail/{id}/contract/upload/camera")
    public String getUploadContractCameraPage(
            @PathVariable Long id, Model model) {

        userUtils.getOptionalUser(model);

        model.addAttribute("purchaseOrderId", id);

        return "contract/media_capture";
    }

    @PostMapping("/detail/{id}/contract/upload/camera")
    @ResponseBody
    public ResponseEntity<Map<String, String>> captureImage(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            RedirectAttributes redirectAttributes) {
        Map<String, String> response = new HashMap<>();

        String imageData = payload.get("image");
        if (imageData != null && imageData.contains(",")) {
            try {
                // Xóa tiền tố "data:image/png;base64," nếu có
                String base64Image = imageData.split(",")[1];
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                // Tạo tên file cho ảnh mới nhất
                String fileName = "latest-image.png";
                String uploadDir = "uploads";

                // Tạo thư mục nếu chưa tồn tại
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Lưu đường dẫn của ảnh mới nhất
                latestImagePath = uploadDir + File.separator + fileName;

                // Ghi file
                try (FileOutputStream fos = new FileOutputStream(new File(directory, fileName))) {
                    fos.write(imageBytes);
                    response.put("success", "true");
                    response.put("fileName", fileName);
                    response.put("path", latestImagePath);

                    return ResponseEntity.ok(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
                response.put("success", "false");
                response.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            } catch (IllegalArgumentException e) {
                // Xử lý lỗi khi decode Base64
                response.put("success", "false");
                response.put("error", "Invalid Base64 encoding: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } else {
            response.put("success", "false");
            response.put("error", "Invalid image data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/detail/{id}/contract/upload/camera/latest-image-path")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getLatestImagePath(
            @PathVariable Long id, Model model
    ) {
        Map<String, String> response = new HashMap<>();

        if (latestImagePath != null) {
            response.put("path", latestImagePath);
            response.put("exists", "true");
            return ResponseEntity.ok(response);
        } else {
            response.put("exists", "false");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
