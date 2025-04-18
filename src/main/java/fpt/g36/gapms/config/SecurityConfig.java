package fpt.g36.gapms.config;

import fpt.g36.gapms.services.impls.CustomOAuth2UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@Import(PasswordConfig.class)
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/home", "/register", "/verify", "/resend", "/home_page", "/assert/**", "/login_form", "/forgot-password", "/reset-password", "/login-error","/verify-code").permitAll() // Cho phép truy cập trang login
                        .requestMatchers("/profile").authenticated()
                        .requestMatchers("/test/user/**").hasRole("USER") //
                        .requestMatchers("/test/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login_form")
                        .loginProcessingUrl("/login")

                        .usernameParameter("phoneNumberOrEmail")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureHandler((request, response, exception) -> {
                            System.out.println("Error: " + exception.getMessage());
                            HttpSession session = request.getSession();
                            session.setAttribute("phoneNumberOrEmail", request.getParameter("phoneNumberOrEmail"));
                            response.sendRedirect("/login-error");
                        }).permitAll()

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
                        .key("mySecretKey")  // Key để mã hóa token
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

            String redirectUrl = request.getContextPath() + "/home_page";

            response.sendRedirect(redirectUrl);
        };
    }
}
