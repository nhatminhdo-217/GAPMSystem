package fpt.g36.gapms.controller.purchase_order;

import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.services.PurchaseOrderService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/purchase-order")
public class PurchaseOrderController {

    private final UserUtils userUtils;
    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(UserUtils userUtils, PurchaseOrderService purchaseOrderService) {
        this.userUtils = userUtils;
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping("/detail/{id}")
    public String getPurchaseOrderDetailPage(@PathVariable Long id, Model model) {

        userUtils.getOptionalUser(model);

        Optional<PurchaseOrderInfoDTO> data = purchaseOrderService.getPurchaseOrderInfoDTOById(id);
        List<PurchaseOrderItemsDTO> items = purchaseOrderService.getPurchaseOrderItemsDTOById(id);

        PurchaseOrderInfoDTO purchaseOrderInfoDTO = data.get();

        model.addAttribute("orderInfo", purchaseOrderInfoDTO);
        model.addAttribute("items", items);

        return "purchase-order/purchase_order_detail";
    }

    @PostMapping("/detail/{id}")
    public String postPurchaseOrderDetailPage(@PathVariable Long id) {

        PurchaseOrder po = purchaseOrderService.updatePurchaseOrderStatus(id);

        return "redirect:/purchase-order/detail/" + po.getId();
    }
}
