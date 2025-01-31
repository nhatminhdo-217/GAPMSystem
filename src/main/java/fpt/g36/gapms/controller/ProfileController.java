package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
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


}
