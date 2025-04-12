package fpt.g36.gapms.models.dto.purchase_order;

import java.math.BigDecimal;

public class PurchaseOrderDetailDTO {
    private Integer quantity;
    private BigDecimal unitPrice;
    private Long brandId;
    private Long categoryId;
    private Long productId;
    private Long purchaseOrderId;
    private String note_color;
    private BigDecimal total_price;

    public PurchaseOrderDetailDTO() {
    }

    public PurchaseOrderDetailDTO(Integer quantity, BigDecimal unitPrice, Long brandId, Long categoryId, Long productId, Long purchaseOrderId, String note_color, BigDecimal total_price) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.productId = productId;
        this.purchaseOrderId = purchaseOrderId;
        this.note_color = note_color;
        this.total_price = total_price;
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

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getNote_color() {
        return note_color;
    }

    public void setNote_color(String note_color) {
        this.note_color = note_color;
    }

    public BigDecimal getTotal_price() {
        return total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }
}
