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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private ImageService imageService;

    @Autowired
    private WindingStageService windingStageService;

    @Autowired
    private PackagingStageService packagingStageService;

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
           model.addAttribute("dyeStages", dyeStages);
            return "team-leader/dye";
        } else if (role.equalsIgnoreCase("LEAD_WINDING")) {
            userUtils.getOptionalUser(model);
            List<WindingStage> windingStages = windingStageService.getAllWindingStageForWindingLead(id);
            model.addAttribute("windingStages",windingStages);
            return "team-leader/winding";
        } else {
            userUtils.getOptionalUser(model);
            List<PackagingStage> packagingStages = packagingStageService.getAllPackagingStageForPackagingLead(id);
            model.addAttribute("packagingStages", packagingStages);
            return "team-leader/packaging";
        }
    }

    @GetMapping("/team-leader/Dye/change/in-process/{id}")
    public String chaneWorkStatusInProcess(@PathVariable("id") Long dyeId,Model model, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        User leader = optionalUser.get();
        dyeStageService.changeStatusDyeStageInProcess(dyeId, leader);
        DyeStage dyeStage = dyeStageService.getDyeStageById(dyeId);
        redirectAttributes.addFlashAttribute("in_process", "Mã WD-" + dyeStage.getWorkOrderDetail().getId() + " Đã được chuyển sang bắt đầu nhuộm");
        return "redirect:/work-order/team-leader/detail/" + dyeStage.getWorkOrderDetail().getId();
    }

    @PostMapping("/team-leader/Dye/change/finish/{id}")
    public String completeDyeing(@PathVariable("id") Long id, @RequestParam("photo") MultipartFile photo, RedirectAttributes redirectAttributes) {
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
            dyeStageService.changeStatusDyeStageFinish(id, photoSaved, leader);
            DyeStage dyeStage = dyeStageService.getDyeStageById(id);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("completed", "Mã WD-" + dyeStage.getWorkOrderDetail().getId() + " Đã được hoàn thành");
        } catch (IllegalArgumentException e) {
            // Thêm thông báo lỗi nếu có ngoại lệ
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xử lý: " + e.getMessage());
        }

        // Redirect về trang chi tiết
        DyeStage dyeStage = dyeStageService.getDyeStageById(id);
        return "redirect:/work-order/team-leader/detail/" + dyeStage.getWorkOrderDetail().getId();
    }

    @GetMapping("/team-leader/winding/change/in-process/{id}")
    public String chaneWorkStatusInProcessWinding(@PathVariable("id") Long wdId,Model model, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        User leader = optionalUser.get();
        windingStageService.changeStatusWindingStageInProcess(wdId, leader);
        WindingStage windingStage = windingStageService.getWindingStageById(wdId);
        redirectAttributes.addFlashAttribute("in_process_winding", "Mã WD-" + windingStage.getWorkOrderDetail().getId() + " Đã được chuyển sang bắt đầu Côn");
        return "redirect:/work-order/team-leader/detail/" + windingStage.getWorkOrderDetail().getId();
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
            windingStageService.changeStatusWindingStageFinish(id, photoSaved, leader);
            WindingStage windingStage = windingStageService.getWindingStageById(id);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("complete_winding", "Mã WD-" + windingStage.getWorkOrderDetail().getId() + " Đã được hoàn thành");
        } catch (IllegalArgumentException e) {
            // Thêm thông báo lỗi nếu có ngoại lệ
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xử lý: " + e.getMessage());
        }

        // Redirect về trang chi tiết
        WindingStage windingStage = windingStageService.getWindingStageById(id);
        return "redirect:/work-order/team-leader/detail/" + windingStage.getWorkOrderDetail().getId();
    }



    @GetMapping("/team-leader/packaging/change/in-process/{id}")
    public String chaneWorkStatusInProcessPackaging(@PathVariable("id") Long psId,Model model, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        }
        User leader = optionalUser.get();
        packagingStageService.changeStatusPackagingStageInProcess(psId, leader);
       PackagingStage packagingStage = packagingStageService.getPackagingStageById(psId);
        redirectAttributes.addFlashAttribute("in_process_packaging", "Mã WD-" + packagingStage.getWorkOrderDetail().getId() + " Đã Được Chuyển Sang Bắt Đầu Đóng Gói");
        return "redirect:/work-order/team-leader/detail/" + packagingStage.getWorkOrderDetail().getId();
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
            packagingStageService.changeStatusPackagingStageFinish(id, photoSaved, leader);
            PackagingStage packagingStage = packagingStageService.getPackagingStageById(id);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("complete_packaging", "Mã WD-" + packagingStage.getWorkOrderDetail().getId() + " Đã được hoàn thành");
        } catch (IllegalArgumentException e) {
            // Thêm thông báo lỗi nếu có ngoại lệ
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xử lý: " + e.getMessage());
        }

        // Redirect về trang chi tiết
        PackagingStage packagingStage = packagingStageService.getPackagingStageById(id);
        return "redirect:/work-order/team-leader/detail/" + packagingStage.getWorkOrderDetail().getId();
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
            return "quality_assurance/qa_dye";
        } else if (role.equalsIgnoreCase("QA_WINDING")) {
            userUtils.getOptionalUser(model);
            List<WindingStage> windingStages = windingStageService.getAllWindingStageForWindingLead(id);
            model.addAttribute("windingStages",windingStages);
            return "quality_assurance/qa_packaging";
        } else {
            userUtils.getOptionalUser(model);
            List<PackagingStage> packagingStages = packagingStageService.getAllPackagingStageForPackagingLead(id);
            model.addAttribute("packagingStages", packagingStages);
            return "quality_assurance/qa_packaging";
        }
    }

    @GetMapping("/quality_assurance/test-form/dye/{id}")
    public String getTestDyeForm(Model model, @PathVariable(value = "id") Long id){
        DyeRiskAssessment dyeRiskAssessment = dyeStageService.getDyeRiskAssessmentByDyeStageId(id);
        model.addAttribute("dyeRiskAssessment", dyeRiskAssessment);
        userUtils.getOptionalUser(model);
        return "quality_assurance/qa-test-dye";
    }

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

        /*String uploadDir = "/uploads";

        List<String> existingPhotoList = existingPhotos != null && !existingPhotos.isEmpty()
                ? new ArrayList<>(Arrays.asList(existingPhotos.split(",")))
                : new ArrayList<>();
        List<String> deletedPhotoList = deletedPhotos != null && !deletedPhotos.isEmpty()
                ? new ArrayList<>(Arrays.asList(deletedPhotos.split(",")))
                : new ArrayList<>();

        // Xóa các ảnh trong deletedPhotoList
        for (String deletedPhoto : deletedPhotoList) {
            if (existingPhotoList.contains(deletedPhoto)) {
                existingPhotoList.remove(deletedPhoto);
                File fileToDelete = new File(uploadDir + deletedPhoto);
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        }*/
        DyeRiskAssessment dyeRiskAssessment_save = dyeStageService.saveTestDye(id, dyeRiskAssessment, optionalUser.get(), photos);

        return "redirect:/work-order/quality_assurance/test-form/dye/" + dyeRiskAssessment_save.getDyeStage().getId() ;
    }


}
