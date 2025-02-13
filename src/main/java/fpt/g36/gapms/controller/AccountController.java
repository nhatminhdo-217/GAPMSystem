package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.services.AccountService;
import fpt.g36.gapms.services.MailService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.services.impls.AccountServiceImpl;
import fpt.g36.gapms.services.impls.RoleServiceImpl;
import fpt.g36.gapms.utils.PasswordGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;
import static fpt.g36.gapms.utils.PasswordGenerator.generateRandomPassword;

@Controller
@RequestMapping("/admin")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final RoleServiceImpl roleService;
    private final RoleRepository roleRepository;
    private AccountServiceImpl accountServiceImpl;
    private final MailService emailService;

    public AccountController(
            AccountService accountService,
            UserService userService,
            RoleServiceImpl roleService,
            RoleRepository roleRepository,
            MailService emailService
    ) {
        this.accountService = accountService;
        this.userService = userService;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
    }

    @GetMapping("/list_account")
    public String listAccount(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        model.addAttribute("user", new User());

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = accountService.getAccounts(pageable);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            String currentUserName = authentication.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(currentUserName, currentUserName);

            if (optionalUser.isPresent()) {
                User currentUser = optionalUser.get();
                model.addAttribute("username", currentUser.getUsername());
                model.addAttribute("email", currentUser.getEmail());
                model.addAttribute("role", currentUser.getRole().getName());
                model.addAttribute("avatar", "/uploads/" + currentUser.getAvatar());
                model.addAttribute("account", users);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", users.getTotalPages());
                model.addAttribute("size", size);
                System.out.println(currentUser);
            }
        }
        return "home-page/account_management";
    }

    @GetMapping("/account_detail/{id}")
    public String accountDetail(@PathVariable Long id, Model model) {
        User user = accountService.getUserById(id);
        List<Role> roles = roleRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);

        return "home-page/account_detail";
    }

    @GetMapping("/account_showUpdate/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        User user = accountService.getUserById(id);
        model.addAttribute("user", user);
        return "home-page/account_detail";
    }

    @PostMapping("/account_update/{id}")
    public String updateAccount(@PathVariable Long id, @ModelAttribute("user") UserDTO userDTO) {
        userService.updateUser(id, userDTO);
        return "redirect:/admin/account_detail/" + id;
    }


    @PostMapping("/create_account")
    public String createAccount(@ModelAttribute("user") User user, Model model, RedirectAttributes redirectAttributes) {
        if (accountService.existsByEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("emailError", "Email đã tồn tại!");
            return "redirect:/admin/list_account";
        }

        if (accountService.existsByPhoneNumber(user.getPhoneNumber())) {
            redirectAttributes.addFlashAttribute("phoneError", "Số điện thoại đã tồn tại!");
            return "redirect:/admin/list_account";
        }

        String randomPassword = PasswordGenerator.generateRandomPassword(8);
        accountService.createAccount(user, randomPassword);
        emailService.sendPasswordEmail(user.getEmail(), randomPassword);
        redirectAttributes.addFlashAttribute("message", "Tạo tài khoản thành công!");
        return "redirect:/admin/list_account";
    }

    @GetMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(id);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/admin/list_account";
        }

        boolean newStatus = !user.isActive(); // Đảo trạng thái active/inactive
        userService.updateUserStatus(id, newStatus);

        redirectAttributes.addFlashAttribute("message", "Trạng thái tài khoản đã thay đổi!");
        return "redirect:/admin/list_account"; // Quay lại trang danh sách
    }

    @GetMapping("/account_view/{id}")
    public String accountView(@PathVariable Long id, Model model) {
        User user = accountService.getUserById(id);
        List<Role> roles = roleRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);

        return "home-page/view_account_detail"; // Trả về file riêng
    }
}
