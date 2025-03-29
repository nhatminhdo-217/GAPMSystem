package fpt.g36.gapms.controller.production_order;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDetailDTO;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.ProductionOrderService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/production-order")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;
    private final UserUtils userUtils;

    public ProductionOrderController(ProductionOrderService productionOrderService, UserUtils userUtils) {
        this.productionOrderService = productionOrderService;
        this.userUtils = userUtils;
    }

    @GetMapping("/list")
    public String listProductionOrder(Model model) {
        return findPaginated(1, "createAt", "asc", model);
    }

    @GetMapping("/list/page/{page}")
    public String findPaginated(
            @PathVariable("page") Integer page,
            @RequestParam("sortField") String sortField,
            @RequestParam("sortDir") String sortDir,
            Model model
    ) {

        userUtils.getOptionalUser(model);

        int pageSize = 5;

        User currUser = userUtils.getOptionalUserInfo(model);

        Page<ProductionOrderDTO> pageData = productionOrderService.findPaginatedByRoles(page, pageSize, sortField, sortDir, currUser);

        List<ProductionOrderDTO> productionOrderList = pageData.getContent();

        model.addAttribute("productionOrderList", productionOrderList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("totalItems", pageData.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "production_order/list_production_order";
    }

    @GetMapping("/detail/{id}")
    public String detailProductionOrder(@PathVariable("id") Long id, Model model) {

        userUtils.getOptionalUser(model);

        ProductionOrderDTO productionOrder = productionOrderService.findById(id);
        List<ProductionOrderDetailDTO> productionOrderDetailList = productionOrderService.findDetailByProductionOrderId(id);

        model.addAttribute("productionOrder", productionOrder);
        model.addAttribute("productionOrderDetailList", productionOrderDetailList);
        model.addAttribute("productionOrderDetail", new ProductionOrderDetailDTO());


        return "production_order/detail_production_order";
    }

    @PostMapping("/detail/{id}")
    public String createProductionOrderDetail(@PathVariable("id") Long id,
                                              RedirectAttributes redirectAttributes,
                                              Model model) {

        User currUser = userUtils.getOptionalUserInfo();

        BaseEnum status = productionOrderService.getStatusByProductionOrder(id);

        if (status.equals(BaseEnum.NOT_APPROVED)) {
            ProductionOrder po = productionOrderService.updateStatus(id, currUser);
            redirectAttributes.addFlashAttribute("success", "Cập nhật lệnh sản xuất thành công");
            return "redirect:/production-order/detail/" + po.getId();
        }else {
            if (status.equals(BaseEnum.WAIT_FOR_APPROVAL)) {
                redirectAttributes.addFlashAttribute("success", "Lệnh sản xuất đã được phê duyệt");
            } else {
                redirectAttributes.addFlashAttribute("success", "Cập nhật lệnh sản xuất thành công");
            }
            ProductionOrder po = productionOrderService.updateStatus(id, currUser);
            return "redirect:/production-order/detail/" + po.getId();
        }
    }

    @PostMapping("/detail/{id}/cancel")
    public String cancelProductionOrderDetail(@PathVariable("id") Long id,
                                              RedirectAttributes redirectAttributes) {

        boolean isCancelled = productionOrderService.cancelProductionOrder(id);

        if (isCancelled) {
            redirectAttributes.addFlashAttribute("success", "Hủy lệnh sản xuất thành công");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy lệnh sản xuất");
        }
        return "redirect:/production-order/detail/" + id;
    }

    @PostMapping("/detail/update/{id}")
    public String updateProductionOrderDetail(@PathVariable("id") Long id,
                                              @ModelAttribute("productionOrderDetail") ProductionOrderDetailDTO productionOrderDetailDTO,
                                              Model model) {

        ProductionOrderDetailDTO dto = productionOrderService.updateProductionOrderDetail(productionOrderDetailDTO);

        return "redirect:/production-order/detail/" + id;
    }


}
