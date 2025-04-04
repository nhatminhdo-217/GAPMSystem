package fpt.g36.gapms.models.dto.purchase_order;

import fpt.g36.gapms.enums.BaseEnum;

import java.time.LocalDate;

public class PurchaseOrderInfoDTO {

    private Long id;

    private String customerName;

    private String companyName;

    private String taxNumber;

    private String companyAddress;

    private String contractId;

    private LocalDate expectedDeliveryDate;

    private LocalDate actualDeliveryDate;

    private Long solutionId;

    private BaseEnum status;

    public PurchaseOrderInfoDTO() {
    }

    public PurchaseOrderInfoDTO(Long id, String customerName, String companyName, String taxNumber, String companyAddress, String contractId, LocalDate expectedDeliveryDate, LocalDate actualDeliveryDate, Long solutionId, BaseEnum status) {
        this.id = id;
        this.customerName = customerName;
        this.companyName = companyName;
        this.taxNumber = taxNumber;
        this.companyAddress = companyAddress;
        this.contractId = contractId;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.solutionId = solutionId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public LocalDate getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public Long getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(Long solutionId) {
        this.solutionId = solutionId;
    }

    public BaseEnum getStatus() {
        return status;
    }

    public void setStatus(BaseEnum status) {
        this.status = status;
    }
}
