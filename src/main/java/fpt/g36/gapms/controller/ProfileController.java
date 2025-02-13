package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.ImageService;
import fpt.g36.gapms.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final ImageService imageService;

    public ProfileController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping
    public String getUserProfile(Model model, Principal principal) {
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
            return "profile";
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
        // Kiểm tra mật khẩu cũ và xác nhận mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "profile";
        }

        // Lấy thông tin người dùng hiện tại từ Principal
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        // Lấy thông tin của người dùng
        User currentUser = optionalUser.get();

        // Kiểm tra mật khẩu cũ
        if (!userService.checkPassword(oldPassword, currentUser.getPassword())) {
            model.addAttribute("error", "Mật khẩu cũ không đúng.");
            return "profile";
        }

        // Cập nhật mật khẩu mới
        userService.updatePassword(currentUser, newPassword);

        // Lấy lại đối tượng user sau khi cập nhật mật khẩu
        optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        currentUser = optionalUser.get();

        model.addAttribute("user", currentUser);
        model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
        model.addAttribute("success", "Mật khẩu đã được thay đổi thành công.");

        return "redirect:/profile";
    }


    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam(value = "username", required = false) String username,
                                @RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
                                Principal principal,
                                Model model) {
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Tài khoản không tồn tại.");
            return "profile";
        }

        User currentUser = optionalUser.get();

        //
        if (username != null && !username.isEmpty()) {
            currentUser.setUsername(username);
        }
        if (email != null && !email.isEmpty()) {
            currentUser.setEmail(email);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            currentUser.setPhoneNumber(phoneNumber);
        }

        //
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String fileName = imageService.saveImageMultiFile(avatarFile);
                currentUser.setAvatar(fileName);
            } catch (IOException e) {
                model.addAttribute("error", "Tải ảnh thất bại!");
                return "profile";
            }
        }

        userService.updateUser(currentUser);

        //
        model.addAttribute("user", currentUser);
        model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
        model.addAttribute("success", "Thay đổi thông tin cá nhân thành công!");

        return "profile";
    }
}
