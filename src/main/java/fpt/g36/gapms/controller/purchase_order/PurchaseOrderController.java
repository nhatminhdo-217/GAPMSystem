package fpt.g36.gapms.controller.purchase_order;

import fpt.g36.gapms.models.dto.contract.ContractDTO;
import fpt.g36.gapms.models.dto.contract.ContractUpdateDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.entities.Contract;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.ContractService;
import fpt.g36.gapms.services.PurchaseOrderService;
import fpt.g36.gapms.utils.UserUtils;
import jakarta.validation.Valid;
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

    public PurchaseOrderController(UserUtils userUtils, PurchaseOrderService purchaseOrderService, ContractService contractService) {
        this.userUtils = userUtils;
        this.purchaseOrderService = purchaseOrderService;
        this.contractService = contractService;
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

        PurchaseOrder po = purchaseOrderService.updatePurchaseOrderStatus(id);

        return "redirect:/purchase-order/detail/" + po.getId();
    }

    @GetMapping("/detail/{purchaseId}/contract/{id}")
    public String getContractPage(
            @PathVariable Long purchaseId,
            @PathVariable String id, Model model) {

        userUtils.getOptionalUser(model);

        Optional<Contract> contract = contractService.findById(id);

        model.addAttribute("contract", contract.get());
        model.addAttribute("purchaseOrderId", purchaseId);

        return "purchase-order/contracts";
    }

    @GetMapping("/detail/contract/{id}/edit")
    public String getEditContractPage(@PathVariable String id, Model model) {

        userUtils.getOptionalUser(model);

        Optional<Contract> contract = contractService.findById(id);
        model.addAttribute("contract", contract.get());

        return "purchase-order/edit_contract";
    }

    @PostMapping("/detail/contract/{id}/edit")
    public String postEditContractPage(
            @PathVariable String id,
            @Valid @ModelAttribute ContractDTO contractDTO,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
            Model model) throws IOException {

        Contract contract = contractService.updateContract(id, contractDTO, file);

        redirectAttributes.addFlashAttribute("message", "Contract updated successfully");

        return "redirect:/purchase-order/detail/contract/" + contract.getId();
    }

    @GetMapping("/detail/{id}/contract/upload")
    public String getUploadContractPage(@PathVariable Long id, Model model) {

        userUtils.getOptionalUser(model);

        model.addAttribute("purchaseOrderId", id);
        model.addAttribute("contractDTO", new ContractDTO());

        return "purchase-order/upload_contract";
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
            return "purchase-order/upload_contract";
        }
        try {
            Contract contract = contractService.createContract(id, contractDTO, file);
            redirectAttributes.addFlashAttribute("success", "Tạo hợp đồng thành công");
            return "redirect:/purchase-order/detail/contract/" + contract.getId();
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Tạo hợp đồng thất bại");
            System.err.println(e.getMessage());
            return "redirect:/purchase-order/detail/" + id + "/contract/upload";
        }
    }
}
