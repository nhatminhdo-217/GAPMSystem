package fpt.g36.gapms.controller.technical;

import fpt.g36.gapms.models.dto.technical.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.technical.ProductionOrderDetailsDTO;
import fpt.g36.gapms.services.ProductionOrderService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/technical")
public class ProductionOrderController {
    private final ProductionOrderService productionOrderService;
    private final UserUtils userUtils;

    @Autowired
    public ProductionOrderController(ProductionOrderService productionOrderService, UserUtils userUtils) {
        this.productionOrderService = productionOrderService;
        this.userUtils = userUtils;
    }


    @GetMapping("/view-approved-production-order")
    public String viewApprovedProductionOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Pageable pageable = PageRequest.of(page, size);
            Page<ProductionOrderDTO> productionOrderPage = productionOrderService.getApprovedProductionOrders(pageable);
            model.addAttribute("productionOrders", productionOrderPage.getContent());
            model.addAttribute("productionOrderPage", productionOrderPage);
            return "technical/view-approved-production-order";
        }
        return "redirect:/login";
    }

    @GetMapping("/production-order-details/{id}")
    public String viewProductionOrderDetails(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                ProductionOrderDetailsDTO productionOrder = productionOrderService.getProductionOrderDetails(id);
                model.addAttribute("productionOrder", productionOrder);
                return "technical/production-order-details";
            } catch (RuntimeException e) {
                model.addAttribute("error", "Không tìm thấy Production Order với ID: " + id);
                return "redirect:/technical/view-approved-production-order";
            }
        }
        return "redirect:/login";
    }
}
