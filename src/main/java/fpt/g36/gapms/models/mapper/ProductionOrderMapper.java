package fpt.g36.gapms.models.mapper;

import fpt.g36.gapms.models.dto.production_order.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDetailDTO;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.ProductionOrderDetail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductionOrderMapper {

    public ProductionOrderDTO toDTO(ProductionOrder po) {
        if (po == null) {
            return null;
        }

        ProductionOrderDTO dto = new ProductionOrderDTO();
        dto.setId(po.getId());
        dto.setStatus(po.getStatus());
        dto.setCreateAt(po.getCreateAt());
        dto.setUpdateAt(po.getUpdateAt());
        dto.setPurchaseOrderId(po.getPurchaseOrder().getId());
        dto.setApprovedBy(po.getApprovedBy().getUsername());
        dto.setCreatedBy(po.getCreatedBy().getUsername());

        return dto;
    }

    public List<ProductionOrderDTO> toDTOList(List<ProductionOrder> po) {
        return po.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ProductionOrderDetailDTO toDetailDTO(ProductionOrderDetail pod) {
        if (pod == null) {
            return null;
        }

        ProductionOrderDetailDTO dto = new ProductionOrderDetailDTO();
        dto.setId(pod.getId());
        dto.setCreatedAt(pod.getCreateAt());
        dto.setUpdatedAt(pod.getUpdateAt());
        dto.setDescription(pod.getDescription());
        dto.setLightEnv(pod.getLight_env());
        dto.setThreadMass(pod.getThread_mass());
        dto.setProductionOrderId(pod.getProductionOrder().getId());

        return dto;
    }

    public List<ProductionOrderDetailDTO> toDetailDTOList(List<ProductionOrderDetail> pod) {
        return pod.stream().map(this::toDetailDTO).collect(Collectors.toList());
    }
}
