package fpt.g36.gapms.models.dto.purchase_order;

import java.time.LocalDate;
import java.util.List;

public class PurchaseOrderDTO {

    private Long purchaseOrderId;

    private String customerName;

    private String companyName;

    private String taxNumber;

    private String companyAddress;

    private LocalDate expectedDeliveryDate;

    private LocalDate actualDeliveryDate;

    private String contractId;

    private Long solutionId;

    private List<PurchaseOrderItemsDTO> purchaseOrderItems;

    public PurchaseOrderDTO() {
    }

    public PurchaseOrderDTO(Long purchaseOrderId, String customerName, String companyName, String taxNumber, String companyAddress, String contractId, LocalDate expectedDeliveryDate, LocalDate actualDeliveryDate, Long solutionId, List<PurchaseOrderItemsDTO> purchaseOrderItems) {
        this.purchaseOrderId = purchaseOrderId;
        this.customerName = customerName;
        this.companyName = companyName;
        this.taxNumber = taxNumber;
        this.companyAddress = companyAddress;
        this.contractId = contractId;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.solutionId = solutionId;
        this.purchaseOrderItems = purchaseOrderItems;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
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

    public List<PurchaseOrderItemsDTO> getPurchaseOrderItems() {
        return purchaseOrderItems;
    }

    public void setPurchaseOrderItems(List<PurchaseOrderItemsDTO> purchaseOrderItems) {
        this.purchaseOrderItems = purchaseOrderItems;
    }
}
