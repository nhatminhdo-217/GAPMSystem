package fpt.g36.gapms.models.dto;

import fpt.g36.gapms.utils.Regex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
public class CreateAccountDTO {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = Regex.EMAIL,
            message = "Vui lòng nhập đúng định dạng Email (example@gmail.com)")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = Regex.PHONENUMBER,
            message = "Định dạng số điện thoại không hợp lệ")
    private String phoneNumber;

    private int role;

    public CreateAccountDTO() {
    }

    public CreateAccountDTO(String username, String email, String phoneNumber, int role) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername( String username) {
        this.username = username;
    }

    public  String getEmail() {
        return email;
    }

    public void setEmail( String email) {
        this.email = email;
    }

    public  String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

}
