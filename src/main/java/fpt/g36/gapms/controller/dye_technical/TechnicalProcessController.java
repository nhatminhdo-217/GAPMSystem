package fpt.g36.gapms.controller.dye_technical;

import fpt.g36.gapms.models.entities.TechnologyProcess;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.TechnologyProcessService;
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
@RequestMapping("/dye-technical")
public class TechnicalProcessController {
    @Autowired
    private TechnologyProcessService technologyProcessService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;

    @GetMapping("/view-all-technology-process")
    public String viewAllTechnologyProcesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String previousStatus, // Thêm tham số để lưu trạng thái trước đó nếu cần
            Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            // Lấy đối tượng User từ Authentication
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
                        // Tìm TechnologyProcess theo ID và createdBy
                        TechnologyProcess technologyProcess = technologyProcessService.getTechnologyProcessByIdAndCreatedBy(searchId, currentUser);
                        // Trả về chỉ TechnologyProcess tìm thấy
                        technologyProcessPage = new PageImplWrapper<>(Collections.singletonList(technologyProcess), pageable, 1);
                        // Lưu trạng thái trước đó nếu cần
                        model.addAttribute("previousStatus", previousStatus != null ? previousStatus : "");
                    } catch (RuntimeException e) {
                        technologyProcessPage = new PageImplWrapper<>(Collections.emptyList(), pageable, 0);
                        model.addAttribute("error", "Không tìm thấy Technology Process với ID: " + searchId + " cho user: " + currentUser.getUsername());
                        // Nếu không tìm thấy, quay về tab trước đó (previousStatus)
                        String fallbackStatus = (previousStatus != null && !previousStatus.isEmpty()) ? previousStatus : "";
                        model.addAttribute("previousStatus", fallbackStatus);
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("error", "Mã Technology Process phải là số.");
                    // Lấy tất cả TechnologyProcess của user đang đăng nhập
                    technologyProcessPage = technologyProcessService.getAllTechnologyProcessesByCreatedBy(pageable, currentUser);
                    model.addAttribute("previousStatus", previousStatus != null ? previousStatus : "");
                }
            }
            // Trường hợp mặc định: hiển thị tất cả TechnologyProcess của user đang đăng nhập
            else {
                technologyProcessPage = technologyProcessService.getAllTechnologyProcessesByCreatedBy(pageable, currentUser);
                model.addAttribute("previousStatus", "");
            }

            model.addAttribute("technologyProcesses", technologyProcessPage.getContent());
            model.addAttribute("technologyProcessPage", technologyProcessPage);
            model.addAttribute("search", search);
            return "technical/view-all-technology-process";
        }
        System.err.println("User chưa đăng nhập, chuyển hướng đến trang login.");
        return "redirect:/login";
    }

    @GetMapping("/technology-process/details/{id}")
    public String viewTechnologyProcessDetails(@PathVariable Long id, Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.getOptionalUser(model);

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                // Lấy thông tin user hiện tại
                String emailOrPhone = principal.getName();
                Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
                if (!optionalUser.isPresent()) {
                    System.err.println("Không tìm thấy User với email/phone: " + emailOrPhone + ", chuyển hướng đến trang login.");
                    return "redirect:/login";
                }
                User currentUser = optionalUser.get();

                // Lấy TechnologyProcess theo ID và kiểm tra createdBy
                TechnologyProcess technologyProcess = technologyProcessService.getTechnologyProcessByIdAndCreatedBy(id, currentUser);
                model.addAttribute("technologyProcess", technologyProcess);
                return "technical/technology-process-details";
            } catch (RuntimeException e) {
                model.addAttribute("error", "Không tìm thấy Technology Process với ID: " + id + " hoặc bạn không có quyền truy cập.");
                return "redirect:/view-all-technology-process";
            }
        }
        System.err.println("User chưa đăng nhập, chuyển hướng đến trang login.");
        return "redirect:/login";
    }

    private static class PageImplWrapper<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImplWrapper(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}
