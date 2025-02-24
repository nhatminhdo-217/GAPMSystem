package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_price")
public class PurchaseOrderPrice extends IdEntity {

    @NotNull
    @Column(name = "detail_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal detailPrice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    public PurchaseOrderPrice() {
    }

    public PurchaseOrderPrice(Long id, BigDecimal detailPrice, Quotation quotation) {
        super(id);
        this.detailPrice = detailPrice;
        this.quotation = quotation;
    }

    public BigDecimal getDetailPrice() {
        return detailPrice;
    }

    public void setDetailPrice(BigDecimal detailPrice) {
        this.detailPrice = detailPrice;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
    }
}