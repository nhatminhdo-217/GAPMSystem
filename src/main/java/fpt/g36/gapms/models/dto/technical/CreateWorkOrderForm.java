package fpt.g36.gapms.models.dto.technical;

import java.util.List;

public class CreateWorkOrderForm {
    private Long productionOrderId;
    private List<Long> selectedDyeMachineIds;
    private List<Long> selectedWindingMachineIds;

    public Long getProductionOrderId() {
        return productionOrderId;
    }

    public void setProductionOrderId(Long productionOrderId) {
        this.productionOrderId = productionOrderId;
    }

    public List<Long> getSelectedDyeMachineIds() {
        return selectedDyeMachineIds;
    }

    public void setSelectedDyeMachineIds(List<Long> selectedDyeMachineIds) {
        this.selectedDyeMachineIds = selectedDyeMachineIds;
    }

    public List<Long> getSelectedWindingMachineIds() {
        return selectedWindingMachineIds;
    }

    public void setSelectedWindingMachineIds(List<Long> selectedWindingMachineIds) {
        this.selectedWindingMachineIds = selectedWindingMachineIds;
    }
}
