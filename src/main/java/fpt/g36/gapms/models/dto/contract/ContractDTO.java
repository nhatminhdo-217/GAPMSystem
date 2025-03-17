package fpt.g36.gapms.models.dto.contract;

import fpt.g36.gapms.enums.BaseEnum;
import jakarta.validation.constraints.NotBlank;

public class ContractDTO {

    private String id;

    @NotBlank(message = "Tên hợp đồng không được để trống")
    private String name;

    private String path;

    private BaseEnum status;

    private Long purchaseOrderId;

    private String approvedByUserName;

    private String createByUserName;

    public ContractDTO() {
    }

    public ContractDTO(String id, String name, String path, BaseEnum status, Long purchaseOrderId, String approvedByUserName, String createByUserName) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.status = status;
        this.purchaseOrderId = purchaseOrderId;
        this.approvedByUserName = approvedByUserName;
        this.createByUserName = createByUserName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BaseEnum getStatus() {
        return status;
    }

    public void setStatus(BaseEnum status) {
        this.status = status;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
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
}
