package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.SendEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "rfq")
public class Rfq extends BaseEntity {
    @NotNull
    @Column(name = "expect_delivery_date", nullable = false)
    private LocalDate expectDeliveryDate;

    @Column(name = "is_approved")
    @Enumerated(EnumType.STRING)
    private SendEnum isApproved;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "create_by", nullable = false)
    private User createBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @OneToMany(mappedBy = "rfq")
    private Set<Quotation> quotations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "rfq")
    private Set<RfqDetail> rfqDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "rfq")
    private Set<Solution> solutions = new LinkedHashSet<>();

    public Rfq() {
    }

    public Rfq(Long id, LocalDateTime createAt, LocalDateTime updateAt, LocalDate expectDeliveryDate, SendEnum isApproved, User createBy, User approvedBy, Set<Quotation> quotations, Set<RfqDetail> rfqDetails, Set<Solution> solutions) {
        super(id, createAt, updateAt);
        this.expectDeliveryDate = expectDeliveryDate;
        this.isApproved = isApproved;
        this.createBy = createBy;
        this.approvedBy = approvedBy;
        this.quotations = quotations;
        this.rfqDetails = rfqDetails;
        this.solutions = solutions;
    }

    public LocalDate getExpectDeliveryDate() {
        return expectDeliveryDate;
    }

    public void setExpectDeliveryDate(LocalDate expectDeliveryDate) {
        this.expectDeliveryDate = expectDeliveryDate;
    }

    public SendEnum getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(SendEnum isApproved) {
        this.isApproved = isApproved;
    }

    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Set<Quotation> getQuotations() {
        return quotations;
    }

    public void setQuotations(Set<Quotation> quotations) {
        this.quotations = quotations;
    }

    public Set<RfqDetail> getRfqDetails() {
        return rfqDetails;
    }

    public void setRfqDetails(Set<RfqDetail> rfqDetails) {
        this.rfqDetails = rfqDetails;
    }

    public Set<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(Set<Solution> solutions) {
        this.solutions = solutions;
    }
}