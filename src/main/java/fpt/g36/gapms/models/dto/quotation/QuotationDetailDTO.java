package fpt.g36.gapms.models.dto.quotation;

import java.math.BigDecimal;

public class QuotationDetailDTO {
    private String brandName;
    private String categoryName;
    private boolean hasColor;
    private BigDecimal price;
    private String noteColor;

    public QuotationDetailDTO() {
    }

    public QuotationDetailDTO(String brandName, String categoryName, boolean hasColor, BigDecimal price, String noteColor) {
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.hasColor = hasColor;
        this.price = price;
        this.noteColor = noteColor;
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

    public boolean isHasColor() {
        return hasColor;
    }

    public void setHasColor(boolean hasColor) {
        this.hasColor = hasColor;
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
}
