package fpt.g36.gapms.models.dto;

import fpt.g36.gapms.utils.Regex;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileDTO {
    @NotBlank(message = "Username không được bỏ trống")
    private String username;

    @NotBlank(message = "Email không được bỏ trống")
    @Email(message = "Email không đúng format")
    private String email;

    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = Regex.PHONENUMBER,
            message = "Số điện thoại phải có độ dài từ 9 - 10 số")
    private String phoneNumber;

    private String avatar;

    private LocalDate updatedAt;

    @Transient
    private MultipartFile avatarFile;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MultipartFile getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(MultipartFile avatarFile) {
        this.avatarFile = avatarFile;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
