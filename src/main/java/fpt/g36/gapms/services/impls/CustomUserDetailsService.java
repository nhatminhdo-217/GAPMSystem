package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumberOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrPhoneNumber(phoneNumberOrEmail, phoneNumberOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));

        // ✅ Kiểm tra nếu tài khoản bị khóa (status = false)
        if (!user.isActive()) {

                throw new DisabledException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");

        }
        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(),
                user.getPassword(),
                getAuthorities(user.getRole())
        );
    }

    private List<SimpleGrantedAuthority> getAuthorities(Role role) {
        String roleName = "ROLE_" + role.getName().toUpperCase();
        System.out.println("User Role: " + role.getName());
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }
}
