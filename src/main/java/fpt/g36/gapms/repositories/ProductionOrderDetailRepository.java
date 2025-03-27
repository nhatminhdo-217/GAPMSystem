package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.ProductionOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionOrderDetailRepository extends JpaRepository<ProductionOrderDetail, Long> {
}
