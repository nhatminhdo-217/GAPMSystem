package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.AccountService;
import fpt.g36.gapms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/list-account")
    public String listAccount(Model model) {
        List<User> users = accountService.getAccounts();
        model.addAttribute("account", users);
        return "home-page/account-management";
    }
    @GetMapping("/account-detail/{id}")
    public String accountDetail(@PathVariable Long id, Model model) {
        User user = accountService.getUserById(id);
        model.addAttribute("user", user);
        return "home-page/account-detail";
    }
}
