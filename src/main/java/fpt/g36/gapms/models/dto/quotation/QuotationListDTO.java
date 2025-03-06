package fpt.g36.gapms.models.dto.quotation;

import fpt.g36.gapms.enums.BaseEnum;

import java.time.LocalDate;
import java.util.List;

public class QuotationListDTO {
    private Long quotationId;
    private String userName;
    private BaseEnum isAccepted;
    private LocalDate createAt;
    private List<QuotationDetailDTO> products;

    public QuotationListDTO() {
    }

    public QuotationListDTO(Long quotationId, String userName, BaseEnum isAccepted, LocalDate createAt, List<QuotationDetailDTO> products) {
        this.quotationId = quotationId;
        this.userName = userName;
        this.isAccepted = isAccepted;
        this.createAt = createAt;
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

    public BaseEnum getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(BaseEnum isAccepted) {
        this.isAccepted = isAccepted;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    public List<QuotationDetailDTO> getProducts() {
        return products;
    }

    public void setProducts(List<QuotationDetailDTO> products) {
        this.products = products;
    }
}
