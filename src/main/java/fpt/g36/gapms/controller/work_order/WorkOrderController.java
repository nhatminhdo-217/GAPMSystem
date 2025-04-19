package fpt.g36.gapms.controller.work_order;


import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.services.*;
import fpt.g36.gapms.utils.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/work-order")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private DyeStageService dyeStageService;

    @Autowired
    private PhotoStageService photoStageService;
    @Autowired
    private DyeBatchService dyeBatchService;
    @Autowired
    private WindingBatchService windingBatchService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private TechnologyProcessService technologyProcessService;
    @Autowired
    private WindingStageService windingStageService;

    @Autowired
    private PackagingStageService packagingStageService;

    @Autowired
    private PackagingBatchService packagingBatchService;

    @Autowired
    private WorkOrderDetailService workOrderDetailService;


    @GetMapping("/team-leader/list")
    public String getAllWorkOrderForTeamLeader(Model model,
                                               @RequestParam(required = false) String workOrderId,
                                               @RequestParam(value = "page", defaultValue = "0") String pageStr,
                                               @RequestParam(value = "size", defaultValue = "5") String sizeStr) {
        int page;
        try {
            page = Integer.parseInt(pageStr);
            if (page < 0) { // Không cho phép page âm
                page = 0; // Đặt về mặc định nếu không hợp lệ
            }
        } catch (NumberFormatException e) {
            page = 0; // Nếu không parse được (ví dụ: "l"), đặt về 0
        }

        // Xử lý size
        int size;
        try {
            size = Integer.parseInt(sizeStr);
            if (size <= 0 || size > 100) { // Giới hạn size từ 1 đến 100
                size = 5; // Đặt về mặc định nếu không hợp lệ
            }
        } catch (NumberFormatException e) {
            size = 5; // Nếu không parse được (ví dụ: "l"), đặt về 5
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<WorkOrder> workOrders = workOrderService.getAllWorkOrderTeamLeader(pageable, workOrderId);
        model.addAttribute("currentPage", workOrders.getNumber());
        model.addAttribute("totalPages", workOrders.getTotalPages());
        model.addAttribute("totalItems", workOrders.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("workOrders", workOrders);
        userUtils.getOptionalUser(model);
        return "team-leader/work-order-list-team-leader";
    }

    @GetMapping("/team-leader/detail/{id}")
    public String getWorkOrderDetailTeamLeader(Model model, @PathVariable(value = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        String role = optionalUser.get().getRole().getName();
        if (role.equalsIgnoreCase("LEAD_DYE")) {
            userUtils.getOptionalUser(model);
           List<DyeStage> dyeStages = dyeStageService.getAllDyeStageForDyeLead(id);
            model.addAttribute("workOrderId", id);
           model.addAttribute("dyeStages", dyeStages);
            return "team-leader/dye-stage";
        } else if (role.equalsIgnoreCase("LEAD_WINDING")) {
            userUtils.getOptionalUser(model);
            List<WindingStage> windingStages = windingStageService.getAllWindingStageForWindingLead(id);
            model.addAttribute("workOrderId", id);
            model.addAttribute("windingStages",windingStages);
            return "team-leader/winding-stage";
        } else {
            userUtils.getOptionalUser(model);
            List<PackagingStage> packagingStages = packagingStageService.getAllPackagingStageForPackagingLead(id);
            model.addAttribute("packagingStages", packagingStages);
            model.addAttribute("workOrderId", id);
            return "team-leader/packaging-stage";
        }
    }


    @GetMapping("/team-leader/Batch/{id}")
    public String getDyeBatchTeamLeader(Model model, @PathVariable(value = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        String role = optionalUser.get().getRole().getName();
        if (role.equalsIgnoreCase("LEAD_DYE")) {
            userUtils.getOptionalUser(model);
            List<DyeBatch> dyeBatches = dyeBatchService.getAllDyeBatchForDyeLead(id);
            Long woId = dyeBatches.get(0).getDyeStage().getWorkOrderDetail().getWorkOrder().getId();
            model.addAttribute("dyeBatches", dyeBatches);
            model.addAttribute("StageId", id);
            model.addAttribute("woId", woId);
            return "team-leader/dye-batch";
        } else if (role.equalsIgnoreCase("LEAD_WINDING")) {
            userUtils.getOptionalUser(model);
            List<WindingBatch> windingBatches = windingBatchService.getAllWindingBatchForWindingLead(id);
            Long woId = windingBatches.get(0).getWindingStage().getWorkOrderDetail().getWorkOrder().getId();
            model.addAttribute("windingBatches",windingBatches);
            model.addAttribute("StageId", id);
            model.addAttribute("woId", woId);
            return "team-leader/winding-batch";
        } else {
            userUtils.getOptionalUser(model);
            List<PackagingBatch> packagingBatches = packagingBatchService.getAllPackagingBatchForPackagingLead(id);
            Long woId = packagingBatches.get(0).getPackagingStage().getWorkOrderDetail().getWorkOrder().getId();
            model.addAttribute("packagingBatches", packagingBatches);
            model.addAttribute("woId", woId);
            model.addAttribute("StageId", id);
            return "team-leader/packaging-batch";
        }
    }

    @GetMapping("/team-leader/Dye/change/in-process/{id}")
    public String chaneWorkStatusInProcess(@PathVariable("id") Long dbId,Model model, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        User leader = optionalUser.get();
        dyeBatchService.changeStatusDyeBatchInProcess(dbId, leader);
        DyeBatch dyeBatch = dyeBatchService.getDyeBatchById(dbId);
        redirectAttributes.addFlashAttribute("in_process", "Mã mẻ DB-" + dyeBatch.getId() + " Đã được chuyển sang bắt đầu nhuộm");
        return "redirect:/work-order/technology-process/" + dyeBatch.getId();
    }

    @PostMapping("/team-leader/Dye/change/finish/{id}")
    public String completeDyeing(@PathVariable("id") Long dbId, @RequestParam("photo") MultipartFile photo, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra file ảnh
            if (photo == null || photo.isEmpty()) {
                throw new IllegalArgumentException("File ảnh không được để trống");
            }

            // Lưu ảnh vào thư mục upload
            String photoSaved = imageService.saveImageMultiFile(photo);
            System.err.println("Photo saved: " + photoSaved);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> optionalUser = null;
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String emailOrPhone = authentication.getName();
                optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            }
            User leader = optionalUser.get();
            // Cập nhật trạng thái thành FINISHED
            dyeBatchService.changeStatusDyeBatchFinish(dbId, photoSaved , leader);
            DyeBatch dyeBatch = dyeBatchService.getDyeBatchById(dbId);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("completed", "Mã mẻ DB-" + dyeBatch.getId() + " Đã Chuyển sang trạng thái hoàn thành");
        } catch (IllegalArgumentException e) {
            // Thêm thông báo lỗi nếu có ngoại lệ
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xử lý: " + e.getMessage());
        }

        // Redirect về trang chi tiết
        DyeBatch dyeBatch = dyeBatchService.getDyeBatchById(dbId);
        return "redirect:/work-order/technology-process/" + dyeBatch.getId();
    }

    @GetMapping("/team-leader/winding/change/in-process/{id}")
    public String chaneWorkStatusInProcessWinding(@PathVariable("id") Long wbId,Model model, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        User leader = optionalUser.get();
        windingBatchService.changeStatusWindingBatchInProcess(wbId, leader);
        WindingBatch windingBatch = windingBatchService.getWindingBatchById(wbId);
        redirectAttributes.addFlashAttribute("in_process_winding", "Mã Mẻ WB-" + windingBatch.getId() + " Đã được chuyển sang bắt đầu Côn");
        return "redirect:/work-order/technology-process/" + windingBatch.getDyeBatch().getId();
    }

    @PostMapping("/team-leader/winding/change/finish/{id}")
    public String completeWinding(@PathVariable("id") Long id, @RequestParam("photo") MultipartFile photo, RedirectAttributes redirectAttributes) {

        try {
            // Kiểm tra file ảnh
            if (photo == null || photo.isEmpty()) {
                throw new IllegalArgumentException("File ảnh không được để trống");
            }

            // Lưu ảnh vào thư mục upload
            String photoSaved = imageService.saveImageMultiFile(photo);
            System.err.println("Photo saved: " + photoSaved);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> optionalUser = null;
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String emailOrPhone = authentication.getName();
                optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            }
            User leader = optionalUser.get();
            // Cập nhật trạng thái thành FINISHED
            windingBatchService.changeStatusWindingBatchFinish(id, photoSaved, leader);
            WindingBatch windingBatch  = windingBatchService.getWindingBatchById(id);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("complete_winding", "Mã Mẻ WB-" + windingBatch.getId() + " Đã được hoàn thành");
        } catch (IllegalArgumentException e) {
            // Thêm thông báo lỗi nếu có ngoại lệ
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xử lý: " + e.getMessage());
        }

        // Redirect về trang chi tiết
        WindingBatch windingBatch = windingBatchService.getWindingBatchById(id);
        return "redirect:/work-order/technology-process/" + windingBatch.getDyeBatch().getId();
    }



    @GetMapping("/team-leader/packaging/change/in-process/{id}")
    public String chaneWorkStatusInProcessPackaging(@PathVariable("id") Long pbId,Model model, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        User leader = optionalUser.get();
        packagingBatchService.changeStatusPackagingBatchInProcess(pbId, leader);
       PackagingBatch packagingBatch = packagingBatchService.getPackagingBatchById(pbId);
        redirectAttributes.addFlashAttribute("in_process_packaging", "Mã Mẻ PB-" + packagingBatch.getId() + " Đã Được Chuyển Sang Bắt Đầu Đóng Gói");
        return "redirect:/work-order/technology-process/" + packagingBatch.getWindingBatch().getDyeBatch().getId();
    }

    @PostMapping("/team-leader/packaging/change/finish/{id}")
    public String completePackaging(@PathVariable("id") Long id, @RequestParam("photo") MultipartFile photo, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra file ảnh
            if (photo == null || photo.isEmpty()) {
                throw new IllegalArgumentException("File ảnh không được để trống");
            }

            // Lưu ảnh vào thư mục upload
            String photoSaved = imageService.saveImageMultiFile(photo);
            System.err.println("Photo saved: " + photoSaved);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> optionalUser = null;
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String emailOrPhone = authentication.getName();
                optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            }
            User leader = optionalUser.get();
            // Cập nhật trạng thái thành FINISHED
            packagingBatchService.changeStatusPackagingBatchFinish(id, photoSaved, leader);
            PackagingBatch packagingBatch = packagingBatchService.getPackagingBatchById(id);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("complete_packaging", "Mã mẻ WD-" + packagingBatch.getId() + " Đã chuyển sang trạng thái hoàn thành");
        } catch (IllegalArgumentException e) {
            // Thêm thông báo lỗi nếu có ngoại lệ
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xử lý: " + e.getMessage());
        }

        // Redirect về trang chi tiết
        PackagingBatch packagingBatch = packagingBatchService.getPackagingBatchById(id);
        return "redirect:/work-order/technology-process/" + packagingBatch.getWindingBatch().getDyeBatch().getId();
    }



    @GetMapping("/technology-process/{id}")
    public String getTechnology(Model model, @PathVariable("id") Long dyeId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        String role = optionalUser.get().getRole().getName();
        if (role.equalsIgnoreCase("LEAD_DYE")) {
            userUtils.getOptionalUser(model);
            TechnologyProcess technologyProcess = technologyProcessService.getByDyeId(dyeId);
            model.addAttribute("technologyProcess", technologyProcess);
            return "team-leader/dye-technology-process";
        } else if (role.equalsIgnoreCase("LEAD_WINDING")) {
            userUtils.getOptionalUser(model);
            TechnologyProcess technologyProcess = technologyProcessService.getByDyeId(dyeId);
            model.addAttribute("technologyProcess", technologyProcess);
            return "team-leader/winding-technology-process";
        } else if (role.equalsIgnoreCase("LEAD_PACKAGING")){
            userUtils.getOptionalUser(model);
            TechnologyProcess technologyProcess = technologyProcessService.getByDyeId(dyeId);
            model.addAttribute("technologyProcess", technologyProcess);
            return "team-leader/packaging-technology-process";
        }else if (role.equalsIgnoreCase("QA_DYE")){
            DyeRiskAssessment dyeRiskAssessment = dyeStageService.getDyeRiskAssessmentByDyeStageId(dyeId);
            if(dyeRiskAssessment == null){
                DyeBatch dyeBatch = dyeBatchService.getDyeBatchById(dyeId);
                userUtils.getOptionalUser(model);
                model.addAttribute("dyeBatch", dyeBatch);
                model.addAttribute("pass_check_delete", null);
                return "quality_assurance/qa-test-dye";
            }
            model.addAttribute("pass_check_delete", dyeRiskAssessment.getPass());
            model.addAttribute("dyeRiskAssessment", dyeRiskAssessment);
            userUtils.getOptionalUser(model);
            return "quality_assurance/qa-test-dye";
        }else if (role.equalsIgnoreCase("QA_WINDING")){
            WindingRiskAssessment windingRiskAssessment = windingStageService.getWindingRiskAssessmentByWindingBatchId(dyeId);
            if(windingRiskAssessment == null){
                WindingBatch windingBatch = windingBatchService.getWindingBatchById(dyeId);
                userUtils.getOptionalUser(model);
                model.addAttribute("windingBatch", windingBatch);
                model.addAttribute("pass_check_delete", null);
                return "quality_assurance/qa-test-winding";
            }

            model.addAttribute("pass_check_delete", windingRiskAssessment.getPass());
            model.addAttribute("windingRiskAssessment", windingRiskAssessment);
            userUtils.getOptionalUser(model);
            return "quality_assurance/qa-test-winding";
        }else{
            PackagingRiskAssessment packagingRiskAssessment = packagingStageService.getPackagingRiskAssessmentByPackagingBatchId(dyeId);
            if(packagingRiskAssessment == null){
                PackagingBatch packagingBatch = packagingBatchService.getPackagingBatchById(dyeId);
                userUtils.getOptionalUser(model);
                model.addAttribute("packagingBatch", packagingBatch);
                model.addAttribute("pass_check_delete", null);
                return "quality_assurance/qa-test-packaging";
            }
            model.addAttribute("pass_check_delete", packagingRiskAssessment.getPass());
            model.addAttribute("packagingRiskAssessment", packagingRiskAssessment);
            userUtils.getOptionalUser(model);
            return "quality_assurance/qa-test-packaging";
        }
    }

    /*----------------------------------------------QA----------------------------------------------------*/

    @GetMapping("/quality_assurance/list")
    public String getAllWorkOrderForQA(Model model,
                                       @RequestParam(required = false) String workOrderId,
                                       @RequestParam(value = "page", defaultValue = "0") String pageStr,
                                       @RequestParam(value = "size", defaultValue = "5") String sizeStr) {
        int page;
        try {
            page = Integer.parseInt(pageStr);
            if (page < 0) { // Không cho phép page âm
                page = 0; // Đặt về mặc định nếu không hợp lệ
            }
        } catch (NumberFormatException e) {
            page = 0; // Nếu không parse được (ví dụ: "l"), đặt về 0
        }

        // Xử lý size
        int size;
        try {
            size = Integer.parseInt(sizeStr);
            if (size <= 0 || size > 100) { // Giới hạn size từ 1 đến 100
                size = 5; // Đặt về mặc định nếu không hợp lệ
            }
        } catch (NumberFormatException e) {
            size = 5; // Nếu không parse được (ví dụ: "l"), đặt về 5
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<WorkOrder> workOrders = workOrderService.getAllWorkOrderTeamLeader(pageable, workOrderId);
        model.addAttribute("currentPage", workOrders.getNumber());
        model.addAttribute("totalPages", workOrders.getTotalPages());
        model.addAttribute("totalItems", workOrders.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("workOrders", workOrders);
        userUtils.getOptionalUser(model);
        return "quality_assurance/work-order-list-quality_assurance";
    }

    @GetMapping("/quality_assurance/detail/{id}")
    public String getWorkOrderDetailQA(Model model, @PathVariable(value = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        String role = optionalUser.get().getRole().getName();
        if (role.equalsIgnoreCase("QA_DYE")) {
            userUtils.getOptionalUser(model);
            List<DyeStage> dyeStages = dyeStageService.getAllDyeStageForDyeLead(id);
            model.addAttribute("dyeStages", dyeStages);
            model.addAttribute("woId", id);
            return "quality_assurance/qa-dye-stage";
        } else if (role.equalsIgnoreCase("QA_WINDING")) {
            userUtils.getOptionalUser(model);
            List<WindingStage> windingStages = windingStageService.getAllWindingStageForWindingLead(id);
            model.addAttribute("windingStages",windingStages);
            model.addAttribute("woId", id);
            return "quality_assurance/qa-winding-stage";
        } else {
            userUtils.getOptionalUser(model);
            List<PackagingStage> packagingStages = packagingStageService.getAllPackagingStageForPackagingLead(id);
            model.addAttribute("packagingStages", packagingStages);
            model.addAttribute("woId", id);
            return "quality_assurance/qa-packaging-stage";
        }
    }

    @GetMapping("/quality_assurance/Batch/{id}")
    public String getDyeBatchQualityAssurance(Model model, @PathVariable(value = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        String role = optionalUser.get().getRole().getName();
        if (role.equalsIgnoreCase("QA_DYE")) {
            userUtils.getOptionalUser(model);
            List<DyeBatch> dyeBatches = dyeBatchService.getAllDyeBatchForDyeLead(id);
            Long woId = dyeBatches.get(0).getDyeStage().getWorkOrderDetail().getWorkOrder().getId();
            model.addAttribute("dyeBatches", dyeBatches);
            model.addAttribute("stageId", id);
            model.addAttribute("woId", woId);
            return "quality_assurance/qa-dye-batch";
        } else if (role.equalsIgnoreCase("QA_WINDING")) {
            userUtils.getOptionalUser(model);
            List<WindingBatch> windingBatches = windingBatchService.getAllWindingBatchForWindingLead(id);
            Long woId = windingBatches.get(0).getWindingStage().getWorkOrderDetail().getWorkOrder().getId();
            model.addAttribute("windingBatches",windingBatches);
            model.addAttribute("stageId", id);
            model.addAttribute("woId", woId);
            return "quality_assurance/qa-winding-batch";
        } else {
            userUtils.getOptionalUser(model);
            List<PackagingBatch> packagingBatches = packagingBatchService.getAllPackagingBatchForPackagingLead(id);
            Long woId = packagingBatches.get(0).getPackagingStage().getWorkOrderDetail().getWorkOrder().getId();
            model.addAttribute("packagingBatches", packagingBatches);
            model.addAttribute("stageId", id);
            model.addAttribute("woId", woId);
            return "quality_assurance/qa-packaging-batch";
        }
    }

   /* @GetMapping("/quality_assurance/test-form/dye/{id}")
    public String getTestDyeForm(Model model, @PathVariable(value = "id") Long id){
        DyeRiskAssessment dyeRiskAssessment = dyeStageService.getDyeRiskAssessmentByDyeStageId(id);
        if(dyeRiskAssessment == null){
            DyeBatch dyeBatch = dyeBatchService.getDyeBatchById(id);
            userUtils.getOptionalUser(model);
            model.addAttribute("dyeBatch", dyeBatch);
            return "quality_assurance/qa-test-dye";
        }
        model.addAttribute("dyeRiskAssessment", dyeRiskAssessment);
        userUtils.getOptionalUser(model);
        return "quality_assurance/qa-test-dye";
    }*/

    @PostMapping("/quality_assurance/test/dye/{id}")
    public String SaveTestDye(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("dyeRiskAssessment") DyeRiskAssessment dyeRiskAssessment,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes,
                              Model model,
                              @RequestParam(value = "photos", required = false) MultipartFile[] photos,
                              @RequestParam(value = "existingPhotos", required = false) String existingPhotos,
                              @RequestParam(value = "deletedPhotos", required = false) String deletedPhotos
                              ) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }

        if(dyeRiskAssessment.getPass() != null){
            if((dyeRiskAssessment.getColorFading() == null ||
                    dyeRiskAssessment.getColorTrue() == null ||
                    dyeRiskAssessment.getHumidity() == null ||
                    dyeRiskAssessment.getLightTrue() == null ||
                    dyeRiskAssessment.getIndustrialCleaningStains() == null ||
                    dyeRiskAssessment.getMedication() == null ||
                    dyeRiskAssessment.getMedicineSafe() == null))
            {

                redirectAttributes.addFlashAttribute("check_pass", "Chỉ có thể đánh pass/false khi các trường đã được đánh giá đủ");
                return "redirect:/work-order/technology-process/" + dyeRiskAssessment.getDyeBatch().getId();
            }

        }

        if(dyeRiskAssessment.getPass() != null){
            if(dyeRiskAssessment.getPass()) {
                if ((!dyeRiskAssessment.getColorFading() ||
                        !dyeRiskAssessment.getColorTrue() ||
                        !dyeRiskAssessment.getHumidity() ||
                        !dyeRiskAssessment.getLightTrue() ||
                        !dyeRiskAssessment.getIndustrialCleaningStains() ||
                        !dyeRiskAssessment.getMedication() ||
                        !dyeRiskAssessment.getMedicineSafe())) {

                    redirectAttributes.addFlashAttribute("check_pass_when_false", "Chỉ có thể đánh Pass khi các trường đêu trong trạng thái đạt chuẩn");
                    return "redirect:/work-order/technology-process/" + dyeRiskAssessment.getDyeBatch().getId();
                }
            }
        }

        try {
            DyeRiskAssessment dyeRiskAssessment_save = dyeStageService.saveTestDye(id, dyeRiskAssessment, optionalUser.get(), photos);
            redirectAttributes.addFlashAttribute("save_dye", "Đã lưu thông tin kiểm tra");
            return "redirect:/work-order/technology-process/" + dyeRiskAssessment.getDyeBatch().getId();
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("check_photo", e.getMessage());
            return "redirect:/work-order/technology-process/" + dyeRiskAssessment.getDyeBatch().getId();
        }
    }


    /*@GetMapping("/quality_assurance/test-form/winding/{id}")
    public String getTestWindingForm(Model model, @PathVariable(value = "id") Long id){
        WindingRiskAssessment windingRiskAssessment = windingStageService.getWindingRiskAssessmentByWindingBatchId(id);
        if(windingRiskAssessment == null){
            WindingBatch windingBatch = windingBatchService.getWindingBatchById(id);
            userUtils.getOptionalUser(model);
            model.addAttribute("windingBatch", windingBatch);
            return "quality_assurance/qa-test-winding";
        }

        model.addAttribute("windingRiskAssessment", windingRiskAssessment);
        userUtils.getOptionalUser(model);
        return "quality_assurance/qa-test-winding";
    }*/

    @PostMapping("/quality_assurance/test/winding/{id}")
    public String SaveTestWinding(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("windingRiskAssessment") WindingRiskAssessment windingRiskAssessment,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes,
                              Model model,
                              @RequestParam(value = "photos", required = false) MultipartFile[] photos,
                              @RequestParam(value = "existingPhotos", required = false) String existingPhotos,
                              @RequestParam(value = "deletedPhotos", required = false) String deletedPhotos
    ) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }

        if(windingRiskAssessment.getPass() != null){
            if((windingRiskAssessment.getColorFading() == null ||
                    windingRiskAssessment.getColorUniformity() == null ||
                    windingRiskAssessment.getTrueCone() == null ||
                    windingRiskAssessment.getFalseCone() == null )
                    )
            {
                redirectAttributes.addFlashAttribute("check_pass_winding", "Chỉ có thể đánh pass/false khi các trường đã được đánh giá đủ");
                return "redirect:/work-order/technology-process/" + windingRiskAssessment.getWindingBatch().getDyeBatch().getId() ;
            }

        }

        if(windingRiskAssessment.getPass() != null){
            if(windingRiskAssessment.getPass()) {
                if ((!windingRiskAssessment.getColorFading() ||
                        !windingRiskAssessment.getColorUniformity()
                )) {
                    redirectAttributes.addFlashAttribute("check_pass_when_false", "Chỉ có thể đánh Pass khi các trường đêu trong trạng thái đạt chuẩn");
                    return "redirect:/work-order/technology-process/" + windingRiskAssessment.getWindingBatch().getDyeBatch().getId();
                }
            }
        }

        try {
        WindingRiskAssessment windingRiskAssessment_save = windingStageService.saveTestWingding(id, windingRiskAssessment, optionalUser.get(), photos);
        redirectAttributes.addFlashAttribute("save_winding", "Đã lưu thông tin kiểm tra");
            return "redirect:/work-order/technology-process/" + windingRiskAssessment.getWindingBatch().getDyeBatch().getId() ;
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("check_photo_winding", e.getMessage());
            return "redirect:/work-order/technology-process/" + windingRiskAssessment.getWindingBatch().getDyeBatch().getId() ;
        }
    }



    /*@GetMapping("/quality_assurance/test-form/packaging/{id}")
    public String getTestPackagingForm(Model model, @PathVariable(value = "id") Long id){
        PackagingRiskAssessment packagingRiskAssessment = packagingStageService.getPackagingRiskAssessmentByPackagingBatchId(id);
        if(packagingRiskAssessment == null){
             PackagingBatch packagingBatch = packagingBatchService.getPackagingBatchById(id);
            userUtils.getOptionalUser(model);
            model.addAttribute("packagingBatch", packagingBatch);
            return "quality_assurance/qa-test-packaging";
        }
        model.addAttribute("packagingRiskAssessment", packagingRiskAssessment);
        userUtils.getOptionalUser(model);
        return "quality_assurance/qa-test-packaging";
    }*/

    @PostMapping("/quality_assurance/test/packaging/{id}")
    public String SaveTestPackaging(@PathVariable("id") Long id,
                                  @Valid @ModelAttribute("packagingRiskAssessment") PackagingRiskAssessment packagingRiskAssessment,
                                  BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                  Model model,
                                  @RequestParam(value = "photos", required = false) MultipartFile[] photos,
                                  @RequestParam(value = "existingPhotos", required = false) String existingPhotos,
                                  @RequestParam(value = "deletedPhotos", required = false) String deletedPhotos
    ) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }

        if(packagingRiskAssessment.getPass() != null){
            if((packagingRiskAssessment.getFirstStamp() == null ||
                    packagingRiskAssessment.getCoreStamp() == null ||
                    packagingRiskAssessment.getDozenStamp() == null ||
                    packagingRiskAssessment.getKcsStamp() == null )
            )
            {
                redirectAttributes.addFlashAttribute("check_pass_packaging", "Chỉ có thể đánh pass/false khi các trường đã được đánh giá đủ");
                return "redirect:/work-order/technology-process/" + packagingRiskAssessment.getPackagingBatch().getWindingBatch().getDyeBatch().getId();
            }

        }

        if(packagingRiskAssessment.getPass() != null){
            if(packagingRiskAssessment.getPass()) {
                if ((!packagingRiskAssessment.getFirstStamp() ||
                        !packagingRiskAssessment.getCoreStamp()||
                        !packagingRiskAssessment.getDozenStamp() ||
                        !packagingRiskAssessment.getKcsStamp())
                ) {
                    redirectAttributes.addFlashAttribute("check_pass_when_false", "Chỉ có thể đánh Pass khi các trường đêu trong trạng thái đạt chuẩn");
                    return "redirect:/work-order/technology-process/" + packagingRiskAssessment.getPackagingBatch().getWindingBatch().getDyeBatch().getId();
                }
            }
        }

        try {
        PackagingRiskAssessment packagingRiskAssessment_save = packagingStageService.saveTestPackaging(id, packagingRiskAssessment, optionalUser.get(), photos);
        redirectAttributes.addFlashAttribute("save_packaging", "Đã lưu thông tin kiểm tra");
            return "redirect:/work-order/technology-process/" + packagingRiskAssessment.getPackagingBatch().getWindingBatch().getDyeBatch().getId();
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("check_photo_packaging", e.getMessage());
            return "redirect:/work-order/technology-process/" + packagingRiskAssessment.getPackagingBatch().getWindingBatch().getDyeBatch().getId();
        }
    }


    @DeleteMapping("/quality_assurance/delete-photo")
    @ResponseBody
    public Map<String, Object> deletePhoto(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String photoName = request.get("photoName");

        try {
            boolean deleted = photoStageService.deletePhoto(photoName);
            if (deleted) {
                response.put("success", true);
            } else {
                response.put("success", false);
                response.put("message", "Ảnh không tồn tại trong database.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi xóa ảnh: " + e.getMessage());
        }
        return response;
    }


    /*----------------------------------Production Manager-------------------------------------------*/

    @GetMapping("/production-manager/list")
    public String getAllWorkOrderForPo(Model model,
                                               @RequestParam(required = false) String workOrderId,
                                               @RequestParam(value = "page", defaultValue = "0") String pageStr,
                                               @RequestParam(value = "size", defaultValue = "10") String sizeStr) {
        int page;
        try {
            page = Integer.parseInt(pageStr);
            if (page < 0) { // Không cho phép page âm
                page = 0; // Đặt về mặc định nếu không hợp lệ
            }
        } catch (NumberFormatException e) {
            page = 0; // Nếu không parse được (ví dụ: "l"), đặt về 0
        }

        // Xử lý size
        int size;
        try {
            size = Integer.parseInt(sizeStr);
            if (size <= 0 || size > 100) { // Giới hạn size từ 1 đến 100
                size = 5; // Đặt về mặc định nếu không hợp lệ
            }
        } catch (NumberFormatException e) {
            size = 5; // Nếu không parse được (ví dụ: "l"), đặt về 5
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<WorkOrder> workOrders = workOrderService.getAllWorkOrderPo(pageable, workOrderId);
        model.addAttribute("currentPage", workOrders.getNumber());
        model.addAttribute("totalPages", workOrders.getTotalPages());
        model.addAttribute("totalItems", workOrders.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("workOrders", workOrders);
        userUtils.getOptionalUser(model);
        return "production-manager/view-list-work-order";
    }

    @GetMapping("/production-manager/detail/{id}")
    public String getWorkOrderDetailForPo(Model model, @PathVariable("id") Long woId) {

        List<WorkOrderDetail> workOrderDetails = workOrderDetailService.getAllByWoId(woId);
        model.addAttribute("workOrderDetails",workOrderDetails);
        userUtils.getOptionalUser(model);
        return "production-manager/view-work-order-detail";
    }
}
