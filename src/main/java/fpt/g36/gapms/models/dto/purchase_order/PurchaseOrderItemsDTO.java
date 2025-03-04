package fpt.g36.gapms.models.dto.purchase_order;

import java.math.BigDecimal;

public class PurchaseOrderItemsDTO {
    private String productName;

    private String brandName;

    private String categoryName;

    private String noteColor;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal detailAmount;

    public PurchaseOrderItemsDTO() {
    }

    public PurchaseOrderItemsDTO(String productName, String brandName, String categoryName, String noteColor, Integer quantity, BigDecimal price, BigDecimal detailAmount) {
        this.productName = productName;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.noteColor = noteColor;
        this.quantity = quantity;
        this.price = price;
        this.detailAmount = detailAmount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(String noteColor) {
        this.noteColor = noteColor;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDetailAmount() {
        return detailAmount;
    }

    public void setDetailAmount(BigDecimal detailAmount) {
        this.detailAmount = detailAmount;
    }
}
