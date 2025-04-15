package fpt.g36.gapms.models.dto.quotation;

import fpt.g36.gapms.enums.BaseEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class QuotationDTO {
    private Long id;
    private Long rfqId;
    private BaseEnum isAccepted;
    private String createBy;
    private String customerName;
    private LocalDateTime createAt;

    public QuotationDTO() {
    }

    public QuotationDTO(Long id, Long rfqId, BaseEnum isAccepted, String createBy, String customerName, LocalDateTime createAt) {
        this.id = id;
        this.rfqId = rfqId;
        this.isAccepted = isAccepted;
        this.createBy = createBy;
        this.customerName = customerName;
        this.createAt = createAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRfqId() {
        return rfqId;
    }

    public void setRfqId(Long rfqId) {
        this.rfqId = rfqId;
    }

    public BaseEnum getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(BaseEnum isAccepted) {
        this.isAccepted = isAccepted;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
