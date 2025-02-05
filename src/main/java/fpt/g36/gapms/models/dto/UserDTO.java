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

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 8 characters")
    @Pattern(regexp = Regex.PASSWORD,
            message = "Password must contain at least 1 number, 1 special character and 1 uppercase character")
    private String password;

    @NotBlank(message = "Re-password is required")
    private String rePassword;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = Regex.PHONENUMBER,
            message = "Invalid phone number format")
    private String phoneNumber;

    private Role role;

    private boolean isActive;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    public @NotBlank(message = "Username is required") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Username is required") String username) {
        this.username = username;
    }

    public @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 8 characters") @Pattern(regexp = Regex.PASSWORD,
            message = "Password must contain at least 1 number, 1 special character and 1 uppercase character") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 8 characters") @Pattern(regexp = Regex.PASSWORD,
            message = "Password must contain at least 1 number, 1 special character and 1 uppercase character") String password) {
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

    public void setEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email) {
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
