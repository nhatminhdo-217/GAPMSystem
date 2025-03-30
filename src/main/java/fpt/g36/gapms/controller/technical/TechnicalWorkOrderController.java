package fpt.g36.gapms.controller.technical;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.WorkOrder;
import fpt.g36.gapms.services.WorkOrderService;
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

import java.util.Collections;

@Controller
@RequestMapping("/technical")
public class TechnicalWorkOrderController {
    private final WorkOrderService workOrderService;
    private final UserUtils userUtils;

    @Autowired
    public TechnicalWorkOrderController(WorkOrderService workOrderService, UserUtils userUtils) {
        this.workOrderService = workOrderService;
        this.userUtils = userUtils;
    }

    @GetMapping("/view-all-work-order")
    public String viewAllWorkOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search, @RequestParam(required = false) String status, @RequestParam(required = false) String success, @RequestParam(required = false) String error, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Pageable pageable = PageRequest.of(page, size);

            Page<WorkOrder> workOrderPage;
            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    try {
                        WorkOrder workOrder = workOrderService.getWorkOrderById(searchId);
                        workOrderPage = new PageImplWrapper<>(Collections.singletonList(workOrder), pageable, 1);
                    } catch (RuntimeException e) {
                        workOrderPage = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                        model.addAttribute("error", "Không tìm thấy Work Order với ID: " + searchId);
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("error", "Mã Work Order phải là số.");
                    workOrderPage = workOrderService.getAllWorkOrders(pageable);
                }
            } else if (status != null && !status.trim().isEmpty()) {
                try {
                    BaseEnum statusEnum = BaseEnum.valueOf(status.trim());
                    workOrderPage = workOrderService.getWorkOrdersByStatus(statusEnum, pageable);
                    model.addAttribute("selectedStatus", status);
                } catch (IllegalArgumentException e) {
                    model.addAttribute("error", "Trạng thái không hợp lệ: " + status);
                    workOrderPage = workOrderService.getAllWorkOrders(pageable);
                }
            } else {
                workOrderPage = workOrderService.getAllWorkOrders(pageable);
            }

            model.addAttribute("workOrders", workOrderPage.getContent());
            model.addAttribute("workOrderPage", workOrderPage);
            model.addAttribute("statuses", BaseEnum.values());
            model.addAttribute("search", search); // Giữ giá trị search để hiển thị trong input
            return "technical/view-all-work-order";
        }
        return "redirect:/login";
    }

    @GetMapping("/work-order-details/{id}")
    public String viewWorkOrderDetails(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WorkOrder workOrder = workOrderService.getWorkOrderById(id);
                model.addAttribute("workOrder", workOrder);
                return "technical/work-order-details";
            } catch (RuntimeException e) {
                model.addAttribute("error", "Không tìm thấy Work Order với ID: " + id);
                return "redirect:/technical/view-all-work-order"; // Chuyển hướng về danh sách nếu không tìm thấy
            }
        }
        return "redirect:/login";
    }

    private static class PageImplWrapper<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImplWrapper(java.util.List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}


