package fpt.g36.gapms.models.dto.quotation;

import java.util.Date;
import java.util.List;

public class QuotationInforCustomerDTO {
    private Long quotationId;
    private Long rfqId;
    private Boolean isCanceled;
    private String isAccepted;
    private String userName;
    private String companyName;
    private String taxNumber;
    private String productName;
    private Date expectedDate;
    private Date actualDate;
    private String reason;
    private List<QuotationCustomerDTO> products;

    public QuotationInforCustomerDTO() {
    }

    public QuotationInforCustomerDTO(Long quotationId, Long rfqId, Boolean isCanceled, String isAccepted, String userName, String companyName, String taxNumber, String productName, Date expectedDate, Date actualDate, String reason, List<QuotationCustomerDTO> products) {
        this.quotationId = quotationId;
        this.rfqId = rfqId;
        this.isCanceled = isCanceled;
        this.isAccepted = isAccepted;
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

    public Long getRfqId() {
        return rfqId;
    }

    public void setRfqId(Long rfqId) {
        this.rfqId = rfqId;
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

    public List<QuotationCustomerDTO> getProducts() {
        return products;
    }

    public void setProducts(List<QuotationCustomerDTO> products) {
        this.products = products;
    }

    public Boolean getCancel() {
        return isCanceled;
    }

    public void setCancel(Boolean isCanceled) {
        isCanceled = isCanceled;
    }

    public String getAccepted() {
        return isAccepted;
    }

    public void setAccepted(String accepted) {
        isAccepted = accepted;
    }
}
