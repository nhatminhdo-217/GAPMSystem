package fpt.g36.gapms.models.dto.quotation;

import java.util.List;

public class QuotationListDTO {
    private Long quotationId;
    private String userName;
    private List<QuotationDetailDTO> products;

    public QuotationListDTO() {
    }

    public QuotationListDTO(Long quotationId, String userName, List<QuotationDetailDTO> products) {
        this.quotationId = quotationId;
        this.userName = userName;
        this.products = products;
    }

    public Long getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(Long quotationId) {
        this.quotationId = quotationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<QuotationDetailDTO> getProducts() {
        return products;
    }

    public void setProducts(List<QuotationDetailDTO> products) {
        this.products = products;
    }
}
