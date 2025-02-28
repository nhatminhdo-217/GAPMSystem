package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

    @Column(name = "is_sent")
    @Enumerated(EnumType.STRING)
    private BaseEnum isSent;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "create_by", nullable = false)
    private User createBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @OneToMany(mappedBy = "rfq")
    private Set<Quotation> quotations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "rfq", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RfqDetail> rfqDetails = new ArrayList<>();

    @OneToOne(mappedBy = "rfq", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Solution solution;

    public Rfq() {
    }

    public Rfq(Long id, LocalDateTime createAt, LocalDateTime updateAt, LocalDate expectDeliveryDate, SendEnum isApproved, BaseEnum isSent, User createBy, User approvedBy, Set<Quotation> quotations, List<RfqDetail> rfqDetails, Solution solutions) {
        super(id, createAt, updateAt);
        this.expectDeliveryDate = expectDeliveryDate;
        this.isApproved = isApproved;
        this.isSent = isSent;
        this.createBy = createBy;
        this.approvedBy = approvedBy;
        this.quotations = quotations;
        this.rfqDetails = rfqDetails;
        this.solution = solutions;
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

    public List<RfqDetail> getRfqDetails() {
        return rfqDetails;
    }

    public void setRfqDetails(List<RfqDetail> rfqDetails) {
        this.rfqDetails = rfqDetails;
    }

    public Solution getSolutions() {
        return solution;
    }

    public void setSolutions(Solution solutions) {
        this.solution = solutions;
    }

    public BaseEnum getIsSent() {
        return isSent;
    }

    public void setIsSent(BaseEnum isSent) {
        this.isSent = isSent;
    }
}