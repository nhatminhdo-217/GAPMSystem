package fpt.g36.gapms.utils;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Optional;

@Component
public class UserUtils {
    private final UserService userService;

    public UserUtils(UserService userService) {
        this.userService = userService;
    }

    public void getOptionalUser(Model model) {
        // Get the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (optionalUser.isPresent()) {
                model.addAttribute("user", optionalUser.get());
                model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
            }
        }
    }

    public User getOptionalUserInfo(Model model) {
        // Get the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (optionalUser.isPresent()) {
                model.addAttribute("user", optionalUser.get());
                model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
                return optionalUser.get();
            }
        }
        return null;
    }

    public User getOptionalUserInfo() {
        // Get the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (optionalUser.isPresent()) {
                return optionalUser.get();
            }
        }
        return null;
    }
}
