package fpt.g36.gapms.models.dto;

import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.utils.Regex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Pattern(regexp = Regex.PASSWORD,
            message = "Mật khẩu phải chứa ít nhất 1 chữ số, 1 ký tự đặc biệt và 1 chữ in hoa")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String rePassword;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = Regex.PHONENUMBER,
            message = "Định dạng số điện thoại không hợp lệ")
    private String phoneNumber;

    private Role role;

    private boolean isActive;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 8 characters")
    @Pattern(regexp = Regex.PASSWORD, message = "Password must contain at least 1 number, 1 special character and 1 uppercase character")
            String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public @NotBlank(message = "Re-password is required") String getRePassword() {
        return rePassword;
    }

    public void setRePassword(@NotBlank(message = "Re-password is required") String rePassword) {
        this.rePassword = rePassword;
    }

    public @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public @NotBlank(message = "Phone number is required") @Pattern(regexp = Regex.PHONENUMBER,
            message = "Invalid phone number format") String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NotBlank(message = "Phone number is required") @Pattern(regexp = Regex.PHONENUMBER,
            message = "Invalid phone number format") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
