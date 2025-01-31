package fpt.g36.gapms.config;

import fpt.g36.gapms.services.impls.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Import(PasswordConfig.class)
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/home", "/register", "/verify", "/resend", "/home_page","/home_page_2"
                                , "/assert/**", "/login_form"
                                , "/forgot-password", "/reset-password", "/verify-code").permitAll() // Cho phép truy cập trang login
                        .requestMatchers("/test/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login_form")
                        .loginProcessingUrl("/login")
                        .failureUrl("/login-error").permitAll()
                        .usernameParameter("phoneNumber")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler()) // Xử lý chuyển hướng sau khi đăng nhập
                )
                .oauth2Login(oauth2 -> oauth2

                        .successHandler(customAuthenticationSuccessHandler()) // Xử lý chuyển hướng sau khi đăng nhập bằng OAuth2
                ).logout(logout -> logout
                        .logoutSuccessUrl("/login_form") // Sau khi logout, quay về trang login
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember")
                        .permitAll()
                ).rememberMe(rememberMe -> rememberMe
                        .key("mySecretKey")  // Key để mã hóa token (phải cố định)
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 ngày
                        .rememberMeParameter("remember") // Tên tham số trên form
                        .alwaysRemember(false))
        ;

        return http.build();
    }


    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();
            String redirectUrl = request.getContextPath() + "/";


            if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                redirectUrl = request.getContextPath() + "/home_page";
            } else if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))) {
                redirectUrl = request.getContextPath() + "/home_page";
            }

            response.sendRedirect(redirectUrl);
        };
    }
}
