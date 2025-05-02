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
    private BigDecimal dfmForFirstBatches;
    private BigDecimal dfmForLastBatch;
    private BigDecimal anbatexForFirstBatches;
    private BigDecimal anbatexForLastBatch;

    // Constructor mặc định
    public TechnologyProcessForm() {};

    //
    public TechnologyProcessForm(Long workOrderId, Long workOrderDetailId, List<DyeTypeDTO> dyeTypesForFirstBatches, List<DyeTypeDTO> dyeTypesForLastBatch, BigDecimal dispergatorNForFirstBatches, BigDecimal dispergatorNForLastBatch, BigDecimal dfmForFirstBatches, BigDecimal dfmForLastBatch, BigDecimal anbatexForFirstBatches, BigDecimal anbatexForLastBatch) {
        this.workOrderId = workOrderId;
        this.workOrderDetailId = workOrderDetailId;
        this.dyeTypesForFirstBatches = dyeTypesForFirstBatches;
        this.dyeTypesForLastBatch = dyeTypesForLastBatch;
        this.dispergatorNForFirstBatches = dispergatorNForFirstBatches;
        this.dispergatorNForLastBatch = dispergatorNForLastBatch;
        this.dfmForFirstBatches = dfmForFirstBatches;
        this.dfmForLastBatch = dfmForLastBatch;
        this.anbatexForFirstBatches = anbatexForFirstBatches;
        this.anbatexForLastBatch = anbatexForLastBatch;
    }

    public BigDecimal getDfmForFirstBatches() {
        return dfmForFirstBatches;
    }

    public void setDfmForFirstBatches(BigDecimal dfmForFirstBatches) {
        this.dfmForFirstBatches = dfmForFirstBatches;
    }

    public BigDecimal getDfmForLastBatch() {
        return dfmForLastBatch;
    }

    public void setDfmForLastBatch(BigDecimal dfmForLastBatch) {
        this.dfmForLastBatch = dfmForLastBatch;
    }

    public BigDecimal getAnbatexForFirstBatches() {
        return anbatexForFirstBatches;
    }

    public void setAnbatexForFirstBatches(BigDecimal anbatexForFirstBatches) {
        this.anbatexForFirstBatches = anbatexForFirstBatches;
    }

    public BigDecimal getAnbatexForLastBatch() {
        return anbatexForLastBatch;
    }

    public void setAnbatexForLastBatch(BigDecimal anbatexForLastBatch) {
        this.anbatexForLastBatch = anbatexForLastBatch;
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
