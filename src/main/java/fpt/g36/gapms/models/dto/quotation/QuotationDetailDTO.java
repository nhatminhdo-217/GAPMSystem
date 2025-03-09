package fpt.g36.gapms.models.dto.quotation;

import java.math.BigDecimal;

public class QuotationDetailDTO {
    private String productName;
    private String brandName;
    private String categoryName;
    private BigDecimal price;
    private String noteColor;
    private Integer quantity;

    public QuotationDetailDTO() {
    }

    public QuotationDetailDTO(String productName, String brandName, String categoryName, BigDecimal price, String noteColor, Integer quantity) {
        this.productName = productName;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.price = price;
        this.noteColor = noteColor;
        this.quantity = quantity;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
}
