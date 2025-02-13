package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.MailService;
import fpt.g36.gapms.utils.PasswordUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ResetPasswordController {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtils passwordUtils;

    public ResetPasswordController(UserRepository userRepository, MailService mailService, PasswordEncoder passwordEncoder, PasswordUtils passwordUtils) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.passwordUtils = passwordUtils;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        return "authencation/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        //Check if email is existing
        User user = userRepository.findByEmail(email)
                .orElse(null);
        if (user == null) {
            model.addAttribute("emailError", "Không tìm thấy email.");
            return "authencation/forgot-password";
        }

        //Create token and save to session
        String token = String.valueOf((int) (Math.random() * 900000) + 100000);
        session.setAttribute("resetToken", token);
        session.setAttribute("resetUserEmail", email);

        //Set reset expire time
        session.setMaxInactiveInterval(60 * 15); //15 minutes

        model.addAttribute("emailSuccess", "Mã reset mật khẩu đã được gửi vào email của bạn.");
        mailService.sendResetPasswordMail(email, token);

        redirectAttributes.addFlashAttribute("success", true);

        return "redirect:forgot-password";
    }

    @PostMapping("/verify-code")
    public String verifyCode(@RequestParam String code, HttpSession session, Model model) {

        //Check session
        String token = (String) session.getAttribute("resetToken");
        String sessionEmail = (String) session.getAttribute("resetUserEmail");

        if (token == null || sessionEmail == null) {
            return "redirect:/forgot-password?error=invalid_session";
        }

        //Check code
        if (!code.equals(token)) {
            model.addAttribute("codeError", "Mã reset mật khẩu bị sai.");
            return "authencation/forgot-password";
        }

        model.addAttribute("codeSuccess", "Xác thực thành công.");

        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(HttpSession session, Model model) {

        // Get token and email from session
        String token = (String) session.getAttribute("resetToken");
        String email = (String) session.getAttribute("resetUserEmail");

        // Check if token and email are null
        if (token == null || email == null) {
            return "redirect:/forgot-password?error=invalid_session";
        }

        model.addAttribute("email", email);
        return "authencation/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String password, HttpSession session, Model model) {

        //Check session
        String token = (String) session.getAttribute("resetToken");
        String sessionEmail = (String) session.getAttribute("resetUserEmail");

        if (token == null || sessionEmail == null) {
            return "redirect:/forgot-password?error=invalid_session";
        }

        //Check password meet the requirement
        if (!passwordUtils.isPasswordValid(password)) {
            model.addAttribute("passwordError", "Mật khẩu không đạt yêu cầu.");
            return "authencation/reset-password";
        }

        User user = userRepository.findByEmail(sessionEmail)
                .orElse(null);
        if (user == null) {
            return "redirect:/forgot-password?error=user_not_found";
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        model.addAttribute("passwordSuccess", "Mật khẩu đã được reset thành công. Vui lòng đăng nhập.");

        //Remove session
        session.removeAttribute("resetToken");
        session.removeAttribute("resetUserEmail");

        return "redirect:/authencation/login";
    }
}
