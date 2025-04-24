package fpt.g36.gapms.models.dto.dye_technical;

import java.math.BigDecimal;
import java.util.List;

public class TechnologyProcessForm {
    private Long workOrderId;
    private Long workOrderDetailId;
    private List<DyeTypeDTO> dyeTypesForFirstBatches;
    private List<DyeTypeDTO> dyeTypesForLastBatch;
    private BigDecimal dispergatorNForFirstBatches;
    private BigDecimal dispergatorNForLastBatch;

    // Constructor mặc định
    public TechnologyProcessForm() {
    }

    // Getter và Setter
    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public Long getWorkOrderDetailId() {
        return workOrderDetailId;
    }

    public void setWorkOrderDetailId(Long workOrderDetailId) {
        this.workOrderDetailId = workOrderDetailId;
    }

    public List<DyeTypeDTO> getDyeTypesForFirstBatches() {
        return dyeTypesForFirstBatches;
    }

    public void setDyeTypesForFirstBatches(List<DyeTypeDTO> dyeTypesForFirstBatches) {
        this.dyeTypesForFirstBatches = dyeTypesForFirstBatches;
    }

    public List<DyeTypeDTO> getDyeTypesForLastBatch() {
        return dyeTypesForLastBatch;
    }

    public void setDyeTypesForLastBatch(List<DyeTypeDTO> dyeTypesForLastBatch) {
        this.dyeTypesForLastBatch = dyeTypesForLastBatch;
    }

    public BigDecimal getDispergatorNForFirstBatches() {
        return dispergatorNForFirstBatches;
    }

    public void setDispergatorNForFirstBatches(BigDecimal dispergatorNForFirstBatches) {
        this.dispergatorNForFirstBatches = dispergatorNForFirstBatches;
    }

    public BigDecimal getDispergatorNForLastBatch() {
        return dispergatorNForLastBatch;
    }

    public void setDispergatorNForLastBatch(BigDecimal dispergatorNForLastBatch) {
        this.dispergatorNForLastBatch = dispergatorNForLastBatch;
    }
}
