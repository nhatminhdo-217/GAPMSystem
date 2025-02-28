package fpt.g36.gapms.models.dto.quotation;

import java.util.Date;
import java.util.List;

public class QuotationInfoDTO {
    private Long quotationId;
    private String userName;
    private String companyName;
    private String taxNumber;
    private String productName;
    private Date expectedDate;
    private Date actualDate;
    private String reason;
    private List<QuotationDetailDTO> products;

    public QuotationInfoDTO() {
    }

    public QuotationInfoDTO(Long quotationId, String userName, String companyName, String taxNumber, String productName, Date expectedDate, Date actualDate, String reason, List<QuotationDetailDTO> products) {
        this.quotationId = quotationId;
        this.userName = userName;
        this.companyName = companyName;
        this.taxNumber = taxNumber;
        this.productName = productName;
        this.expectedDate = expectedDate;
        this.actualDate = actualDate;
        this.reason = reason;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public Date getActualDate() {
        return actualDate;
    }

    public void setActualDate(Date actualDate) {
        this.actualDate = actualDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<QuotationDetailDTO> getProducts() {
        return products;
    }

    public void setProducts(List<QuotationDetailDTO> products) {
        this.products = products;
    }
}
