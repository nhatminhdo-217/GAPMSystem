package fpt.g36.gapms.controller.production_manager;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.WorkOrder;
import fpt.g36.gapms.services.WorkOrderService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/production-manager")
public class SubmittedWorkOrderController {
    private final WorkOrderService workOrderService;
    private final UserUtils userUtils;

    public SubmittedWorkOrderController(WorkOrderService workOrderService, UserUtils userUtils) {
        this.workOrderService = workOrderService;
        this.userUtils = userUtils;
    }

    @GetMapping("/view-all-submitted-work-order")
    public String viewAllWorkOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Pageable pageable = PageRequest.of(page, size);
            Page<WorkOrder> workOrderPage;

            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    try {
                        WorkOrder workOrder = workOrderService.getSubmittedWorkOrderById(searchId);
                        workOrderPage = new SubmittedWorkOrderController.PageImplWrapper<>(Collections.singletonList(workOrder), pageable, 1);
                    } catch (RuntimeException e) {
                        workOrderPage = new SubmittedWorkOrderController.PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                        model.addAttribute("error", "Không tìm thấy Work Order với ID: " + searchId);
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("error", "Mã Work Order phải là số.");
                    workOrderPage = workOrderService.getAllSubmittedWorkOrders(pageable);
                }
            } else if (status != null && !status.trim().isEmpty()) {
                try {
                    BaseEnum statusEnum = BaseEnum.valueOf(status.trim());
                    workOrderPage = workOrderService.getSubmittedWorkOrdersByStatus(statusEnum, pageable);
                    model.addAttribute("selectedStatus", status);
                } catch (IllegalArgumentException e) {
                    model.addAttribute("error", "Trạng thái không hợp lệ: " + status);
                    workOrderPage = workOrderService.getAllSubmittedWorkOrders(pageable);
                }
            } else {
                workOrderPage = workOrderService.getAllSubmittedWorkOrders(pageable);
            }

            model.addAttribute("workOrders", workOrderPage.getContent());
            model.addAttribute("workOrderPage", workOrderPage);
            model.addAttribute("statuses", BaseEnum.values());
            model.addAttribute("search", search);
            return "production-manager/view-all-submitted-work-order";
        }
        return "redirect:/login";
    }

    @GetMapping("/submitted-work-order-details/{id}")
    public String viewWorkOrderDetails(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WorkOrder workOrder = workOrderService.getWorkOrderById(id);
                model.addAttribute("workOrder", workOrder);
                return "production-manager/submitted-work-order-details";
            } catch (RuntimeException e) {
                model.addAttribute("error", "Không tìm thấy Work Order với ID: " + id);
                return "redirect:/production-manager/view-all-submitted-work-order";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/approve-work-order/{id}")
    public String approveWorkOrder(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WorkOrder workOrder = workOrderService.approveWorkOrder(id);
                model.addAttribute("success", "Work Order đã được đồng ý thành công!");
                model.addAttribute("workOrder", workOrder);
                return "redirect:/production-manager/submitted-work-order-details/" + id;
            } catch (RuntimeException e) {
                WorkOrder workOrder = workOrderService.getWorkOrderById(id);
                model.addAttribute("workOrder", workOrder);
                model.addAttribute("error", "Lỗi khi đồng ý Work Order: " + e.getMessage());
                return "redirect:/production-manager/submitted-work-order-details/" + id;
            }
        }
        return "redirect:/login";
    }

    // Endpoint để xử lý Từ Chối Work Order
    @PostMapping("/reject-work-order/{id}")
    public String rejectWorkOrder(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WorkOrder workOrder = workOrderService.rejectWorkOrder(id);
                model.addAttribute("workOrder", workOrder);
                model.addAttribute("success", "Work Order đã được từ chối thành công!");
                return "redirect:/production-manager/submitted-work-order-details/" + id;
            } catch (RuntimeException e) {
                WorkOrder workOrder = workOrderService.getWorkOrderById(id);
                model.addAttribute("workOrder", workOrder);
                model.addAttribute("error", "Lỗi khi từ chối Work Order: " + e.getMessage());
                return "redirect:/production-manager/submitted-work-order-details/" + id;
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
