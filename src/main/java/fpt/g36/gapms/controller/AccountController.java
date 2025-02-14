package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.services.AccountService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.services.impls.RoleServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final RoleServiceImpl roleService;
    private final RoleRepository roleRepository;

    public AccountController(
            AccountService accountService,
            UserService userService,
            RoleServiceImpl roleService,
            RoleRepository roleRepository) {
        this.accountService = accountService;
        this.userService = userService;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
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

