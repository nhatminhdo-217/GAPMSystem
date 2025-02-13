package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
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
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // ✅ Kiểm tra nếu tài khoản bị khóa
        if (!user.isActive()) {
            throw new DisabledException("Tài khoản của bạn đã bị khóa!");
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


//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByEmailOrPhoneNumber(username, username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        if (!user.isActive()) {
//            throw new DisabledException("Tài khoản của bạn đã bị khóa!");
//        }
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmail(),
//                user.getPassword(),
//                getAuthorities(user)
//        );
//    }
//
//    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
//        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
//    }

}
