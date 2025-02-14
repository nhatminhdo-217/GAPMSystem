package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.services.AccountService;
import org.springframework.data.domain.PageImpl;
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
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        List<User> allUsers;

        allUsers = accountService.getAllAccountExcept();

        if (search != null && !search.isEmpty()) {
            allUsers = accountService.searchAccountsWithoutPaging(search); // Lấy tất cả user tìm kiếm
        } else {
            allUsers = accountService.getAllAccountExcept(); // Lấy toàn bộ user trừ tài khoản admin đang login
        }

        // Debug để kiểm tra Role
        System.out.println("DEBUG: Role filter value = " + role);

        // Lọc theo Role nếu có giá trị
        if (role != null && !role.isEmpty()) {
            allUsers = allUsers.stream()
                    .filter(user -> user.getRole().getName().equalsIgnoreCase(role))
                    .toList();
        }

        // Phân trang lại sau khi lọc
        int start = Math.min((int) pageable.getOffset(), allUsers.size());
        int end = Math.min(start + pageable.getPageSize(), allUsers.size());
        List<User> paginatedUsers = allUsers.subList(start, end);
        Page<User> users = new PageImpl<>(paginatedUsers, pageable, allUsers.size());

        model.addAttribute("account", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages() > 0 ? users.getTotalPages() : 1);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("role", role);
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
    public String showUpdateForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        User user = accountService.getUserById(id);
        model.addAttribute("user", user);
        return "home-page/account_detail";
    }

    @PostMapping("/account_update/{id}")
    public String updateAccount(@PathVariable Long id,
                                @ModelAttribute("user") UserDTO userDTO,
                                RedirectAttributes redirectAttributes) {
        userService.updateUser(id, userDTO);

        // Thêm thông báo thành công vào RedirectAttributes
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tài khoản thành công!");
        return "redirect:/admin/account_detail/" + id;
    }

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
