package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.services.AccountService;
import fpt.g36.gapms.services.UserService;
import fpt.g36.gapms.services.impls.AccountServiceImpl;
import fpt.g36.gapms.services.impls.RoleServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final RoleServiceImpl roleService;  // Thêm roleService để lấy danh sách role từ DB
    private final RoleRepository roleRepository;
    private AccountServiceImpl accountServiceImpl;

    public AccountController(AccountService accountService, UserService userService, RoleServiceImpl roleService, RoleRepository roleRepository) {
        this.accountService = accountService;
        this.userService = userService;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/list-account")
    public String listAccount(
            @RequestParam(defaultValue = "0") int page, // Số trang (mặc định là 0)
            @RequestParam(defaultValue = "5") int size, // Kích thước trang (mặc định là 5)
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = accountService.getAccounts(pageable);

        model.addAttribute("account", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("size", size);
        return "home-page/account-management";
}


    @GetMapping("/account-detail/{id}")
    public String accountDetail(@PathVariable Long id, Model model) {
        User user = accountService.getUserById(id);
        List<Role> roles = roleRepository.findAll(); // Lấy danh sách role từ DB
        model.addAttribute("user", user);
        model.addAttribute("roles", roles); // Gửi danh sách role xuống view
        return "home-page/account-detail";
    }

    @GetMapping("/account-aaaa/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        User user = accountService.getUserById(id);
        model.addAttribute("user", user);  // Thêm dòng này để load thông tin lên form
        return "home-page/account-detail";
    }

    @PostMapping("/account-aaaa/{id}")
    public String updateAccount(@PathVariable Long id, @ModelAttribute("user") UserDTO userDTO) {


        userService.updateUser(id, userDTO);
        return "redirect:/admin/account-detail/" + id;  // Chuyển hướng lại trang chi tiết sau khi update
    }

    @PostMapping("/create")
    public ResponseEntity<User> createAccount(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.register(userDTO));
    }

}
