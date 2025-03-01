package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.dto.quotation.QuotationDetailDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoProjection;
import fpt.g36.gapms.models.dto.quotation.QuotationListDTO;
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
            "cbp.is_color as isColor, cbp.price, rd.note_color, r.expect_delivery_date as expectedDate, s.actual_delivery_date as actualDate, s.reason " +
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

    @Query(value = "SELECT " +
            "q.id AS quotationId, " +
            "u.name AS userName, " +
            "p.name AS productName, " +
            "b.name AS brandName, " +
            "cate.name AS categoryName, " +
            "cbp.is_color AS isColor, " +  // Lấy has_color từ cate_brand_price
            "cbp.price AS price, " +
            "rd.note_color AS noteColor " +
            "FROM quotation q " +
            "JOIN rfq r ON q.rfq_id = r.id " +
            "JOIN user u ON r.create_by = u.id " +
            "JOIN rfq_detail rd ON r.id = rd.rfq_id " +
            "JOIN product p ON rd.product_id = p.id " +
            "JOIN brand b ON rd.brand_id = b.id " +
            "JOIN category cate ON rd.cate_id = cate.id " +
            "JOIN cate_brand_price cbp " +
            "ON cbp.cate_id = rd.cate_id " +
            "AND cbp.brand_id = rd.brand_id " + // Thêm điều kiện join cho has_color
            "WHERE " +
            "   (:search IS NULL OR :search = '' OR u.name LIKE %:search% OR p.name LIKE %:search% OR b.name LIKE %:search%) " +
            "   AND (:product IS NULL OR :product = '' OR p.name = :product) " +
            "   AND (:brand IS NULL OR :brand = '' OR b.name = :brand) " +
            "   AND (:category IS NULL OR :category = '' OR cate.name = :category)",
            countQuery = "SELECT COUNT(DISTINCT q.id) FROM quotation q " +
                    "JOIN rfq r ON q.rfq_id = r.id " +
                    "JOIN user u ON r.create_by = u.id " +
                    "JOIN rfq_detail rd ON r.id = rd.rfq_id " +
                    "JOIN product p ON rd.product_id = p.id " +
                    "JOIN brand b ON rd.brand_id = b.id " +
                    "JOIN category cate ON rd.cate_id = cate.id " +
                    "JOIN cate_brand_price cbp " +
                    "ON cbp.cate_id = rd.cate_id " +
                    "AND cbp.brand_id = rd.brand_id " + // Thêm điều kiện join cho has_color
                    "WHERE " +
                    "   (:search IS NULL OR :search = '' OR u.name LIKE %:search% OR p.name LIKE %:search% OR b.name LIKE %:search%) " +
                    "   AND (:product IS NULL OR :product = '' OR p.name = :product) " +
                    "   AND (:brand IS NULL OR :brand = '' OR b.name = :brand) " +
                    "   AND (:category IS NULL OR :category = '' OR cate.name = :category)",
            nativeQuery = true)
    Page<Object[]> findAllWithFilters(
            @Param("search") String search,
            @Param("product") String product,
            @Param("brand") String brand,
            @Param("category") String category,
            Pageable pageable
    );
}
