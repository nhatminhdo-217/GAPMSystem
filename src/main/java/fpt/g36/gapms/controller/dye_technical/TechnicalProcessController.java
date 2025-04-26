package fpt.g36.gapms.controller.dye_technical;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.dye_technical.DyeTypeDTO;
import fpt.g36.gapms.models.dto.dye_technical.TechnologyProcessForm;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.services.TechnologyProcessService;
import fpt.g36.gapms.services.UserService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@RequestMapping("/dye-technical")
public class TechnicalProcessController {
    @Autowired
    private TechnologyProcessService technologyProcessService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private WorkOrderService workOrderService;

    private void validateDyeTypeDTO(DyeTypeDTO dto, String batchType, Model model, Long workOrderId) {
        BigDecimal ratio = dto.getRatio();
        BigDecimal lightPercent = dto.getLightPercent();
        BigDecimal weight = dto.getWeight();

        if (ratio == null || ratio.compareTo(BigDecimal.ZERO) <= 0 || ratio.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            model.addAttribute("error", "Tỷ lệ (" + batchType + ") phải lớn hơn 0 và nhỏ hơn 10000.");
            throw new IllegalArgumentException("Invalid ratio for " + batchType);
        }
        if (lightPercent == null || lightPercent.compareTo(BigDecimal.ZERO) <= 0 || lightPercent.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            model.addAttribute("error", "Phần trăm ánh sáng (" + batchType + ") phải lớn hơn 0 và nhỏ hơn 10000.");
            throw new IllegalArgumentException("Invalid lightPercent for " + batchType);
        }
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0 || weight.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            model.addAttribute("error", "Trọng lượng (" + batchType + ") phải lớn hơn 0 và nhỏ hơn 10000.");
            throw new IllegalArgumentException("Invalid weight for " + batchType);
        }
    }

    private void validateDispergatorN(BigDecimal dispergatorN, String batchType, Model model, Long workOrderId) {
        if (dispergatorN == null || dispergatorN.compareTo(BigDecimal.ZERO) <= 0 || dispergatorN.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            model.addAttribute("error", "DispergatorN (" + batchType + ") phải lớn hơn 0 và nhỏ hơn 10000.");
            throw new IllegalArgumentException("Invalid dispergatorN for " + batchType);
        }
    }

    @GetMapping("/view-all-approved-work-order")
    public String viewAllApprovedWorkOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
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
            User currentUser = optionalUser.get();
            Pageable pageable = PageRequest.of(page, size);

            // Mặc định hiển thị cả hai tab nếu không có tìm kiếm
            Page<WorkOrder> workOrdersWithoutTechProcess = workOrderService.getApprovedWorkOrdersWithoutTechnologyProcess(pageable);
            Page<WorkOrder> workOrdersWithTechProcess = workOrderService.getWorkOrdersWithTechnologyProcessByCreatedBy(pageable, currentUser);
            String activeTab = "without-tech-content";

            // Xử lý tìm kiếm
            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    try {
                        WorkOrder workOrder = workOrderService.getApprovedWorkOrderWithoutTechnologyProcessById(searchId);
                        workOrdersWithoutTechProcess = new PageImplWrapper<>(Collections.singletonList(workOrder), pageable, 1);
                        workOrdersWithTechProcess = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                        activeTab = "without-tech-content";
                    } catch (RuntimeException e) {
                        try {
                            WorkOrder workOrder = workOrderService.getWorkOrderWithTechnologyProcessByIdAndCreatedBy(searchId, currentUser);
                            workOrdersWithTechProcess = new PageImplWrapper<>(Collections.singletonList(workOrder), pageable, 1);
                            workOrdersWithoutTechProcess = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                            activeTab = "with-tech-content";
                        } catch (RuntimeException ex) {
                            workOrdersWithoutTechProcess = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                            workOrdersWithTechProcess = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                            model.addAttribute("errorWithoutTech", "Không tìm thấy Work Order với ID: " + searchId);
                            activeTab = "without-tech-content";
                        }
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("errorWithoutTech", "Mã Work Order phải là số.");
                }
            }

            model.addAttribute("workOrdersWithoutTechProcess", workOrdersWithoutTechProcess.getContent());
            model.addAttribute("workOrdersWithoutTechProcessPage", workOrdersWithoutTechProcess);
            model.addAttribute("workOrdersWithTechProcess", workOrdersWithTechProcess.getContent());
            model.addAttribute("workOrdersWithTechProcessPage", workOrdersWithTechProcess);
            model.addAttribute("search", search);
            model.addAttribute("activeTab", activeTab); // Truyền tab cần kích hoạt

            return "dye-technical/view-all-approved-work-order";
        }
        System.err.println("User chưa đăng nhập, chuyển hướng đến trang login.");
        return "redirect:/login";
    }

    @GetMapping("/work-order-details/{id}")
    public String viewWorkOrderDetails(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WorkOrder workOrder = workOrderService.getWorkOrderById(id);
                System.err.println("WorkOrder ID: " + id + " found.");

                Map<Long, Boolean> hasTechProcessMap = new HashMap<>();
                List<WorkOrderDetail> details = workOrder.getWorkOrderDetails();
                if (details == null || details.isEmpty()) {
                    System.err.println("No WorkOrderDetails found for WorkOrder ID: " + id);
                    model.addAttribute("error", "Không tìm thấy chi tiết WorkOrder cho ID: " + id);
                    model.addAttribute("workOrder", workOrder);
                    model.addAttribute("hasTechProcessMap", hasTechProcessMap);
                    model.addAttribute("canSubmit", false);
                    model.addAttribute("allTechProcessesApproved", false);
                    return "dye-technical/work-order-details";
                }

                for (WorkOrderDetail detail : details) {
                    boolean hasTechProcess = false;
                    if (detail.getDyeStage() != null && detail.getDyeStage().getDyebatches() != null && !detail.getDyeStage().getDyebatches().isEmpty()) {
                        List<DyeBatch> dyeBatches = detail.getDyeStage().getDyebatches();
                        System.err.println("WorkOrderDetail ID: " + detail.getId() + " has " + dyeBatches.size() + " dye batches.");

                        // Kiểm tra mẻ đầu
                        DyeBatch firstBatch = dyeBatches.get(0);
                        boolean firstBatchHasTechProcess = firstBatch.getTechnologyProcess() != null && firstBatch.getTechnologyProcess().getDyeTypes() != null && !firstBatch.getTechnologyProcess().getDyeTypes().isEmpty();

                        // Kiểm tra mẻ cuối (nếu có nhiều hơn 1 mẻ)
                        boolean lastBatchHasTechProcess = true;
                        if (dyeBatches.size() > 1) {
                            DyeBatch lastBatch = dyeBatches.get(dyeBatches.size() - 1);
                            lastBatchHasTechProcess = lastBatch.getTechnologyProcess() != null && lastBatch.getTechnologyProcess().getDyeTypes() != null && !lastBatch.getTechnologyProcess().getDyeTypes().isEmpty();
                        }

                        // Chỉ đặt hasTechProcess = true nếu cả mẻ đầu và mẻ cuối (nếu có) đều có TechnologyProcess hợp lệ
                        hasTechProcess = firstBatchHasTechProcess && lastBatchHasTechProcess;
                    } else {
                        System.err.println("WorkOrderDetail ID: " + detail.getId() + " has no DyeStage or empty dyebatches.");
                    }
                    hasTechProcessMap.put(detail.getId(), hasTechProcess);
                }

                // Kiểm tra xem có thể submit hay không
                boolean canSubmit = true;
                for (WorkOrderDetail detail : details) {
                    if (detail.getDyeStage() == null || detail.getDyeStage().getDyebatches() == null || detail.getDyeStage().getDyebatches().isEmpty()) {
                        System.err.println("WorkOrderDetail ID: " + detail.getId() + " missing DyeStage or dyebatches.");
                        canSubmit = false;
                        break;
                    }
                    for (DyeBatch batch : detail.getDyeStage().getDyebatches()) {
                        TechnologyProcess techProcess = batch.getTechnologyProcess();
                        if (techProcess == null || techProcess.getDyeTypes() == null || techProcess.getDyeTypes().isEmpty()) {
                            System.err.println("DyeBatch ID: " + batch.getId() + " missing TechnologyProcess or dyeTypes.");
                            canSubmit = false;
                            break;
                        }
                    }
                    if (!canSubmit) break;
                }

                // Kiểm tra xem tất cả TechnologyProcess có trạng thái APPROVED không
                boolean allTechProcessesApproved = details.stream()
                        .flatMap(detail -> detail.getDyeStage() != null && detail.getDyeStage().getDyebatches() != null
                                ? detail.getDyeStage().getDyebatches().stream()
                                : Stream.empty())
                        .filter(batch -> batch.getTechnologyProcess() != null)
                        .allMatch(batch -> "APPROVED".equals(batch.getTechnologyProcess().getStatus().name()));
                System.err.println("All TechnologyProcesses approved: " + allTechProcessesApproved);

                model.addAttribute("workOrder", workOrder);
                model.addAttribute("hasTechProcessMap", hasTechProcessMap);
                model.addAttribute("canSubmit", canSubmit);
                model.addAttribute("allTechProcessesApproved", allTechProcessesApproved);
                return "dye-technical/work-order-details";
            } catch (RuntimeException e) {
                System.err.println("Error fetching WorkOrder ID: " + id + " - " + e.getMessage());
                model.addAttribute("error", "Không tìm thấy Work Order với ID: " + id);
                return "redirect:/dye-technical/view-all-approved-work-order";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/create-technology-process")
    public String createTechnologyProcess(@ModelAttribute TechnologyProcessForm form,
                                          Model model, Principal principal,
                                          RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        System.out.println("Bắt đầu createTechnologyProcess: workOrderId=" + form.getWorkOrderId() + ", workOrderDetailId=" + form.getWorkOrderDetailId());
        System.out.println("dyeTypesForFirstBatches: " + form.getDyeTypesForFirstBatches());
        System.out.println("dyeTypesForLastBatch: " + form.getDyeTypesForLastBatch());

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                String emailOrPhone = principal.getName();
                Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
                if (!optionalUser.isPresent()) {
                    System.err.println("Không tìm thấy User với email/phone: " + emailOrPhone + ", chuyển hướng đến trang login.");
                    return "redirect:/login";
                }
                User currentUser = optionalUser.get();

                if (form.getDyeTypesForFirstBatches() == null || form.getDyeTypesForFirstBatches().isEmpty()) {
                    System.err.println("Lỗi ở dyeTypesForFirstBatches: " + form.getDyeTypesForFirstBatches());
                    redirectAttributes.addFlashAttribute("error", "Dye Types cho các mẻ đầu không được để trống.");
                    return "redirect:/dye-technical/work-order-details/" + form.getWorkOrderId();
                }

                WorkOrder workOrder = workOrderService.getWorkOrderById(form.getWorkOrderId());
                WorkOrderDetail workOrderDetail = workOrder.getWorkOrderDetails().stream()
                        .filter(detail -> detail.getId().equals(form.getWorkOrderDetailId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy WorkOrderDetail với ID: " + form.getWorkOrderDetailId()));

                int dyeBatchCount = workOrderDetail.getDyeStage().getDyebatches() != null
                        ? workOrderDetail.getDyeStage().getDyebatches().size()
                        : 0;
                System.out.println("Số lượng dye batches: " + dyeBatchCount);

                if (dyeBatchCount > 1) {
                    if (form.getDyeTypesForLastBatch() == null || form.getDyeTypesForLastBatch().isEmpty()) {
                        redirectAttributes.addFlashAttribute("error", "Dye Types cho mẻ cuối không được để trống khi có nhiều hơn 1 dye batch.");
                        return "redirect:/dye-technical/work-order-details/" + form.getWorkOrderId();
                    }
                }

                // Validate dyeTypesForFirstBatches
                for (DyeTypeDTO dto : form.getDyeTypesForFirstBatches()) {
                    validateDyeTypeDTO(dto, "mẻ đầu", model, form.getWorkOrderId());
                }
                validateDispergatorN(form.getDispergatorNForFirstBatches(), "mẻ đầu", model, form.getWorkOrderId());

                // Validate dyeTypesForLastBatch if present
                if (form.getDyeTypesForLastBatch() != null && !form.getDyeTypesForLastBatch().isEmpty()) {
                    for (DyeTypeDTO dto : form.getDyeTypesForLastBatch()) {
                        validateDyeTypeDTO(dto, "mẻ cuối", model, form.getWorkOrderId());
                    }
                    validateDispergatorN(form.getDispergatorNForLastBatch(), "mẻ cuối", model, form.getWorkOrderId());
                }

                technologyProcessService.createTechnologyProcess(
                        currentUser,
                        form.getWorkOrderId(),
                        form.getWorkOrderDetailId(),
                        form.getDyeTypesForFirstBatches(),
                        form.getDyeTypesForLastBatch() != null ? form.getDyeTypesForLastBatch() : Collections.emptyList(),
                        form.getDispergatorNForFirstBatches(),
                        form.getDispergatorNForLastBatch()
                );

                redirectAttributes.addFlashAttribute("success", "Tạo Hành Trình Công Nghệ thành công!");
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/dye-technical/work-order-details/" + form.getWorkOrderId();
            } catch (Exception e) {
                System.err.println("Lỗi xảy ra: " + e.getMessage());
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo Hành Trình Công Nghệ: " + e.getMessage());
            }
            return "redirect:/dye-technical/work-order-details/" + form.getWorkOrderId();
        }
        return "redirect:/login";
    }

    @PostMapping("/submit-technology-processes/{workOrderId}")
    public String submitTechnologyProcesses(@PathVariable Long workOrderId,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                technologyProcessService.submitTechnologyProcesses(workOrderId);
                redirectAttributes.addFlashAttribute("success",
                        "Đã hoàn tất hành trình công nghệ");
            } catch (Exception e) {
                System.err.println("Lỗi khi submit Technology Processes: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error",
                        "Lỗi khi submit Technology Processes: " + e.getMessage());
            }
            return "redirect:/dye-technical/work-order-details/" + workOrderId;
        }
        System.err.println("User chưa đăng nhập, chuyển hướng đến trang login.");
        return "redirect:/login";
    }

    @GetMapping("technology-process-details/{id}")
    public String technologyProcessDetails(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                TechnologyProcess technologyProcess = technologyProcessService.getTechnologyProcessById(id);
                model.addAttribute("technologyProcess", technologyProcess);
                return "dye-technical/technology-process-details";
            } catch (RuntimeException e) {
                model.addAttribute("error", "Không tìm thấy Work Order với ID: " + id);
                return "redirect:/dye-technical/view-all-technology-process";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/view-all-technology-process")
    public String viewAllTechnologyProcesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String previousStatus,
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
            User currentUser = optionalUser.get();
            System.err.println("User đang đăng nhập: " + currentUser.getUsername());

            Pageable pageable = PageRequest.of(page, size);
            Page<TechnologyProcess> technologyProcessPage;

            // Xử lý tìm kiếm theo ID
            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long searchId = Long.parseLong(search.trim());
                    try {
                        TechnologyProcess technologyProcess = technologyProcessService.getTechnologyProcessByIdAndCreatedBy(searchId, currentUser);
                        String foundStatus = technologyProcess.getStatus().name();
                        model.addAttribute("selectedStatus", foundStatus);
                        technologyProcessPage = new PageImplWrapper<>(Collections.singletonList(technologyProcess), pageable, 1);
                        model.addAttribute("previousStatus", status != null ? status : "DRAFT");
                    } catch (RuntimeException e) {
                        technologyProcessPage = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                        model.addAttribute("error", "Không tìm thấy Technology Process với ID: " + searchId + " cho user: " + currentUser.getUsername());
                        String fallbackStatus = (previousStatus != null && !previousStatus.isEmpty()) ? previousStatus : (status != null ? status : "DRAFT");
                        model.addAttribute("selectedStatus", fallbackStatus);
                        model.addAttribute("previousStatus", fallbackStatus);
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("error", "Mã Technology Process phải là số.");
                    technologyProcessPage = technologyProcessService.getAllTechnologyProcessesByCreatedBy(pageable, currentUser);
                    String fallbackStatus = (previousStatus != null && !previousStatus.isEmpty()) ? previousStatus : (status != null ? status : "DRAFT");
                    model.addAttribute("selectedStatus", fallbackStatus);
                    model.addAttribute("previousStatus", fallbackStatus);
                }
            }
            // Xử lý lọc theo trạng thái
            else if (status != null && !status.trim().isEmpty()) {
                try {
                    BaseEnum statusEnum = BaseEnum.valueOf(status.trim());
                    technologyProcessPage = technologyProcessService.getTechnologyProcessesByStatusAndCreatedBy(statusEnum, pageable, currentUser);
                    model.addAttribute("selectedStatus", status);
                    model.addAttribute("previousStatus", status);
                } catch (IllegalArgumentException e) {
                    model.addAttribute("error", "Trạng thái không hợp lệ: " + status);
                    technologyProcessPage = technologyProcessService.getAllTechnologyProcessesByCreatedBy(pageable, currentUser);
                    model.addAttribute("selectedStatus", "DRAFT");
                    model.addAttribute("previousStatus", "DRAFT");
                }
            }
            // Trường hợp mặc định: hiển thị tất cả TechnologyProcess của user
            else {
                technologyProcessPage = technologyProcessService.getAllTechnologyProcessesByCreatedBy(pageable, currentUser);
                model.addAttribute("selectedStatus", "DRAFT");
                model.addAttribute("previousStatus", "DRAFT");
            }

            model.addAttribute("technologyProcesses", technologyProcessPage.getContent());
            model.addAttribute("technologyProcessPage", technologyProcessPage);
            model.addAttribute("search", search);
  
            return "dye-technical/view-all-technology-process";
        }
        System.err.println("User chưa đăng nhập, chuyển hướng đến trang login.");
        return "redirect:/login";
    }

    @PostMapping("/update-technology-process")
    public String updateTechnologyProcess(@ModelAttribute TechnologyProcessForm form,
                                          Model model, Principal principal,
                                          RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        System.out.println("Bắt đầu updateTechnologyProcess: workOrderId=" + form.getWorkOrderId() + ", workOrderDetailId=" + form.getWorkOrderDetailId());
        System.out.println("dyeTypesForFirstBatches: " + form.getDyeTypesForFirstBatches());
        System.out.println("dyeTypesForLastBatch: " + form.getDyeTypesForLastBatch());

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                String emailOrPhone = principal.getName();
                Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
                if (!optionalUser.isPresent()) {
                    System.err.println("Không tìm thấy User với email/phone: " + emailOrPhone + ", chuyển hướng đến trang login.");
                    return "redirect:/login";
                }
                User currentUser = optionalUser.get();

                // Validate dyeTypesForFirstBatches
                if (form.getDyeTypesForFirstBatches() == null || form.getDyeTypesForFirstBatches().isEmpty()) {
                    System.err.println("Lỗi ở dyeTypesForFirstBatches: " + form.getDyeTypesForFirstBatches());
                    redirectAttributes.addFlashAttribute("error", "Dye Types cho các mẻ đầu không được để trống.");
                    return "redirect:/dye-technical/work-order-details/" + form.getWorkOrderId();
                }

                // Kiểm tra WorkOrder và WorkOrderDetail
                WorkOrder workOrder = workOrderService.getWorkOrderById(form.getWorkOrderId());
                WorkOrderDetail workOrderDetail = workOrder.getWorkOrderDetails().stream()
                        .filter(detail -> detail.getId().equals(form.getWorkOrderDetailId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy WorkOrderDetail với ID: " + form.getWorkOrderDetailId()));

                int dyeBatchCount = workOrderDetail.getDyeStage().getDyebatches() != null
                        ? workOrderDetail.getDyeStage().getDyebatches().size()
                        : 0;
                System.out.println("Số lượng dye batches: " + dyeBatchCount);

                // Validate dyeTypesForLastBatch nếu có nhiều hơn 1 mẻ
                if (dyeBatchCount > 1) {
                    if (form.getDyeTypesForLastBatch() == null || form.getDyeTypesForLastBatch().isEmpty()) {
                        redirectAttributes.addFlashAttribute("error", "Dye Types cho mẻ cuối không được để trống khi có nhiều hơn 1 dye batch.");
                        return "redirect:/dye-technical/work-order-details/" + form.getWorkOrderId();
                    }
                }

                // Validate dyeTypesForFirstBatches
                for (DyeTypeDTO dto : form.getDyeTypesForFirstBatches()) {
                    validateDyeTypeDTO(dto, "mẻ đầu", model, form.getWorkOrderId());
                }
                validateDispergatorN(form.getDispergatorNForFirstBatches(), "mẻ đầu", model, form.getWorkOrderId());

                // Validate dyeTypesForLastBatch nếu có
                if (form.getDyeTypesForLastBatch() != null && !form.getDyeTypesForLastBatch().isEmpty()) {
                    for (DyeTypeDTO dto : form.getDyeTypesForLastBatch()) {
                        validateDyeTypeDTO(dto, "mẻ cuối", model, form.getWorkOrderId());
                    }
                    validateDispergatorN(form.getDispergatorNForLastBatch(), "mẻ cuối", model, form.getWorkOrderId());
                }

                // Gọi service để cập nhật TechnologyProcess
                technologyProcessService.updateTechnologyProcess(
                        form.getWorkOrderId(),
                        form.getWorkOrderDetailId(),
                        form.getDyeTypesForFirstBatches(),
                        form.getDyeTypesForLastBatch() != null ? form.getDyeTypesForLastBatch() : Collections.emptyList(),
                        form.getDispergatorNForFirstBatches(),
                        form.getDispergatorNForLastBatch(),
                        currentUser
                );

                redirectAttributes.addFlashAttribute("success", "Cập nhật Hành Trình Công Nghệ thành công!");
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/dye-technical/work-order-details/" + form.getWorkOrderId();
            } catch (Exception e) {
                System.err.println("Lỗi xảy ra: " + e.getMessage());
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật Hành Trình Công Nghệ: " + e.getMessage());
            }
            return "redirect:/dye-technical/work-order-details/" + form.getWorkOrderId();
        }
        return "redirect:/login";
    }

    private static class PageImplWrapper<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImplWrapper(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}

