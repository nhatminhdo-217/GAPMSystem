package fpt.g36.gapms.controller.technical;

import fpt.g36.gapms.models.dto.technical.TechnicalProductionOrderDTO;
import fpt.g36.gapms.models.dto.technical.TechnicalProductionOrderDetailsDTO;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.ProductionOrderService;
import fpt.g36.gapms.services.UserService;
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

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/technical")
public class TechnicalProductionOrderController {
    private final ProductionOrderService productionOrderService;
    private final UserService userService;
    private final UserUtils userUtils;

    @Autowired
    public TechnicalProductionOrderController(ProductionOrderService productionOrderService, UserService userService, UserUtils userUtils) {
        this.productionOrderService = productionOrderService;
        this.userService = userService;
        this.userUtils = userUtils;
    }


    @GetMapping("/view-approved-production-order")
    public String viewApprovedProductionOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "without-work-order-content") String activeTab,
            @RequestParam(required = false) String previousTab,
            Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = principal.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (!optionalUser.isPresent()) {
                System.err.println("Không tìm thấy User với email/phone: " + emailOrPhone + ", chuyển hướng đến trang login.");
                return "redirect:/login";
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<TechnicalProductionOrderDTO> productionOrdersWithoutWorkOrder;
            Page<TechnicalProductionOrderDTO> productionOrdersWithWorkOrder;

            // Xử lý tìm kiếm theo ID
            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    try {
                        TechnicalProductionOrderDTO productionOrderDTO = productionOrderService.getTechnicalProductionOrderById(searchId);
                        if (!productionOrderDTO.isHasWorkOrder()) {
                            productionOrdersWithoutWorkOrder = new PageImplWrapper<>(
                                    Collections.singletonList(productionOrderDTO), pageable, 1);
                            productionOrdersWithWorkOrder = productionOrderService.getApprovedProductionOrdersWithWorkOrder(pageable);
                            activeTab = "without-work-order-content";
                        } else {
                            productionOrdersWithWorkOrder = new PageImplWrapper<>(
                                    Collections.singletonList(productionOrderDTO), pageable, 1);
                            productionOrdersWithoutWorkOrder = productionOrderService.getApprovedProductionOrdersWithoutWorkOrder(pageable);
                            activeTab = "with-work-order-content";
                        }
                        model.addAttribute("previousTab", activeTab);
                    } catch (RuntimeException e) {
                        productionOrdersWithoutWorkOrder = productionOrderService.getApprovedProductionOrdersWithoutWorkOrder(pageable);
                        productionOrdersWithWorkOrder = productionOrderService.getApprovedProductionOrdersWithWorkOrder(pageable);
                        model.addAttribute("error", "Không tìm thấy Production Order với ID: " + searchId);
                        activeTab = (previousTab != null && !previousTab.isEmpty()) ? previousTab : "without-work-order-content";
                        model.addAttribute("previousTab", activeTab);
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("error", "Mã Production Order phải là số.");
                    productionOrdersWithoutWorkOrder = productionOrderService.getApprovedProductionOrdersWithoutWorkOrder(pageable);
                    productionOrdersWithWorkOrder = productionOrderService.getApprovedProductionOrdersWithWorkOrder(pageable);
                    activeTab = (previousTab != null && !previousTab.isEmpty()) ? previousTab : "without-work-order-content";
                    model.addAttribute("previousTab", activeTab);
                }
            } else {
                productionOrdersWithoutWorkOrder = productionOrderService.getApprovedProductionOrdersWithoutWorkOrder(pageable);
                productionOrdersWithWorkOrder = productionOrderService.getApprovedProductionOrdersWithWorkOrder(pageable);
                model.addAttribute("previousTab", activeTab);
            }

            model.addAttribute("productionOrdersWithoutWorkOrder", productionOrdersWithoutWorkOrder.getContent());
            model.addAttribute("productionOrdersWithoutWorkOrderPage", productionOrdersWithoutWorkOrder);
            model.addAttribute("productionOrdersWithWorkOrder", productionOrdersWithWorkOrder.getContent());
            model.addAttribute("productionOrdersWithWorkOrderPage", productionOrdersWithWorkOrder);
            model.addAttribute("search", search);
            model.addAttribute("activeTab", activeTab);

            return "technical/view-approved-production-order";
        }
        System.err.println("User chưa đăng nhập, chuyển hướng đến trang login.");
        return "redirect:/login";
    }

    @GetMapping("/production-order-details/{id}")
    public String viewProductionOrderDetails(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                TechnicalProductionOrderDetailsDTO productionOrder = productionOrderService.getProductionOrderDetails(id);
                model.addAttribute("productionOrder", productionOrder);
                return "technical/production-order-details";
            } catch (RuntimeException e) {
                model.addAttribute("error", "Không tìm thấy Production Order với ID: " + id);
                return "redirect:/technical/view-approved-production-order";
            }
        }
        return "redirect:/login";
    }

    private static class PageImplWrapper<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImplWrapper(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}
