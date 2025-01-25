package fpt.g36.gapms.config;

import fpt.g36.gapms.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Import(PasswordConfig.class)
public class SecurityConfig {

    private final UserDetailsService userService;

    public SecurityConfig(UserDetailsService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //Authorize requests
                .authorizeHttpRequests(req -> req.requestMatchers("/home", "/register", "/verify", "/resend").permitAll())
                .authorizeHttpRequests(req -> req.requestMatchers("/admin/**").hasAnyRole("ADMIN"))
                .authorizeHttpRequests(req -> req.anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").permitAll())
                .userDetailsService(userService);

        return httpSecurity.build();
    }
}
