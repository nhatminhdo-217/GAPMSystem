package fpt.g36.gapms.repositories;

import aj.org.objectweb.asm.commons.Remapper;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.ProductionOrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    Page<ProductionOrder> findAll(Pageable pageable);

    Optional<ProductionOrder> findByPurchaseOrderId(Long id);

    @Query("SELECT pod FROM ProductionOrderDetail pod WHERE pod.productionOrder.id = :productionOrderId")
    List<ProductionOrderDetail> findAllByProductionOrderId(@Param("productionOrderId") Long id);

    @Query("SELECT pod FROM ProductionOrderDetail pod WHERE pod.id = :id")
    Optional<ProductionOrderDetail> findByProductionOrderId(@Param("id") Long id);
}
