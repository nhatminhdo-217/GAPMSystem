package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "solution")
public class Solution extends BaseEntity {

    @Column(name = "reason")
    private String reason;

    @NotNull
    @Column(name = "actual_delivery_date", nullable = false)
    private LocalDate actualDeliveryDate;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "create_by", nullable = false)
    private User createBy;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rfq_id", nullable = false)
    private Rfq rfq;

    public Solution() {
    }

    public Solution(Long id, LocalDateTime createAt, LocalDateTime updateAt, String reason, LocalDate actualDeliveryDate, String description, User createBy, Rfq rfq) {
        super(id, createAt, updateAt);
        this.reason = reason;
        this.actualDeliveryDate = actualDeliveryDate;
        this.description = description;
        this.createBy = createBy;
        this.rfq = rfq;
    }

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

    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    public Rfq getRfq() {
        return rfq;
    }

    public void setRfq(Rfq rfq) {
        this.rfq = rfq;
    }
}