package fpt.g36.gapms.config;

import fpt.g36.gapms.services.impls.CustomOAuth2UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
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
                        .requestMatchers("/home", "/register", "/verify", "/resend", "/home_page", "/assert/**",
                                "/login_form", "/forgot-password", "/reset-password", "/login-error", "/verify-code")
                        .permitAll() // Cho phép truy cập trang login
                        .requestMatchers("/profile").authenticated()
                        .requestMatchers("/test/user/**").hasRole("USER")
                        .requestMatchers("/admin/{id}/toggle-status").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login_form")
                        .loginProcessingUrl("/login")

                        .usernameParameter("phoneNumberOrEmail")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureHandler((request, response, exception) -> {
                            System.out.println("Error: " + exception.getMessage());
                            HttpSession session = request.getSession();

                            // ✅ Xử lý lỗi đúng thứ tự
                            String errorMessage;
                            if (exception instanceof DisabledException) {
                                errorMessage = "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.";
                            } else if (exception instanceof BadCredentialsException) {
                                errorMessage = "Sai tài khoản hoặc mật khẩu.";
                            } else {
                                errorMessage = "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.";
                            }

                            System.out.println("⚠️ Đăng nhập thất bại: " + errorMessage);
                            session.setAttribute("error", errorMessage);
                            session.setAttribute("phoneNumberOrEmail", request.getParameter("phoneNumberOrEmail"));
                            response.sendRedirect("/login-error");
                        }).permitAll()

                )
                .oauth2Login(oauth2 -> oauth2
                        .failureHandler((request, response, exception) -> {
                            HttpSession session = request.getSession();

                            // ✅ Log toàn bộ lỗi để kiểm tra nội dung thực sự
                            System.out.println("⚠️ OAuth2 Exception Class: " + exception.getClass().getName());
                            System.out.println("⚠️ OAuth2 Exception Message: " + exception.getMessage());
                            if (exception.getCause() != null) {
                                System.out.println("⚠️ OAuth2 Exception Cause: " + exception.getCause().getMessage());
                            }

                            // ✅ Lỗi mặc định
                            String errorMessage = "Đăng nhập bằng Google thất bại.";

                            // ✅ Kiểm tra cả lỗi chính và lỗi gốc
                            String exceptionMessage = (exception.getMessage() != null) ? exception.getMessage() : "";
                            String causeMessage = (exception.getCause() != null
                                    && exception.getCause().getMessage() != null)
                                            ? exception.getCause().getMessage()
                                            : "";

                            if (causeMessage.toLowerCase().contains("tài khoản của bạn đã bị khóa") ||
                                    exceptionMessage.toLowerCase().contains("tài khoản của bạn đã bị khóa")) {
                                errorMessage = "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.";
                            }

                            System.out.println("⚠️ OAuth2 Đăng nhập thất bại: " + errorMessage);

                            session.setAttribute("error", errorMessage);
                            response.sendRedirect("/login-error");
                        })
                        .successHandler(customAuthenticationSuccessHandler()) // Xử lý khi đăng nhập thành công
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/login_form") // Sau khi logout, quay về trang login
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember")
                        .permitAll())
                .rememberMe(rememberMe -> rememberMe
                        .key("mySecretKey") // Key để mã hóa token
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 ngày
                        .rememberMeParameter("remember") // Tên tham số trên form
                        .alwaysRemember(false));

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
