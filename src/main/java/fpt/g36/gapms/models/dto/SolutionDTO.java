package fpt.g36.gapms.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class SolutionDTO {

    private String reason;

    @NotNull(message = "Ngày giao hàng dự kiến không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualDeliveryDate;

    @NotNull(message = "Mô tả không được để trống")
    @Size(min = 1, max = 1000, message = "Mô tả phải từ 1 đến 1000 ký tự")
    private String description;

    // Thêm getter/setter
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}