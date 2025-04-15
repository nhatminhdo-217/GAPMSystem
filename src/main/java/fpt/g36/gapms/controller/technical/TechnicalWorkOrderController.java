package fpt.g36.gapms.controller.technical;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.dto.technical.CreateWorkOrderForm;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.services.MachineService;
import fpt.g36.gapms.services.ProductionOrderService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.services.WorkOrderService;
import fpt.g36.gapms.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/technical")
public class TechnicalWorkOrderController {
    private final WorkOrderService workOrderService;
    private final UserUtils userUtils;
    private final ProductionOrderService productionOrderService;
    private final MachineService machineService;
    private final UserService userService;

    @Autowired
    public TechnicalWorkOrderController(WorkOrderService workOrderService, UserUtils userUtils, ProductionOrderService productionOrderService, MachineService machineService, UserService userService) {
        this.workOrderService = workOrderService;
        this.userUtils = userUtils;
        this.productionOrderService = productionOrderService;
        this.machineService = machineService;
        this.userService = userService;
    }

    @GetMapping("/view-all-work-order")
    public String viewAllWorkOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search, @RequestParam(required = false) String status, Model model) {
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
            model.addAttribute("search", search);
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
                return "redirect:/technical/view-all-work-order";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/create-work-order")
    public String showCreateWorkOrderForm(@RequestParam("productionOrderId") Long productionOrderId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                if (productionOrderId == null) {
                    model.addAttribute("error", "Production Order ID không hợp lệ.");
                    return "redirect:/technical/view-all-work-order";
                }

                ProductionOrder productionOrder = productionOrderService.getProductionOrderById(productionOrderId);
                if (productionOrder == null) {
                    model.addAttribute("error", "Production Order không tồn tại");
                    return "redirect:/technical/view-all-work-order";
                }

                LocalDateTime plannedStartAt = LocalDateTime.now().plusHours(2);
                LocalDateTime plannedEndAt = productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate().minusDays(1).atTime(LocalTime.MAX);

                List<DyeMachine> freeDyeMachines = machineService.findFreeDyeMachines(plannedStartAt, plannedEndAt);
                List<WindingMachine> freeWindingMachines = machineService.findFreeWindingMachines(plannedStartAt, plannedEndAt);
                List<DyeMachine> queuedDyeMachines = machineService.findQueuedDyeMachinesForProductionOrder(productionOrder, plannedStartAt, plannedEndAt);
                List<WindingMachine> queuedWindingMachines = machineService.findQueuedWindingMachinesForProductionOrder(productionOrder, plannedStartAt, plannedEndAt);

                if (freeDyeMachines.isEmpty() && queuedDyeMachines.isEmpty() || freeWindingMachines.isEmpty() && queuedWindingMachines.isEmpty()) {
                    model.addAttribute("queueMessage", "Không có máy nào khả dụng (rảnh hoặc trong hàng chờ).");
                }

                model.addAttribute("productionOrder", productionOrder);
                model.addAttribute("productionOrderId", productionOrderId);
                model.addAttribute("freeDyeMachines", freeDyeMachines);
                model.addAttribute("queuedDyeMachines", queuedDyeMachines);
                model.addAttribute("freeWindingMachines", freeWindingMachines);
                model.addAttribute("queuedWindingMachines", queuedWindingMachines);
                model.addAttribute("plannedStartAt", plannedStartAt);
                model.addAttribute("plannedEndAt", plannedEndAt);

                return "technical/create-work-order";
            } catch (Exception e) {
                System.err.println("Lỗi ở showCreateWorkOrderForm: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("error", "Có lỗi xảy ra khi tải form: " + e.getMessage());
                return "redirect:/technical/view-all-work-order";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/check-machine-availability")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkMachineAvailability(
            @RequestParam("productionOrderId") Long productionOrderId,
            @RequestParam("detailId") Long detailId,
            @RequestParam("dyeMachineId") Long dyeMachineId,
            @RequestParam(value = "windingMachineId", required = false) Long windingMachineId,
            @RequestParam("additionalWeight") BigDecimal additionalWeight) {
        Map<String, Object> response = new HashMap<>();
        try {
            ProductionOrder productionOrder = productionOrderService.getProductionOrderById(productionOrderId);
            if (productionOrder == null || productionOrder.getPurchaseOrder() == null || productionOrder.getPurchaseOrder().getSolution() == null || productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate() == null) {
                response.put("success", false);
                response.put("message", "Production Order hoặc ngày giao hàng không hợp lệ.");
                response.put("deadlines", new HashMap<>());
                return ResponseEntity.ok(response);
            }

            ProductionOrderDetail detail = productionOrder.getProductionOrderDetails().stream().filter(d -> d.getId().equals(detailId)).findFirst().orElse(null);
            if (detail == null) {
                response.put("success", false);
                response.put("message", "Production Order Detail không tồn tại.");
                response.put("deadlines", new HashMap<>());
                return ResponseEntity.ok(response);
            }

            LocalDateTime plannedStartAt = LocalDateTime.now().plusHours(2);
            LocalDateTime plannedEndAt = productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate().minusDays(1).atTime(LocalTime.MAX);

            List<DyeMachine> availableDyeMachines = machineService.findAvailableDyeMachinesForProductionOrder(productionOrder, plannedStartAt, plannedEndAt);
            DyeMachine dyeMachine = availableDyeMachines.stream().filter(m -> m.getId().equals(dyeMachineId)).findFirst().orElse(null);
            if (dyeMachine == null) {
                response.put("success", false);
                response.put("message", "Máy nhuộm không khả dụng.");
                response.put("deadlines", new HashMap<>());
                return ResponseEntity.ok(response);
            }

            BigDecimal threadMass = detail.getThread_mass() != null && !detail.getThread_mass().equals(BigDecimal.ZERO) ? detail.getThread_mass() : detail.getPurchaseOrderDetail().getProduct().getThread().getConvert_rate().multiply(BigDecimal.valueOf(detail.getPurchaseOrderDetail().getQuantity()));

            // Kiểm tra giới hạn của additionalWeight
            BigDecimal minAdditionalWeight = BigDecimal.valueOf(0.4);
            BigDecimal maxAdditionalWeight = threadMass.divide(BigDecimal.valueOf(10), 2, BigDecimal.ROUND_DOWN); // 1/10 của threadMass

            if (additionalWeight.compareTo(minAdditionalWeight) < 0 || additionalWeight.compareTo(maxAdditionalWeight) > 0) {
                response.put("success", false);
                response.put("message", "Giá trị additionalWeight phải nằm trong khoảng [" + minAdditionalWeight + ", " + maxAdditionalWeight + "].");
                response.put("deadlines", new HashMap<>());
                return ResponseEntity.ok(response);
            }

            // Tính coneWeight với additionalWeight do người dùng nhập
            BigDecimal coneWeight = threadMass.add(additionalWeight);
            BigDecimal maxWeight = dyeMachine.getMaxWeight();
            BigDecimal convertRate = detail.getPurchaseOrderDetail().getProduct().getThread().getConvert_rate();

            BigDecimal coneBatchWeight;
            int dyeBatches;
            if (maxWeight.compareTo(coneWeight) >= 0) {
                dyeBatches = 1;
                coneBatchWeight = coneWeight;
            } else {
                BigDecimal maxProductPerBatch = maxWeight.divide(convertRate, 2, BigDecimal.ROUND_DOWN);
                int maxProductInt = maxProductPerBatch.intValue();
                coneBatchWeight = convertRate.multiply(BigDecimal.valueOf(maxProductInt));
                dyeBatches = coneWeight.divide(coneBatchWeight, 2, BigDecimal.ROUND_UP).intValue();
            }

            BigDecimal littersBatch = coneBatchWeight.multiply(BigDecimal.valueOf(6));
            BigDecimal coneBatchQuantity = coneBatchWeight.divide(BigDecimal.valueOf(1.25), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal coneQuantity = coneWeight.divide(BigDecimal.valueOf(1.25), 2, BigDecimal.ROUND_HALF_UP);

            BigDecimal littersMin = dyeMachine.getLittersMin();
            BigDecimal littersMax = dyeMachine.getLittersMax();
            BigDecimal coneMin = dyeMachine.getConeMin();
            BigDecimal coneMax = dyeMachine.getConeMax();

            boolean isLittersBatchValid = littersBatch.compareTo(littersMin) >= 0 && littersBatch.compareTo(littersMax) <= 0;
            boolean isConeBatchQuantityValid = coneBatchQuantity.compareTo(coneMin) >= 0 && coneBatchQuantity.compareTo(coneMax) <= 0;

            Map<String, Object> dyeMachineInfo = new HashMap<>();
            dyeMachineInfo.put("maxWeight", dyeMachine.getMaxWeight());
            dyeMachineInfo.put("littersMin", dyeMachine.getLittersMin());
            dyeMachineInfo.put("littersMax", dyeMachine.getLittersMax());
            dyeMachineInfo.put("coneMin", dyeMachine.getConeMin());
            dyeMachineInfo.put("coneMax", dyeMachine.getConeMax());

            Map<String, Object> dyeCalculations = new HashMap<>();
            dyeCalculations.put("coneWeight", coneWeight);
            dyeCalculations.put("coneBatchWeight", coneBatchWeight);
            dyeCalculations.put("coneQuantity", coneQuantity);
            dyeCalculations.put("coneBatchQuantity", coneBatchQuantity);
            dyeCalculations.put("dyeBatches", dyeBatches);
            dyeCalculations.put("littersBatch", littersBatch);

            if (!isLittersBatchValid || !isConeBatchQuantityValid) {
                StringBuilder errorMessage = new StringBuilder("Máy nhuộm không hợp lệ: ");
                if (!isLittersBatchValid) {
                    errorMessage.append("Litters Batch (").append(littersBatch).append(") không nằm trong khoảng [").append(littersMin).append(", ").append(littersMax).append("]. ");
                }
                if (!isConeBatchQuantityValid) {
                    errorMessage.append("Cone Batch Quantity (").append(coneBatchQuantity).append(") không nằm trong khoảng [").append(coneMin).append(", ").append(coneMax).append("].");
                }
                response.put("success", false);
                response.put("message", errorMessage.toString());
                response.put("dyeMachine", dyeMachineInfo);
                response.put("dyeCalculations", dyeCalculations);
                response.put("deadlines", new HashMap<>());
                return ResponseEntity.ok(response);
            }

            long dyeDurationMinutes = dyeBatches * 120 + (dyeBatches - 1) * 15;
            LocalDateTime dyeDeadline = plannedStartAt.plusMinutes(dyeDurationMinutes);

            Map<String, String> deadlines = new HashMap<>();
            deadlines.put("dyeDeadline", dyeDeadline.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            deadlines.put("plannedEndAt", plannedEndAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

            if (windingMachineId == null) {
                boolean dyeMeetsDeadline = !dyeDeadline.isAfter(plannedEndAt);
                response.put("success", dyeMeetsDeadline);
                response.put("message", dyeMeetsDeadline ? "Máy nhuộm khả dụng." : "Máy nhuộm không phù hợp do deadline vượt quá giới hạn.");
                response.put("dyeMachine", dyeMachineInfo);
                response.put("dyeCalculations", dyeCalculations);
                response.put("deadlines", deadlines);
                return ResponseEntity.ok(response);
            }

            List<WindingMachine> availableWindingMachines = machineService.findAvailableWindingMachinesForProductionOrder(productionOrder, plannedStartAt, plannedEndAt);
            WindingMachine windingMachine = availableWindingMachines.stream().filter(m -> m.getId().equals(windingMachineId)).findFirst().orElse(null);
            if (windingMachine == null) {
                response.put("success", false);
                response.put("message", "Máy cuốn không khả dụng.");
                response.put("dyeMachine", dyeMachineInfo);
                response.put("dyeCalculations", dyeCalculations);
                response.put("deadlines", deadlines);
                return ResponseEntity.ok(response);
            }

            int windingBatches = dyeBatches;
            long windingDurationMinutes = windingBatches * 60 + (windingBatches - 1) * 15;
            LocalDateTime windingStart = dyeBatches > 1 ? plannedStartAt.plusMinutes(270) : dyeDeadline;
            LocalDateTime windingDeadline = windingStart.plusMinutes(windingDurationMinutes);

            int packagingBatches = dyeBatches;
            BigDecimal packagingTimePerProduct = BigDecimal.valueOf(0.5);
            BigDecimal totalPackagingDurationMinutes = BigDecimal.ZERO;
            BigDecimal remainingConeWeight = coneWeight;

            for (int i = 0; i < packagingBatches; i++) {
                BigDecimal currentConeBatchWeight = (i == packagingBatches - 1 && remainingConeWeight.compareTo(coneBatchWeight) < 0) ? remainingConeWeight : coneBatchWeight;
                BigDecimal productsInBatch = currentConeBatchWeight.divide(convertRate);
                totalPackagingDurationMinutes = totalPackagingDurationMinutes.add(productsInBatch.multiply(packagingTimePerProduct));
                remainingConeWeight = remainingConeWeight.subtract(currentConeBatchWeight);
            }

            LocalDateTime packagingStart = packagingBatches > 1 ? windingStart.plusMinutes(150) : windingDeadline;
            LocalDateTime packagingDeadline = packagingStart.plusMinutes(totalPackagingDurationMinutes.longValue());

            boolean meetsDeadline = !dyeDeadline.isAfter(plannedEndAt) && !windingDeadline.isAfter(plannedEndAt) && !packagingDeadline.isAfter(plannedEndAt);
            response.put("success", meetsDeadline);
            response.put("message", meetsDeadline ? "Máy khả dụng." : "Không phù hợp do deadline vượt quá giới hạn.");

            deadlines.put("windingDeadline", windingDeadline.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            deadlines.put("packagingDeadline", packagingDeadline.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

            response.put("dyeMachine", dyeMachineInfo);
            response.put("dyeCalculations", dyeCalculations);
            response.put("deadlines", deadlines);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            response.put("deadlines", new HashMap<>());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/create-work-order")
    public String createWorkOrder(
            @RequestParam("productionOrderId") Long productionOrderId,
            @RequestParam(value = "additionalWeights", required = false) List<BigDecimal> additionalWeights,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                String emailOrPhone = authentication.getName();
                Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
                User createBy = optionalUser.orElseThrow(() -> new IllegalStateException("Không tìm thấy thông tin người dùng hiện tại."));

                ProductionOrder productionOrder = productionOrderService.getProductionOrderById(productionOrderId);
                if (productionOrder == null) {
                    System.err.println("Production Order id " + productionOrderId);
                    model.addAttribute("error", "Production Order không tồn tại.");
                    return "redirect:/technical/view-all-work-order";
                }

                // Kiểm tra actualDeliveryDate
                if (productionOrder.getPurchaseOrder() == null ||
                        productionOrder.getPurchaseOrder().getSolution() == null ||
                        productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate() == null) {
                    System.err.println("Purchase Order Id " + productionOrder.getPurchaseOrder() +
                            " Actual Delivery Date " + productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate());
                    model.addAttribute("error", "Ngày giao hàng thực tế không hợp lệ.");
                    return "technical/create-work-order";
                }

                // Tạo DTO và gán giá trị thủ công
                CreateWorkOrderForm form = new CreateWorkOrderForm();
                form.setProductionOrderId(productionOrderId);

                // Lấy danh sách máy nhuộm và máy cuốn thủ công từ request
                List<Long> selectedDyeMachineIds = new ArrayList<>();
                List<Long> selectedWindingMachineIds = new ArrayList<>();

                Map<String, String[]> parameterMap = request.getParameterMap();

                // Lấy các tham số selectedDyeMachineIds[i]
                for (int i = 0; ; i++) {
                    String paramName = "selectedDyeMachineIds[" + i + "]";
                    if (!parameterMap.containsKey(paramName)) {
                        break;
                    }
                    String value = request.getParameter(paramName);
                    if (value != null && !value.isEmpty()) {
                        selectedDyeMachineIds.add(Long.parseLong(value));
                    }
                }

                // Lấy các tham số selectedWindingMachineIds[i]
                for (int i = 0; ; i++) {
                    String paramName = "selectedWindingMachineIds[" + i + "]";
                    if (!parameterMap.containsKey(paramName)) {
                        break;
                    }
                    String value = request.getParameter(paramName);
                    if (value != null && !value.isEmpty()) {
                        selectedWindingMachineIds.add(Long.parseLong(value));
                    }
                }

                // Lấy các tham số additionalWeights[i]
                if (additionalWeights == null) {
                    additionalWeights = new ArrayList<>();
                    for (int i = 0; ; i++) {
                        String paramName = "additionalWeights[" + i + "]";
                        if (!parameterMap.containsKey(paramName)) {
                            break;
                        }
                        String value = request.getParameter(paramName);
                        if (value != null && !value.isEmpty()) {
                            additionalWeights.add(new BigDecimal(value));
                        }
                    }
                }

                form.setSelectedDyeMachineIds(selectedDyeMachineIds);
                form.setSelectedWindingMachineIds(selectedWindingMachineIds);

                // Kiểm tra danh sách máy
                if (form.getSelectedDyeMachineIds().isEmpty() || form.getSelectedWindingMachineIds().isEmpty()) {
                    System.err.println("Selected Dye machine ids " + form.getSelectedDyeMachineIds());
                    model.addAttribute("error", "Vui lòng chọn đầy đủ máy nhuộm và máy cuốn.");
                    return "technical/create-work-order";
                }

                if (form.getSelectedDyeMachineIds().size() != productionOrder.getProductionOrderDetails().size() ||
                        form.getSelectedWindingMachineIds().size() != productionOrder.getProductionOrderDetails().size()) {
                    System.err.println("Selected Dye machine ids " + form.getSelectedDyeMachineIds());
                    model.addAttribute("error", "Số lượng máy được chọn không khớp với số lượng Production Order Details.");
                    return "technical/create-work-order";
                }

                // Kiểm tra số lượng additionalWeights
                if (additionalWeights == null || additionalWeights.isEmpty() ||
                        additionalWeights.size() != productionOrder.getProductionOrderDetails().size()) {
                    System.err.println("Additional Weights: " + additionalWeights);
                    model.addAttribute("error", "Số lượng trọng lượng bổ sung không khớp với số lượng Production Order Details.");
                    return "technical/create-work-order";
                }

                // Kiểm tra giá trị của từng additionalWeight
                for (int i = 0; i < additionalWeights.size(); i++) {
                    BigDecimal weight = additionalWeights.get(i);
                    if (weight == null || weight.compareTo(BigDecimal.valueOf(0.4)) < 0) {
                        System.err.println("Additional Weight at index " + i + ": " + weight);
                        model.addAttribute("error", "Trọng lượng bổ sung tại Detail " + (i + 1) + " phải lớn hơn hoặc bằng 0.4 kg.");
                        return "technical/create-work-order";
                    }
                    // Kiểm tra nếu trọng lượng bổ sung vượt quá 1/10 threadMass của detail tương ứng
                    ProductionOrderDetail detail = productionOrder.getProductionOrderDetails().get(i);
                    BigDecimal maxAdditionalWeight = detail.getThread_mass().divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP);
                    if (weight.compareTo(maxAdditionalWeight) > 0) {
                        System.err.println("Additional Weight at index " + i + ": " + weight + " exceeds max " + maxAdditionalWeight);
                        model.addAttribute("error", "Trọng lượng bổ sung tại Detail " + (i + 1) + " không được vượt quá " + maxAdditionalWeight + " kg (1/10 threadMass).");
                        return "technical/create-work-order";
                    }
                }

                WorkOrder newWorkOrder = workOrderService.createWorkOrder(
                        productionOrder,
                        createBy,
                        form.getSelectedDyeMachineIds(),
                        form.getSelectedWindingMachineIds(),
                        additionalWeights);
                redirectAttributes.addFlashAttribute("success", "Tạo Work Order thành công!");
                return "redirect:/technical/work-order-details/" + newWorkOrder.getId();
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                model.addAttribute("error", e.getMessage());
                return "technical/create-work-order";
            } catch (IllegalStateException e) {
                System.err.println("Error: " + e.getMessage());
                model.addAttribute("error", e.getMessage());
                return "technical/create-work-order";
            } catch (Exception e) {
                System.err.println(e.getMessage());
                model.addAttribute("error", "Có lỗi khi tạo Work Order: " + e.getMessage());
                return "technical/create-work-order";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/submit-work-order/{id}")
    public String submitWorkOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Gọi service để gửi Work Order
            WorkOrder workOrder = workOrderService.submitWorkOrder(id);
            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("success", "Work Order đã được gửi thành công!");
        } catch (IllegalArgumentException e) {
            // Nếu Work Order không tồn tại
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            // Nếu Work Order không ở trạng thái NOT_SENT
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi gửi Work Order: " + e.getMessage());
        }

        // Chuyển hướng về trang chi tiết Work Order
        return "redirect:/technical/work-order-details/" + id;
    }

    @GetMapping("/update-work-order/{workOrderId}")
    public String showUpdateWorkOrderForm(@PathVariable("workOrderId") Long workOrderId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(auth instanceof AnonymousAuthenticationToken)) {
            try {
                WorkOrder workOrder = workOrderService.getWorkOrderById(workOrderId);
                if (workOrder == null) {
                    model.addAttribute("error", "Work order không tồn tại");
                    return "redirect:/technical/view-all-work-order";
                }

                if (workOrder.getStatus() != BaseEnum.WAIT_FOR_UPDATE) {
                    model.addAttribute("error", "Work order không ở trạng thái có thể cập nhật");
                    return "redirect:/technical/work-order-details/" + workOrderId;
                }

                ProductionOrder productionOrder = workOrder.getProductionOrder();
                if (productionOrder == null) {
                    model.addAttribute("error", "Production order không tồn tại");
                    return "redirect:/technical/view-all-work-order";
                }

                if (productionOrder.getPurchaseOrder() == null ||
                        productionOrder.getPurchaseOrder().getSolution() == null ||
                        productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate() == null) {
                    model.addAttribute("error", "Ngày giao hàng thực tế không tồn tại");
                    return "redirect:/technical/view-all-work-order";
                }

                LocalDateTime plannedStartAt = LocalDateTime.now().plusHours(2);
                LocalDateTime plannedEndAt = productionOrder.getPurchaseOrder().getSolution()
                        .getActualDeliveryDate().minusDays(1).atTime(LocalTime.MAX);

                List<DyeMachine> freeDyeMachines = machineService.findFreeDyeMachines(plannedStartAt, plannedEndAt);
                List<WindingMachine> freeWindingMachines = machineService.findFreeWindingMachines(plannedStartAt, plannedEndAt);
                List<DyeMachine> queuedDyeMachines = machineService.findQueuedDyeMachinesForProductionOrder(productionOrder, plannedStartAt, plannedEndAt);
                List<WindingMachine> queuedWindingMachines = machineService.findQueuedWindingMachinesForProductionOrder(productionOrder, plannedStartAt, plannedEndAt);

                List<BigDecimal> additionalWeights = new ArrayList<>();
                for (WorkOrderDetail detail : workOrder.getWorkOrderDetails()) {
                    additionalWeights.add(detail.getAdditionalWeight());
                }

                if (freeDyeMachines.isEmpty() && queuedDyeMachines.isEmpty() ||
                        freeWindingMachines.isEmpty() && queuedWindingMachines.isEmpty()) {
                    model.addAttribute("queueMessage", "Không có máy nào khả dụng (rảnh hoặc trong hàng chờ).");
                }

                model.addAttribute("workOrder", workOrder);
                model.addAttribute("productionOrder", productionOrder);
                model.addAttribute("productionOrderId", productionOrder.getId());
                model.addAttribute("freeDyeMachines", freeDyeMachines);
                model.addAttribute("queuedDyeMachines", queuedDyeMachines);
                model.addAttribute("freeWindingMachines", freeWindingMachines);
                model.addAttribute("queuedWindingMachines", queuedWindingMachines);
                model.addAttribute("additionalWeights", additionalWeights);
                model.addAttribute("plannedStartAt", plannedStartAt);
                model.addAttribute("plannedEndAt", plannedEndAt);
                return "technical/update-work-order";
            } catch (Exception e) {
                System.err.println("Error in showUpdateWorkOrderForm: " + e.getMessage());
                model.addAttribute("error", e.getMessage());
                return "redirect:/technical/work-order-details/" + workOrderId;
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/update-work-order/{workOrderId}")
    public String updateWorkOrder(
            @PathVariable("workOrderId") Long workOrderId,
            @RequestParam(value = "additionalWeights", required = false) List<BigDecimal> additionalWeights,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WorkOrder workOrder = workOrderService.getWorkOrderById(workOrderId);
                if (workOrder == null) {
                    model.addAttribute("error", "WorkOrder không tồn tại.");
                    return "redirect:/technical/view-all-work-order";
                }

                if (workOrder.getStatus() != BaseEnum.WAIT_FOR_UPDATE) {
                    model.addAttribute("error", "WorkOrder không ở trạng thái có thể cập nhật.");
                    return "redirect:/technical/work-order-details/" + workOrderId;
                }

                ProductionOrder productionOrder = workOrder.getProductionOrder();
                if (productionOrder == null) {
                    model.addAttribute("error", "Production Order không tồn tại.");
                    return "redirect:/technical/view-all-work-order";
                }

                List<Long> selectedDyeMachineIds = new ArrayList<>();
                List<Long> selectedWindingMachineIds = new ArrayList<>();

                Map<String, String[]> parameterMap = request.getParameterMap();

                for (int i = 0; ; i++) {
                    String paramName = "selectedDyeMachineIds[" + i + "]";
                    if (!parameterMap.containsKey(paramName)) {
                        break;
                    }
                    String value = request.getParameter(paramName);
                    if (value != null && !value.isEmpty()) {
                        selectedDyeMachineIds.add(Long.parseLong(value));
                    }
                }

                for (int i = 0; ; i++) {
                    String paramName = "selectedWindingMachineIds[" + i + "]";
                    if (!parameterMap.containsKey(paramName)) {
                        break;
                    }
                    String value = request.getParameter(paramName);
                    if (value != null && !value.isEmpty()) {
                        selectedWindingMachineIds.add(Long.parseLong(value));
                    }
                }

                if (additionalWeights == null) {
                    additionalWeights = new ArrayList<>();
                    for (int i = 0; ; i++) {
                        String paramName = "additionalWeights[" + i + "]";
                        if (!parameterMap.containsKey(paramName)) {
                            break;
                        }
                        String value = request.getParameter(paramName);
                        if (value != null && !value.isEmpty()) {
                            additionalWeights.add(new BigDecimal(value));
                        }
                    }
                }

                if (selectedDyeMachineIds.isEmpty() || selectedWindingMachineIds.isEmpty()) {
                    model.addAttribute("error", "Vui lòng chọn đầy đủ máy nhuộm và máy cuốn.");
                    return "technical/update-work-order";
                }

                if (selectedDyeMachineIds.size() != productionOrder.getProductionOrderDetails().size() ||
                        selectedWindingMachineIds.size() != productionOrder.getProductionOrderDetails().size()) {
                    model.addAttribute("error", "Số lượng máy được chọn không khớp với số lượng Production Order Details.");
                    return "technical/update-work-order";
                }

                if (additionalWeights == null || additionalWeights.isEmpty() ||
                        additionalWeights.size() != productionOrder.getProductionOrderDetails().size()) {
                    System.err.println("Additional Weights: " + additionalWeights);
                    model.addAttribute("error", "Số lượng trọng lượng bổ sung không khớp với số lượng Production Order Details.");
                    return "technical/update-work-order";
                }

                for (int i = 0; i < additionalWeights.size(); i++) {
                    BigDecimal weight = additionalWeights.get(i);
                    if (weight == null || weight.compareTo(BigDecimal.valueOf(0.4)) < 0) {
                        System.err.println("Additional Weight at index " + i + ": " + weight);
                        model.addAttribute("error", "Trọng lượng bổ sung tại Detail " + (i + 1) + " phải lớn hơn hoặc bằng 0.4 kg.");
                        return "technical/update-work-order";
                    }
                    ProductionOrderDetail detail = productionOrder.getProductionOrderDetails().get(i);
                    BigDecimal maxAdditionalWeight = detail.getThread_mass().divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP);
                    if (weight.compareTo(maxAdditionalWeight) > 0) {
                        System.err.println("Additional Weight at index " + i + ": " + weight + " exceeds max " + maxAdditionalWeight);
                        model.addAttribute("error", "Trọng lượng bổ sung tại Detail " + (i + 1) + " không được vượt quá " + maxAdditionalWeight + " kg (1/10 threadMass).");
                        return "technical/update-work-order";
                    }
                }

//                WorkOrder updatedWorkOrder = workOrderService.updateWorkOrder(
//                        workOrderId,
//                        selectedDyeMachineIds,
//                        selectedWindingMachineIds,
//                        additionalWeights);
                redirectAttributes.addFlashAttribute("success", "Cập nhật Work Order thành công!");
//                return "redirect:/technical/work-order-details/" + updatedWorkOrder.getId();
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                model.addAttribute("error", e.getMessage());
                return "technical/update-work-order";
            } catch (IllegalStateException e) {
                System.err.println("Error: " + e.getMessage());
                model.addAttribute("error", e.getMessage());
                return "technical/update-work-order";
            } catch (Exception e) {
                System.err.println(e.getMessage());
                model.addAttribute("error", "Có lỗi khi cập nhật Work Order: " + e.getMessage());
                return "technical/update-work-order";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/delete-work-order-details/{workOrderId}")
    public String deleteWorkOrderDetails(@PathVariable("workOrderId") Long workOrderId,
                                         RedirectAttributes redirectAttributes, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                WorkOrder workOrder = workOrderService.getWorkOrderById(workOrderId);
                if (workOrder == null) {
                    redirectAttributes.addFlashAttribute("error", "Work Order không tồn tại.");
                    return "redirect:/technical/view-all-work-order";
                }

                boolean canDelete = (workOrder.getStatus() == BaseEnum.DRAFT &&
                        workOrder.getSendStatus() == SendEnum.NOT_SENT) ||
                        (workOrder.getStatus() == BaseEnum.NOT_APPROVED &&
                                workOrder.getSendStatus() == SendEnum.SENT);

                if (!canDelete) {
                    redirectAttributes.addFlashAttribute("error",
                            "Chỉ có thể xóa Work Order Details khi ở trạng thái DRAFT và NOT_SENT, hoặc NOT_APPROVED và SENT.");
                    return "redirect:/technical/work-order-details/" + workOrderId;
                }

                workOrderService.deleteWorkOrderDetails(workOrderId);

                // Sau khi xóa, lấy lại WorkOrder để đảm bảo trạng thái mới nhất
                workOrder = workOrderService.getWorkOrderById(workOrderId);
                if (workOrder.getWorkOrderDetails().isEmpty()) {
                    redirectAttributes.addFlashAttribute("success", "Xóa Work Order Details thành công!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Xóa Work Order Details không hoàn tất. Vẫn còn dữ liệu tồn tại.");
                }
                return "redirect:/technical/work-order-details/" + workOrderId;

            } catch (IllegalStateException e) {
                System.err.println("Error: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/technical/work-order-details/" + workOrderId;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error",
                        "Có lỗi khi xóa Work Order Details: " + e.getMessage());
                return "redirect:/technical/work-order-details/" + workOrderId;
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