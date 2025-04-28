package fpt.g36.gapms.repositories;

import aj.org.objectweb.asm.commons.Remapper;
import fpt.g36.gapms.enums.BaseEnum;
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

    Page<ProductionOrder> findAllByStatus(BaseEnum status, Pageable pageable);

    @Query(value = """
    select distinct po from ProductionOrder po
    where (:search is null or lower(po.createdBy.username) like concat('%', lower(:search), '%'))
    and (:status is null or po.status = :status)
    order by 
      case 
        when po.status = fpt.g36.gapms.enums.BaseEnum.DRAFT then 1
        when po.status = fpt.g36.gapms.enums.BaseEnum.NOT_APPROVED then 2
        when po.status = fpt.g36.gapms.enums.BaseEnum.WAIT_FOR_APPROVAL then 3
        when po.status = fpt.g36.gapms.enums.BaseEnum.APPROVED then 4
        when po.status = fpt.g36.gapms.enums.BaseEnum.CANCELED then 5
        else 6
      end,
      po.createAt desc
    """,
            countQuery = """
    select count(distinct po) from ProductionOrder po
    where (:search is null or lower(po.createdBy.username) like concat('%', lower(:search), '%'))
    and (:status is null or po.status = :status)
    """)
    Page<ProductionOrder> searchAndFilter(
            @Param("search") String search,
            @Param("status") BaseEnum status,
            Pageable pageable);

    Page<ProductionOrder> findAllByStatusAndWorkOrderIsNull(BaseEnum status, Pageable pageable);

    Page<ProductionOrder> findAllByStatusAndWorkOrderIsNotNull(BaseEnum status, Pageable pageable);
}
