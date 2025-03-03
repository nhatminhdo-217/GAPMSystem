package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Query("""
       SELECT DISTINCT new fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO(
          u.username as customerName,
          c.name as companyName,
          c.taxNumber,
          c.address as companyAddress,
          con.id as contractId,
          r.expectDeliveryDate as expectedDate,
          s.actualDeliveryDate as actualDate,
          s.id as solutionId
       )
       FROM PurchaseOrder po
         JOIN po.quotation q
         JOIN po.contract con
         JOIN q.rfq r
         JOIN r.solution s
         JOIN r.createBy u
         JOIN u.companyUsers cu
         JOIN cu.company c
       WHERE po.id = :id
      """)
    Optional<PurchaseOrderInfoDTO> getPurchaseOrderInfoDTOById(@Param("id") Long id);

    @Query(value = "SELECT p.name AS productName, " +
            "b.name AS brandName, " +
            "c.name AS categoryName, " +
            "rd.note_color AS noteColor, " +
            "rd.quantity AS quantity, " +
            "cbp.price AS price, " +
            "(rd.quantity * cbp.price) AS detailAmount " +
            "FROM purchase_order po " +
            "JOIN quotation q ON po.quotation_id = q.id " +
            "JOIN rfq r ON q.rfq_id = r.id " +
            "JOIN rfq_detail rd ON rd.rfq_id = r.id " +
            "JOIN product p ON rd.product_id = p.id " +
            "JOIN brand b ON rd.brand_id = b.id " +
            "JOIN category c ON rd.cate_id = c.id " +
            "JOIN cate_brand_price cbp ON c.id = cbp.cate_id " +
            "WHERE po.id = :id AND cbp.is_color = 1", nativeQuery = true)
    List<Object[]> getPurchaseOrderItemsDTOById(@Param("id") Long id);
}
