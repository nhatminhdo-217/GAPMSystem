package fpt.g36.gapms.utils;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.NotificationService;
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
    private final NotificationService notificationService;

    public UserUtils(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public void getOptionalUser(Model model) {
        // Get the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String emailOrPhone = authentication.getName();
            Optional<User> optionalUser = userService.findByEmailOrPhone(emailOrPhone, emailOrPhone);
            if (optionalUser.isPresent()) {
                model.addAttribute("user", optionalUser.get());
                String avatar = optionalUser.get().getAvatar();
                if (avatar != null && !avatar.startsWith("http")) {
                    model.addAttribute("avatar", "/uploads/" + optionalUser.get().getAvatar());
                } else {
                    System.err.println("Avatar URL: " + avatar);
                    model.addAttribute("avatar", avatar);
                }


                Long unreadNotifications = notificationService.countUnreadNotifications(optionalUser.get().getId());
                model.addAttribute("unreadNotifications", unreadNotifications);
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

                Long unreadNotifications = notificationService.countUnreadNotifications(optionalUser.get().getId());
                model.addAttribute("unreadNotifications", unreadNotifications);
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

    public String cleanSpaces(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }
}
