package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.CreateAccountDTO;
import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.services.AccountService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.data.domain.PageImpl;
import fpt.g36.gapms.services.MailService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.services.impls.AccountServiceImpl;
import fpt.g36.gapms.services.impls.RoleServiceImpl;
import fpt.g36.gapms.utils.PasswordGenerator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final RoleServiceImpl roleService;
    private final RoleRepository roleRepository;
    private AccountServiceImpl accountServiceImpl;
    private final MailService emailService;
    private final UserUtils userUtils;

    public AccountController(
            AccountService accountService,
            UserService userService,
            RoleServiceImpl roleService,
            RoleRepository roleRepository,
            MailService emailService, UserUtils userUtils) {
        this.accountService = accountService;
        this.userService = userService;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.userUtils = userUtils;
    }

    @GetMapping("/list_account")
    public String listAccount(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            Model model,
            Principal principal) {
        List<Role> roles = roleRepository.findAll();
        String emailOrPhone = principal.getName();
        Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        //
        model.addAttribute("user", optionalUser.get());
        model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
        //
        model.addAttribute("roles", roles);
        model.addAttribute("users", new User());
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

    @GetMapping("/account_create")
    public String showCreateAccountForm(Model model, @RequestParam(value = "success", required = false) boolean success) {

        userUtils.getOptionalUser(model);

        model.addAttribute("userDTO", new CreateAccountDTO());
        model.addAttribute("roles", roleService.getAllRoles());

        // Kiểm tra xem có thông báo tạo tài khoản thành công không
        if (success) {
            model.addAttribute("successMessage", "Tạo tài khoản thành công!");
        }

        return "home-page/account_create";
    }

    @PostMapping("/account_create")
    public String createAccount(@Valid @ModelAttribute("userDTO") CreateAccountDTO createAccountDTO,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            userUtils.getOptionalUser(model);
            model.addAttribute("roles", roleService.getAllRoles());
            return "home-page/account_create";
        }

        boolean hasDuplicateError = false;

        if (accountService.existsByEmail(createAccountDTO.getEmail())) {
            result.rejectValue("email", "error.email", "Email này đã được sử dụng!");
            hasDuplicateError = true;
        }
        if (accountService.existsByPhoneNumber(createAccountDTO.getPhoneNumber())) {
            result.rejectValue("phoneNumber", "error.phoneNumber", "Số điện thoại này đã được sử dụng!");
            hasDuplicateError = true;
        }
        if (hasDuplicateError) {
            userUtils.getOptionalUser(model);
            model.addAttribute("roles", roleService.getAllRoles());
            return "home-page/account_create";
        }
        String randomPassword = PasswordGenerator.generateRandomPassword(8);
        try {
            User newUser = accountService.createAccount(createAccountDTO, randomPassword);
            if (newUser != null) {
                emailService.sendPasswordEmail(createAccountDTO.getEmail(), randomPassword);
                model.addAttribute("roles", roleService.getAllRoles());
                return "redirect:/admin/account_create?success=true";
            } else {
                model.addAttribute("error", "Lỗi khi lưu tài khoản vào database!");
                model.addAttribute("roles", roleService.getAllRoles());
                return "home-page/account_create";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tạo tài khoản: " + e.getMessage());
            return "home-page/account_create";
        }
    }

    @GetMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(id);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/admin/list_account";
        }
        boolean newStatus = !user.isActive();
        userService.updateUserStatus(id, newStatus);
        redirectAttributes.addFlashAttribute("message", "Trạng thái tài khoản đã thay đổi!");
        return "redirect:/admin/list_account";
    }

    @GetMapping("/account_view/{id}")
    public String accountView(@PathVariable Long id, Model model) {
        User user = accountService.getUserById(id);
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);

        return "home-page/view_account_detail";
    }
}
