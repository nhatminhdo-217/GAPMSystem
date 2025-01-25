package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.dto.VerificationCode;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class RegisterController {

    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    @Autowired
    public RegisterController(JavaMailSender mailSender, PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    private static final Map<String, VerificationCode> verificationCode = new ConcurrentHashMap<>();
    private static final int EXPIRED_TIME = 5;

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
    @GetMapping("/register")
    public String registerForm(Model model){
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegisterForm(@Valid UserDTO user, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            System.out.println("Error");
            return "register";
        }

        //Check re-password
        if (!user.getPassword().equals(user.getRePassword())){
            model.addAttribute("passwordError", "Re-password does not match");
            System.out.println("Password not match");
            return "register";
        }

        //Check unique username
        if (userRepository.existsByUsername(user.getUsername())){
            model.addAttribute("usernameError", "Username already exists");
            System.out.println("Username exists");
            return "register";
        }

        //Check unique email
        if (userRepository.existsByEmail(user.getEmail())){
            model.addAttribute("emailError", "Email already exists");
            System.out.println("Email exists");
            return "register";
        }

        //Check unique phone number
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())){
            model.addAttribute("phoneError", "Phone number already exists");
            System.out.println("Phone exists");
            return "register";
        }

        //Create verification code with expired time
        //Generate random 6-digit code
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        verificationCode.put(user.getEmail(), new VerificationCode(code, EXPIRED_TIME));
        System.out.println("Code: " + code + " - Expired in: " + EXPIRED_TIME);

        try {
            //Send email
            sendVerifyEmail(user.getEmail(), code);
            System.out.println("Email sent to " + user.getEmail());
        } catch (Exception e){
            model.addAttribute("error", "Failed to send email");
            return "register";
        }

        User newUser = userService.register(user);
        if (newUser != null){
            model.addAttribute("registerSuccess", "Register successfully.");
        }

        redirectAttributes.addFlashAttribute("emailFlash", user.getEmail());

        return "redirect:/verify";
    }

    @GetMapping("/verify")
    public String showVerifyPage(Model model) {
        //Check flash attribute "email" is existing
        if (!model.containsAttribute("emailFlash")) {
            System.out.println("Email not found");
            return "redirect:/register";
        }

        return "verify";
    }

    @PostMapping("/verify")
    public String processVerifyForm(RedirectAttributes redirectAttributes, @RequestParam String code, Model model){
        //Get email from flash attribute
        String email = (String) redirectAttributes.getFlashAttributes().get("emailFlash");
        VerificationCode verifyCode = verificationCode.get(email);

        // Check if code is expired
        if (verifyCode == null || verifyCode.isExpired()){
            model.addAttribute("verifyError", "Code has expired. Please request a new one.");
            model.addAttribute("email", email);
            return "verify";
        }

        // Check if code is correct
        if (!verifyCode.getCode().equals(code)){
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

        return "redirect:/login?verified=true";
    }

    @PostMapping("/resend")
    public String resendVerifyCode(@RequestParam String email, Model model){

        // Resend verification code
        String newCode = String.valueOf((int) (Math.random() * 9000) + 1000);
        verificationCode.put(email, new VerificationCode(newCode, EXPIRED_TIME));

        try{
            sendVerifyEmail(email, newCode);
            model.addAttribute("success-msg", "New verification code has been sent");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to resend code. Try again.");
        }

        model.addAttribute("email", email);
        return "redirect:/verify";
    }

    private void sendVerifyEmail(String email, String code) {
        //Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify your email");
        message.setText("Your verification code is: " + code + ". It will be expired in " + EXPIRED_TIME + " minutes");
        mailSender.send(message);
    }
}
