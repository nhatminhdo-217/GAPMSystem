package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.dto.quotation.QuotationInfoProjection;
import fpt.g36.gapms.models.entities.Quotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    @Query(value = "SELECT q.id as quotationId, u.name as username, c.name as companyname, c.tax_number, " +
            "p.name as productname, b.name as brandname, cate.name as categoryname, " +
            "cate.has_color, cbp.price, rd.note_color, r.expect_delivery_date as expectedDate, s.actual_delivery_date as actualDate, s.reason " +
            "FROM quotation q " +
            "JOIN rfq r ON q.rfq_id = r.id " +
            "JOIN rfq_detail rd ON r.id = rd.rfq_id " +
            "JOIN product p ON rd.product_id = p.id " +
            "JOIN brand b ON rd.brand_id = b.id " +
            "JOIN category cate ON rd.cate_id = cate.id " +
            "JOIN cate_brand_price cbp ON cbp.cate_id = cate.id " +
            "JOIN user u ON r.create_by = u.id " +
            "JOIN company_user cu ON u.id = cu.user_id " +
            "JOIN company c ON cu.company_id = c.id " +
            "JOIN solution s ON r.id = s.rfq_id " +
            "WHERE q.id = :quotationId", nativeQuery = true)
    List<QuotationInfoProjection> findQuotationDetail(@Param("quotationId") long id);

    Page<Quotation> findQuotationsByCreateAt(Pageable pageable);
}
