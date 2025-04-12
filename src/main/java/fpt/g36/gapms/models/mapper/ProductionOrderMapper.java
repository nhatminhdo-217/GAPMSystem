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
    dto.setCreatedBy(po.getCreatedBy() != null ? po.getCreatedBy().getUsername() : null);

    System.err.println("DTO: " + dto.getStatus());

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

    if (pod.getLight_env() != null) {
      dto.setLightEnv(pod.getLight_env());
    }
    if (pod.getThread_mass() != null) {
        dto.setThreadMass(pod.getThread_mass());
    }
    dto.setProductionOrderId(pod.getProductionOrder().getId());
    dto.setPurchaseOrderDetailId(pod.getPurchaseOrderDetail().getId());
    dto.setBrandName(pod.getPurchaseOrderDetail().getBrand().getName());
    dto.setNoteColor(pod.getPurchaseOrderDetail().getNote_color());
    dto.setQuantity(pod.getPurchaseOrderDetail().getQuantity());
    dto.setCategory(pod.getPurchaseOrderDetail().getCategory().getName());
    dto.setThreadName(pod.getPurchaseOrderDetail().getProduct().getThread().getName());
    dto.setRate(pod.getPurchaseOrderDetail().getProduct().getThread().getConvert_rate());
    dto.setProcess(pod.getPurchaseOrderDetail().getProduct().getThread().getProcess().getId());

    return dto;
  }

  public List<ProductionOrderDetailDTO> toDetailDTOList(List<ProductionOrderDetail> pod) {
    return pod.stream().map(this::toDetailDTO).collect(Collectors.toList());
  }

  public ProductionOrderDetailDTO convertToDTO(ProductionOrderDetail entity) {
    ProductionOrderDetailDTO dto = new ProductionOrderDetailDTO();
    dto.setId(entity.getId());
    dto.setCreatedAt(entity.getCreateAt());
    dto.setUpdatedAt(entity.getUpdateAt());
    dto.setLightEnv(entity.getLight_env());
    dto.setThreadMass(entity.getThread_mass());
    dto.setProductionOrderId(entity.getProductionOrder().getId());

    // Kiểm tra null trước khi lấy ID
    if (entity.getPurchaseOrderDetail() != null) {
      dto.setPurchaseOrderDetailId(entity.getPurchaseOrderDetail().getId());
    }

    dto.setBrandName(entity.getPurchaseOrderDetail().getBrand().getName());
    dto.setNoteColor(entity.getPurchaseOrderDetail().getNote_color());
    dto.setQuantity(entity.getPurchaseOrderDetail().getQuantity());
    dto.setCategory(entity.getPurchaseOrderDetail().getCategory().getName());
    dto.setThreadName(entity.getPurchaseOrderDetail().getProduct().getThread().getName());
    dto.setRate(entity.getPurchaseOrderDetail().getProduct().getThread().getConvert_rate());
    dto.setProcess(entity.getPurchaseOrderDetail().getProduct().getThread().getProcess().getId());

    return dto;
  }
}
