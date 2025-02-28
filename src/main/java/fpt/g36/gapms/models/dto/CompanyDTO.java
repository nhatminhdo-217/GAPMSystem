package fpt.g36.gapms.models.dto;


import fpt.g36.gapms.utils.Regex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CompanyDTO {
    @NotBlank(message = "Tên công ty không được để trống")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = Regex.EMAIL,
            message = "Vui lòng nhập đúng định dạng Email (example@gmail.com)")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = Regex.PHONENUMBER,
            message = "Vui lòng nhập đúng định dạng số điện thoại (+84 - 9 đến 10 chữ số)")
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ công ty không được để trống")
    private String address;

    @NotBlank(message = "Mã số thuế không được để chống")
    @Pattern(regexp = Regex.TAXNUMBER,
            message = "Vui lòng nhập đúng định dạng mã số thuế")
    private String taxNumber;

    public CompanyDTO() {
    }

    public CompanyDTO(String name, String taxNumber, String address, String phoneNumber, String email) {
        this.name = name;
        this.taxNumber = taxNumber;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }
}
