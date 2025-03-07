package fpt.g36.gapms.models.dto.quotation;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class QuotationInfoDTO {
    private Long quotationId;
    private String userName;
    private String companyName;
    private String taxNumber;
    private String companyAddress;
    private String isAccepted;
    private LocalDate expectedDate;
    private LocalDate actualDate;
    private Long solutionId;
    private List<QuotationDetailDTO> products;

    public QuotationInfoDTO() {
    }

    public QuotationInfoDTO(Long quotationId, String userName, String companyName, String taxNumber, String companyAddress, String isAccepted, LocalDate expectedDate, LocalDate actualDate, Long solutionId, List<QuotationDetailDTO> products) {
        this.quotationId = quotationId;
        this.userName = userName;
        this.companyName = companyName;
        this.taxNumber = taxNumber;
        this.companyAddress = companyAddress;
        this.isAccepted = isAccepted;
        this.expectedDate = expectedDate;
        this.actualDate = actualDate;
        this.solutionId = solutionId;
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

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(String isAccepted) {
        this.isAccepted = isAccepted;
    }

    public LocalDate getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(LocalDate expectedDate) {
        this.expectedDate = expectedDate;
    }

    public LocalDate getActualDate() {
        return actualDate;
    }

    public void setActualDate(LocalDate actualDate) {
        this.actualDate = actualDate;
    }

    public List<QuotationDetailDTO> getProducts() {
        return products;
    }

    public void setProducts(List<QuotationDetailDTO> products) {
        this.products = products;
    }

    public Long getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(Long solutionId) {
        this.solutionId = solutionId;
    }
}
