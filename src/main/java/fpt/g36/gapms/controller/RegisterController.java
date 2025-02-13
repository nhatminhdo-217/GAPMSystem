package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.dto.VerificationCode;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.MailService;
import fpt.g36.gapms.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Controller

public class RegisterController {

    private static final Map<String, VerificationCode> verificationCode = new ConcurrentHashMap<>();
    private static final int EXPIRED_TIME = 5;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final MailService mailService;
  
    @Autowired
    public RegisterController(JavaMailSender mailSender, PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, UserService userService, MailService mailService) {
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    //Check if DB don't have any user, create a default admin
    @PostConstruct
    public void initDefaultData() {
        // Tạo role trước
        initDefaultRoles();
        // Tạo admin sau (đảm bảo role đã tồn tại)
        initDefaultAdmin();
    }

    private void initDefaultRoles() {
        if (roleRepository.count() == 0) {
            createRoleIfNotFound("ADMIN", "Admin role");
            createRoleIfNotFound("USER", "User role");
        }
    }

    private void initDefaultAdmin() {
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setEmail("admin@example.com");
            admin.setPhoneNumber("+84123456789");
            admin.setRole(adminRole);
            admin.setVerified(true); // Thêm trạng thái verified nếu cần

            userRepository.save(admin); // Thêm dòng này để lưu vào DB
        }
    }

    private void createRoleIfNotFound(String name, String description) {
        if (!roleRepository.existsByName(name)) {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            roleRepository.save(role);
        }
    }


    @GetMapping("/login_form")
    public String viewLogin() {
        return "authencation/login";
    }

    @GetMapping("/login-error")
    public String loginError(HttpSession session, Model model) {
        model.addAttribute("loginError", true);

        // Lấy dữ liệu từ Session
        Object phoneNumberOrEmail = session.getAttribute("phoneNumberOrEmail");
        model.addAttribute("phoneNumberOrEmail", phoneNumberOrEmail);

        return "authencation/login";
    }


    @GetMapping("/home_page")
    public String viewHomePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            String currentUserName = authentication.getName();

            // Tìm user theo email hoặc số điện thoại
            Optional<User> optionalUser = userService.findByEmailOrPhone(currentUserName, currentUserName);

            // Nếu tìm thấy user, thêm vào model
            if (optionalUser.isPresent()) {
                model.addAttribute("username", optionalUser.get().getUsername());
                model.addAttribute("email", optionalUser.get().getEmail());
                model.addAttribute("role", optionalUser.get().getRole().getName());
                model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
                System.out.println(optionalUser.get());
            }

        }

        return "home-page/home-page";
    }


    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegisterForm(@Valid @ModelAttribute("user") UserDTO user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }

        //Check re-password
        if (!user.getPassword().equals(user.getRePassword())) {
            model.addAttribute("passwordError", "Re-password does not match");
            return "register";
        }

        //Check unique username
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("usernameError", "Username already exists");
            return "register";
        }

        //Check unique email
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Email already exists");
            return "register";
        }

        //Check unique phone number
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            model.addAttribute("phoneError", "Phone number already exists");
            return "register";
        }

        //Create verification code with expired time
        //Generate random 6-digit code
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        verificationCode.put(user.getEmail(), new VerificationCode(code, EXPIRED_TIME));

        try {
            //Send email
            mailService.sendVerifyMail(user.getEmail(), code, EXPIRED_TIME);
        } catch (Exception e){
            model.addAttribute("error", "Failed to send email");
            return "register";
        }

        User newUser = userService.register(user);
        if (newUser != null) {
            model.addAttribute("registerSuccess", "Register successfully.");
        }

        redirectAttributes.addFlashAttribute("emailFlash", user.getEmail());

        return "redirect:/verify";
    }

    @GetMapping("/verify")
    public String showVerifyPage(Model model) {
        //Check flash attribute "email" is existing
        if (!model.containsAttribute("emailFlash")) {
            return "redirect:/register";
        }

        return "verify";
    }

    @PostMapping("/verify")
    public String processVerifyForm(RedirectAttributes redirectAttributes, @RequestParam String code, Model model) {
        //Get email from flash attribute
        String email = (String) redirectAttributes.getFlashAttributes().get("emailFlash");
        VerificationCode verifyCode = verificationCode.get(email);

        // Check if code is expired
        if (verifyCode == null || verifyCode.isExpired()) {
            model.addAttribute("verifyError", "Code has expired. Please request a new one.");
            model.addAttribute("email", email);
            return "verify";
        }

        // Check if code is correct
        if (!verifyCode.getCode().equals(code)) {
            model.addAttribute("verifyError", "Invalid verify code");
            model.addAttribute("email", email);
            return "verify";
        }

        //Change user status to verified
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerified(true);
        userRepository.save(user);
        model.addAttribute("verifySuccess", "Email verified successfully");

        //Remove verification code
        verificationCode.remove(email);

        return "redirect:/login_form";
    }

    @PostMapping("/resend")
    public String resendVerifyCode(@RequestParam String email, Model model) {

        // Resend verification code
        String newCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        verificationCode.put(email, new VerificationCode(newCode, EXPIRED_TIME));

        try {
            mailService.sendVerifyMail(email, newCode, EXPIRED_TIME);
            model.addAttribute("success-msg", "New verification code has been sent");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to resend code. Try again.");
        }

        model.addAttribute("email", email);
        return "redirect:/verify";
    }

    @GetMapping("/user_logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Xóa session nếu có
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Xóa cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue(null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

        // Xóa thông tin xác thực của Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        // Chuyển hướng đến trang đăng nhập
        return "redirect:/login_form";
    }
}
