package fpt.g36.gapms.models.dto;

public class RfqDetailDTO {
    private Long id;
    private Long rfqId;
    private Long productId;
    private Long brandId;
    private Long categoryId;
    private String noteColor; // Sửa từ 'color' thành 'noteColor' để khớp với frontend và entity
    private int quantity;
    private String productName;
    private String brandName;
    private String categoryName;
    private NestedObject product;
    private NestedObject brand;
    private NestedObject cate;

    // Lớp lồng nhau để lưu thông tin product, brand, cate
    public static class NestedObject {
        private Long id;
        private String name;

        public NestedObject() {}
        public NestedObject(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        // Getters và Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRfqId() { return rfqId; }
    public void setRfqId(Long rfqId) { this.rfqId = rfqId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getNoteColor() { return noteColor; }
    public void setNoteColor(String noteColor) { this.noteColor = noteColor; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public NestedObject getProduct() { return product; }
    public void setProduct(NestedObject product) { this.product = product; }
    public NestedObject getBrand() { return brand; }
    public void setBrand(NestedObject brand) { this.brand = brand; }
    public NestedObject getCate() { return cate; }
    public void setCate(NestedObject cate) { this.cate = cate; }

}
