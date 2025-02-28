package fpt.g36.gapms.models.dto;

public class RfqDetailDTO {
    private Long productId;
    private Long brandId;
    private Long categoryId;
    private String color;
    private int quantity;

    public RfqDetailDTO() {
    }

    public RfqDetailDTO(Long productId, Long brandId, Long categoryId, String color, int quantity) {
        this.productId = productId;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.color = color;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
