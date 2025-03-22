package fpt.g36.gapms.repositories;

import aj.org.objectweb.asm.commons.Remapper;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.ProductionOrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    Page<ProductionOrder> findAll(Pageable pageable);

    Optional<ProductionOrder> findByPurchaseOrderId(Long id);

    @Query("SELECT pod FROM ProductionOrderDetail pod WHERE pod.productionOrder.id = ?1")
    List<ProductionOrderDetail> findAllByProductionOrderId(Long id);

    @Query("SELECT pod FROM ProductionOrderDetail pod WHERE pod.id = ?1")
    Optional<ProductionOrderDetail> findByProductionOrderId(Long id);
}
