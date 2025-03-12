package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_order_detail")
public class PurchaseOrderDetail extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String note_color;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    public PurchaseOrderDetail() {
    }

    public PurchaseOrderDetail(Long id, LocalDateTime createAt, LocalDateTime updateAt, PurchaseOrder purchaseOrder, Product product, Brand brand, Category category, String note_color, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        super(id, createAt, updateAt);
        this.purchaseOrder = purchaseOrder;
        this.product = product;
        this.brand = brand;
        this.category = category;
        this.note_color = note_color;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getNote_color() {
        return note_color;
    }

    public void setNote_color(String note_color) {
        this.note_color = note_color;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
