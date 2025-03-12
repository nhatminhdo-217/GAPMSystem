package fpt.g36.gapms.models.dto.purchase_order;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.Contract;
import fpt.g36.gapms.models.entities.Quotation;
import fpt.g36.gapms.models.entities.User;

import java.time.LocalDate;
import java.util.List;

public class PurchaseOrderDTO {

    private Long purchaseOrderId;

    private String customerName;

    private BaseEnum status;

    private Long quotationId;

    private String contractId;

    private String approvedByUserName;

    private String createByUserName;

    private LocalDate createAt;

    public PurchaseOrderDTO() {
    }

    public PurchaseOrderDTO(Long purchaseOrderId, String customerName, BaseEnum status, Long quotationId, String contractId, String approvedByUserName, String createByUserName, LocalDate createAt) {
        this.purchaseOrderId = purchaseOrderId;
        this.customerName = customerName;
        this.status = status;
        this.quotationId = quotationId;
        this.contractId = contractId;
        this.approvedByUserName = approvedByUserName;
        this.createByUserName = createByUserName;
        this.createAt = createAt;
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

    public BaseEnum getStatus() {
        return status;
    }

    public void setStatus(BaseEnum status) {
        this.status = status;
    }

    public Long getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(Long quotationId) {
        this.quotationId = quotationId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }


    public String getApprovedByUserName() {
        return approvedByUserName;
    }

    public void setApprovedByUserName(String approvedByUserName) {
        this.approvedByUserName = approvedByUserName;
    }

    public String getCreateByUserName() {
        return createByUserName;
    }

    public void setCreateByUserName(String createByUserName) {
        this.createByUserName = createByUserName;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }
}
