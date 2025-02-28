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
            //User
            model.addAttribute("user", optionalUser.get());
            model.addAttribute("username", optionalUser.get().getUsername());
            model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
            //Company
            Optional<Company> optionalCompany = companyService.findByUserId(optionalUser.get().getId());

            if (optionalCompany.isPresent()) {
                model.addAttribute("company", optionalCompany.get());
                System.err.println("Find company" + optionalCompany.get());
                return "profile";
            } else {
                model.addAttribute("company", null);
                System.err.println("Find company" + null);
                return "profile";
            }
        } else {
            // Nếu không tìm thấy người dùng, có thể xử lý chuyển hướng hoặc thông báo lỗi
            return "redirect:/error";
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal,
                                 Model model) {
        // Lấy thông tin người dùng hiện tại từ Principal
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        // Lấy thông tin của người dùng
        User currentUser = optionalUser.get();
        Optional<Company> optionalCompany = companyService.findByUserId(currentUser.getId());

        // Kiểm tra mật khẩu cũ và xác nhận mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            model.addAttribute("user", currentUser);
            model.addAttribute("company", optionalCompany.get());
            model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
            return "profile";
        }

        // Kiểm tra mật khẩu cũ
        if (!userService.checkPassword(oldPassword, currentUser.getPassword())) {
            model.addAttribute("error", "Mật khẩu cũ không đúng.");
            model.addAttribute("user", currentUser);
            model.addAttribute("company", optionalCompany.get());
            model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
            return "profile";
        }

        // Cập nhật mật khẩu mới
        userService.updatePassword(currentUser, newPassword);

        // Lấy lại đối tượng user sau khi cập nhật mật khẩu
        optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        currentUser = optionalUser.get();

        model.addAttribute("user", currentUser);
        model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
        model.addAttribute("company", optionalCompany.get());
        model.addAttribute("success", "Mật khẩu đã được thay đổi thành công.");

        return "/profile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@Valid @ModelAttribute("user") UpdateProfileDTO updateProfileDTO,
                                BindingResult result,
                                Principal principal,
                                Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Tài khoản không tồn tại.");
            return "profile";
        }

        User currentUser = optionalUser.get();
        Optional<Company> optionalCompany = companyService.findByUserId(currentUser.getId());

        // Nếu có lỗi validation, hiển thị chi tiết lỗi
        if (result.hasErrors()) {
            model.addAttribute("user", currentUser);
            model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
            model.addAttribute("company", optionalCompany.get());
            model.addAttribute("validationErrors", result.getAllErrors());
            return "profile";
        }

        // Kiểm tra email đã tồn tại
        if (!updateProfileDTO.getEmail().isEmpty()) {
            Optional<User> userByEmail = userService.findByEmailOrPhone
                    (updateProfileDTO.getEmail(), updateProfileDTO.getEmail());
            if (userByEmail.isPresent() && !userByEmail.get().getId().equals(currentUser.getId())) {
                model.addAttribute("user", currentUser);
                model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
                model.addAttribute("company", optionalCompany.get());
                model.addAttribute("error", "Email đã được sử dụng bởi tài khoản khác.");
                return "profile";
            }
        }

        // Kiểm tra số điện thoại đã tồn tại
        if (!updateProfileDTO.getPhoneNumber().isEmpty()) {
            Optional<User> userByPhone = userService.findByEmailOrPhone
                    (updateProfileDTO.getPhoneNumber(), updateProfileDTO.getPhoneNumber());
            if (userByPhone.isPresent() && !userByPhone.get().getId().equals(currentUser.getId())) {
                model.addAttribute("user", currentUser);
                model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
                model.addAttribute("company", optionalCompany.get());
                model.addAttribute("error", "Số điện thoại đã được sử dụng bởi tài khoản khác.");
                return "profile";
            }
        }

        //Xử lý avatar (sử dụng avatarFile thay vì avatar)
        if (updateProfileDTO.getAvatarFile() != null && !updateProfileDTO.getAvatarFile().isEmpty()) {
            try {
                String fileName = imageService.saveImageMultiFile(updateProfileDTO.getAvatarFile());
                updateProfileDTO.setAvatar(fileName);
            } catch (IOException e) {
                model.addAttribute("error", "Tải ảnh thất bại!");
                return "profile";
            }
        } else {
            updateProfileDTO.setAvatar(currentUser.getAvatar());
        }

        // Cập nhật thông tin người dùng
        userService.updatePersonalUser(currentUser.getId(), updateProfileDTO);

        //Truy vấn lại User bằng `findByEmailOrPhone`
        String newEmailOrPhone = updateProfileDTO.getEmail().isEmpty() ?
                updateProfileDTO.getPhoneNumber() : updateProfileDTO.getEmail();
        Optional<User> updatedUserOpt = userService.findByEmailOrPhone(newEmailOrPhone, newEmailOrPhone);

        if (updatedUserOpt.isEmpty()) {
            model.addAttribute("error", "Cập nhật thành công nhưng không tìm thấy tài khoản. " +
                    "Vui lòng đăng nhập lại.");
            return "profile";
        }

        User updatedUser = updatedUserOpt.get();

        //Cập nhật lại Security Context
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newEmailOrPhone, // Dùng email hoặc số điện thoại mới (nếu có thay đổi)
                authentication.getCredentials(),
                authentication.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        model.addAttribute("user", updatedUser);
        model.addAttribute("avatar", "/uploads/" + updatedUser.getAvatar());
        model.addAttribute("company", optionalCompany.get());
        model.addAttribute("success", "Thay đổi thông tin cá nhân thành công!");

        return "profile";
    }

    @PostMapping("/addCompany")
    public String addCompany(@Valid @ModelAttribute("company") CompanyDTO companyDTO,
                             BindingResult result,
                             Principal principal,
                             Model model) {
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            model.addAttribute("errorCompany", "Tài khoản không tồn tại.");
            return "profile";
        }

        User currentUser = optionalUser.get();

        if (result.hasErrors()) {
            model.addAttribute("user", currentUser);
            model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
            model.addAttribute("validationErrorsCompany", result.getAllErrors());
            return "profile";
        }

        try {
            Company company = companyService.addCompany(currentUser.getId(), companyDTO);
            model.addAttribute("user", currentUser);
            model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
            model.addAttribute("company", company);
            model.addAttribute("successCompany", "Thêm thông tin công ty thành công!");
        } catch (RuntimeException e) {
            model.addAttribute("user", currentUser);
            model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
            model.addAttribute("errorCompany", "Runtime Error: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("user", currentUser);
            model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
            model.addAttribute("errorCompany", "Unexpected Error: " + e.getMessage());
        }
        return "profile";
    }

    @PostMapping("/updateCompany")
    public String updateCompany(@Valid @ModelAttribute("company") CompanyDTO companyDTO,
                                BindingResult result,
                                Principal principal,
                                Model model) {
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            model.addAttribute("errorCompany", "Tài khoản không tồn tại.");
            return "profile";
        }

        User currentUser = optionalUser.get();
        Optional<Company> optionalCompany = companyService.findByUserId(currentUser.getId());

        if (optionalCompany.isEmpty()) {
            model.addAttribute("errorCompany", "Không tìm thấy công ty để cập nhật.");
            return "profile";
        }

        Company currentCompany = optionalCompany.get();

        // Kiểm tra lỗi validation trước khi cập nhật
        if (result.hasErrors()) {
            model.addAttribute("user", currentUser);
            model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
            model.addAttribute("company", currentCompany); // Trả về thông tin công ty cũ
            model.addAttribute("validationErrorsCompany", result.getAllErrors());
            return "profile";
        }

        try {
            Company updatedCompany = companyService.updateCompany(currentUser.getId(), companyDTO);
            model.addAttribute("user", currentUser);
            model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
            model.addAttribute("company", updatedCompany);
            model.addAttribute("successCompany", "Cập nhật thông tin công ty thành công!");
        } catch (RuntimeException e) {
            model.addAttribute("user", currentUser);
            model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
            model.addAttribute("company", currentCompany);
            model.addAttribute("errorCompany", "Runtime Error: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("user", currentUser);
            model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
            model.addAttribute("company", currentCompany);
            model.addAttribute("errorCompany", "Unexpected Error: " + e.getMessage());
        }
        return "profile";
    }
}
