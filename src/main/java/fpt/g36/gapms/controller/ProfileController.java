package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.CompanyDTO;
import fpt.g36.gapms.models.dto.UpdateProfileDTO;
import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.CompanyService;
import fpt.g36.gapms.services.ImageService;
import fpt.g36.gapms.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final ImageService imageService;
    private final CompanyService companyService;

    public ProfileController(UserService userService, ImageService imageService, CompanyService companyService) {
        this.userService = userService;
        this.imageService = imageService;
        this.companyService = companyService;
    }

    @GetMapping
    public String getUserProfile(Model model, Principal principal) {
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isPresent()) {
            // User
            model.addAttribute("user", optionalUser.get());
            model.addAttribute("username", optionalUser.get().getUsername());
            model.addAttribute("avatar",optionalUser.get().getAvatar());
            //Company
            Optional<Company> optionalCompany = companyService.findByUserId(optionalUser.get().getId());

            if (optionalCompany.isPresent()) {
                model.addAttribute("company", optionalCompany.get());

                return "profile";
            } else {
                model.addAttribute("company", null);
                return "profile";
            }
        } else {
            return "redirect:/error";
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản không tồn tại.");
            return "redirect:/profile";
        }

        User currentUser = optionalUser.get();
        Optional<Company> optionalCompany = companyService.findByUserId(currentUser.getId());

        // Kiểm tra mật khẩu cũ và xác nhận mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "redirect:/profile";
        }

        // Kiểm tra mật khẩu cũ
        if (!userService.checkPassword(oldPassword, currentUser.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu cũ không đúng.");
            return "redirect:/profile";
        }

        // Cập nhật mật khẩu mới
        userService.updatePassword(currentUser, newPassword);
        redirectAttributes.addFlashAttribute("success", "Mật khẩu đã được thay đổi thành công.");
        return "redirect:/profile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(
            @Valid @ModelAttribute("user") UpdateProfileDTO updateProfileDTO,
            BindingResult result,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản không tồn tại.");
            return "redirect:/profile";
        }

        User currentUser = optionalUser.get();
        Optional<Company> optionalCompany = companyService.findByUserId(currentUser.getId());

        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("validationErrors", result.getAllErrors());
            return "redirect:/profile";
        }

        // Kiểm tra email đã tồn tại
        if (!updateProfileDTO.getEmail().isEmpty()) {
            Optional<User> userByEmail = userService.findByEmailOrPhone(updateProfileDTO.getEmail(), updateProfileDTO.getEmail());
            if (userByEmail.isPresent() && !userByEmail.get().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng bởi tài khoản khác.");
                return "redirect:/profile";
            }
        }

        // Kiểm tra số điện thoại đã tồn tại
        if (!updateProfileDTO.getPhoneNumber().isEmpty()) {
            Optional<User> userByPhone = userService.findByEmailOrPhone(updateProfileDTO.getPhoneNumber(), updateProfileDTO.getPhoneNumber());
            if (userByPhone.isPresent() && !userByPhone.get().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "Số điện thoại đã được sử dụng bởi tài khoản khác.");
                return "redirect:/profile";
            }
        }

        // Xử lý avatar
        if (updateProfileDTO.getAvatarFile() != null && !updateProfileDTO.getAvatarFile().isEmpty()) {
            try {
                String fileName = imageService.saveImageMultiFile(updateProfileDTO.getAvatarFile());
                updateProfileDTO.setAvatar(fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Tải ảnh thất bại!");
                return "redirect:/profile";
            }
        } else {
            updateProfileDTO.setAvatar(currentUser.getAvatar());
        }

        // Cập nhật thông tin người dùng
        userService.updatePersonalUser(currentUser.getId(), updateProfileDTO);

        // Truy vấn lại User
        String newEmailOrPhone = updateProfileDTO.getEmail().isEmpty() ?
                updateProfileDTO.getPhoneNumber() : updateProfileDTO.getEmail();
        Optional<User> updatedUserOpt = userService.findByEmailOrPhone(newEmailOrPhone, newEmailOrPhone);

        if (updatedUserOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật thành công nhưng không tìm thấy tài khoản. Vui lòng đăng nhập lại.");
            return "redirect:/profile";
        }

        // Cập nhật Security Context
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newEmailOrPhone,
                authentication.getCredentials(),
                authentication.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectAttributes.addFlashAttribute("success", "Thay đổi thông tin cá nhân thành công!");
        return "redirect:/profile";
    }

    @PostMapping("/addCompany")
    public String addCompany(
            @Valid @ModelAttribute("company") CompanyDTO companyDTO,
            BindingResult result,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorCompany", "Tài khoản không tồn tại.");
            return "redirect:/profile";
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("validationErrorsCompany", "Kiểm tra lại thông tin công ty");
            return "redirect:/profile";
        }

        try {
            User currentUser = optionalUser.get();
            Company company = companyService.addCompany(currentUser.getId(), companyDTO);
            redirectAttributes.addFlashAttribute("successCompany", "Thêm thông tin công ty thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorCompany", "Runtime Error: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCompany", "Unexpected Error: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    @PostMapping("/updateCompany")
    public String updateCompany(
            @Valid @ModelAttribute("company") CompanyDTO companyDTO,
            BindingResult result,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorCompany", "Tài khoản không tồn tại.");
            return "redirect:/profile";
        }

        User currentUser = optionalUser.get();
        Optional<Company> optionalCompany = companyService.findByUserId(currentUser.getId());

        if (optionalCompany.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorCompany", "Không tìm thấy công ty để cập nhật.");
            return "redirect:/profile";
        }

        // Kiểm tra lỗi validation trước khi cập nhật
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("validationErrorsCompany", "Kiểm tra lại thông tin công ty");
            return "redirect:/profile";
        }

        try {
            Company updatedCompany = companyService.updateCompany(currentUser.getId(), companyDTO);
            redirectAttributes.addFlashAttribute("successCompany", "Cập nhật thông tin công ty thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorCompany", "Runtime Error: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCompany", "Unexpected Error: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}