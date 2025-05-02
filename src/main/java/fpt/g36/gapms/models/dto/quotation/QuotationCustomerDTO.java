package fpt.g36.gapms.models.dto.quotation;

import java.math.BigDecimal;

public class QuotationCustomerDTO {
    private Long rfqDetailId;
    private String productName;
    private String brandName;
    private String categoryName;
    private Boolean isColor;
    private BigDecimal price;
    private String noteColor;
    private int quantity;
    private String note;

    public QuotationCustomerDTO() {
    }

    public QuotationCustomerDTO(Long rfqDetailId, String productName, String brandName, String categoryName, Boolean isColor, BigDecimal price, String noteColor, int quantity, String note) {
        this.rfqDetailId = rfqDetailId;
        this.productName = productName;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.isColor = isColor;
        this.price = price;
        this.noteColor = noteColor;
        this.quantity = quantity;
        this.note = note;
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

    public Boolean getColor() {
        return isColor;
    }

    public void setColor(Boolean color) {
        isColor = color;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getRfqDetailId() {
        return rfqDetailId;
    }

    public void setRfqDetailId(Long rfqDetailId) {
        this.rfqDetailId = rfqDetailId;
    }
}
