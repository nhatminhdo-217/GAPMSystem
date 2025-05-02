package fpt.g36.gapms.controller.production_manager;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.WorkOrder;
import fpt.g36.gapms.services.WorkOrderService;
import fpt.g36.gapms.utils.NotificationUtils;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/production-manager")
public class SubmittedWorkOrderController {
    private final WorkOrderService workOrderService;
    private final UserUtils userUtils;
    private final NotificationUtils notificationUtils;

    public SubmittedWorkOrderController(WorkOrderService workOrderService, UserUtils userUtils, NotificationUtils notificationUtils) {
        this.workOrderService = workOrderService;
        this.userUtils = userUtils;
        this.notificationUtils = notificationUtils;
    }

    @GetMapping("/view-all-submitted-work-order")
    public String viewAllWorkOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String previousStatus,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Pageable pageable = PageRequest.of(page, size);
            Page<WorkOrder> workOrderPage;

            // Xử lý tìm kiếm theo ID
            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    try {
                        // Tìm WorkOrder theo ID
                        WorkOrder workOrder = workOrderService.getSubmittedWorkOrderById(searchId);
                        // Gán selectedStatus dựa trên trạng thái của Work Order tìm thấy
                        String foundStatus = workOrder.getStatus().name();
                        // Kiểm tra xem trạng thái tìm thấy có nằm trong các trạng thái hợp lệ không
                        if (foundStatus.equals("APPROVED") || foundStatus.equals("WAIT_FOR_APPROVAL")) {
                            workOrderPage = new PageImplWrapper<>(Collections.singletonList(workOrder), pageable, 1);
                            model.addAttribute("selectedStatus", foundStatus);
                            model.addAttribute("previousStatus", status != null ? status : "WAIT_FOR_APPROVAL");
                        } else {
                            workOrderPage = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                            model.addAttribute("error", "Không tìm thấy Kế hoạch sản xuất với ID: " + searchId + " trong các trạng thái hợp lệ.");
                            // Nếu không tìm thấy, quay về tab trước đó
                            String fallbackStatus = (previousStatus != null && !previousStatus.isEmpty()) ? previousStatus : (status != null ? status : "WAIT_FOR_APPROVAL");
                            model.addAttribute("selectedStatus", fallbackStatus);
                            model.addAttribute("previousStatus", fallbackStatus);
                        }
                    } catch (RuntimeException e) {
                        workOrderPage = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                        model.addAttribute("error", "Không tìm thấy Kế hoạch sản xuất với ID: " + searchId);
                        // Nếu không tìm thấy, quay về tab trước đó
                        String fallbackStatus = (previousStatus != null && !previousStatus.isEmpty()) ? previousStatus : (status != null ? status : "WAIT_FOR_APPROVAL");
                        model.addAttribute("selectedStatus", fallbackStatus);
                        model.addAttribute("previousStatus", fallbackStatus);
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("error", "Mã Kế hoạch sản xuất phải là số.");
                    // Lấy danh sách theo trạng thái mặc định hoặc trạng thái trước đó
                    String fallbackStatus = (previousStatus != null && !previousStatus.isEmpty()) ? previousStatus : (status != null ? status : "WAIT_FOR_APPROVAL");
                    BaseEnum tabStatus = fallbackStatus.equals("APPROVED") ? BaseEnum.APPROVED : BaseEnum.WAIT_FOR_APPROVAL;
                    workOrderPage = workOrderService.getSubmittedWorkOrdersByStatus(tabStatus, pageable);
                    model.addAttribute("selectedStatus", fallbackStatus);
                    model.addAttribute("previousStatus", fallbackStatus);
                }
            }
            // Xử lý lọc theo trạng thái
            else if (status != null && !status.trim().isEmpty()) {
                try {
                    BaseEnum statusEnum = BaseEnum.valueOf(status.trim());
                    // Chỉ cho phép trạng thái APPROVED hoặc WAIT_FOR_APPROVAL
                    if (statusEnum == BaseEnum.APPROVED || statusEnum == BaseEnum.WAIT_FOR_APPROVAL) {
                        workOrderPage = workOrderService.getSubmittedWorkOrdersByStatus(statusEnum, pageable);
                        model.addAttribute("selectedStatus", status);
                        model.addAttribute("previousStatus", status);
                    } else {
                        model.addAttribute("error", "Trạng thái không hợp lệ: " + status);
                        workOrderPage = workOrderService.getSubmittedWorkOrdersByStatus(BaseEnum.WAIT_FOR_APPROVAL, pageable);
                        model.addAttribute("selectedStatus", "WAIT_FOR_APPROVAL");
                        model.addAttribute("previousStatus", "WAIT_FOR_APPROVAL");
                    }
                } catch (IllegalArgumentException e) {
                    model.addAttribute("error", "Trạng thái không hợp lệ: " + status);
                    workOrderPage = workOrderService.getSubmittedWorkOrdersByStatus(BaseEnum.WAIT_FOR_APPROVAL, pageable);
                    model.addAttribute("selectedStatus", "WAIT_FOR_APPROVAL");
                    model.addAttribute("previousStatus", "WAIT_FOR_APPROVAL");
                }
            }
            // Mặc định hiển thị danh sách với trạng thái WAIT_FOR_APPROVAL
            else {
                workOrderPage = workOrderService.getSubmittedWorkOrdersByStatus(BaseEnum.WAIT_FOR_APPROVAL, pageable);
                model.addAttribute("selectedStatus", "WAIT_FOR_APPROVAL");
                model.addAttribute("previousStatus", "WAIT_FOR_APPROVAL");
            }

            model.addAttribute("workOrders", workOrderPage.getContent());
            model.addAttribute("workOrderPage", workOrderPage);
            model.addAttribute("search", search);
            return "production-manager/view-all-submitted-work-order";
        }
        System.err.println("User chưa đăng nhập, chuyển hướng đến trang login.");
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
    public String approveWorkOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WorkOrder workOrder = workOrderService.approveWorkOrder(id);
                notificationUtils.sentWorkOrderFromPOToDyeTechnical(workOrder.getId());
                redirectAttributes.addFlashAttribute("success", "Lệnh làm việc đã được phê duyệt!");
                return "redirect:/production-manager/submitted-work-order-details/" + id;
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi đồng ý Work Order: " + e.getMessage());
                return "redirect:/production-manager/submitted-work-order-details/" + id;
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/reject-work-order/{id}")
    public String rejectWorkOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WorkOrder workOrder = workOrderService.rejectWorkOrder(id);
                notificationUtils.rejectWorkOrderPoTechnicalToTechnical(workOrder.getId());
                redirectAttributes.addFlashAttribute("success", "Đã từ chối lệnh làm việc!");
                return "redirect:/production-manager/submitted-work-order-details/" + id;
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi từ chối Work Order: " + e.getMessage());
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
