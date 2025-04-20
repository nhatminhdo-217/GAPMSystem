package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.quotation.QuotationDetailDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoProjection;
import fpt.g36.gapms.models.dto.quotation.QuotationInforCustomerProjection;
import fpt.g36.gapms.models.dto.quotation.QuotationListDTO;
import fpt.g36.gapms.models.entities.Product;
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

    @Query(value = "SELECT q.id as quotationId, u.name as username, c.name as companyname, c.tax_number, c.address as companyaddress, " +
            "q.is_accepted, s.id as solutionid ,p.name as productname, b.name as brandname, cate.name as categoryname, " +
            "rd.quantity, cbp.price, rd.note_color, r.expect_delivery_date as expectedDate, s.actual_delivery_date as actualDate " +
            "FROM quotation q " +
            "JOIN rfq r ON q.rfq_id = r.id " +
            "JOIN rfq_detail rd ON r.id = rd.rfq_id " +
            "JOIN product p ON rd.product_id = p.id " +
            "JOIN brand b ON rd.brand_id = b.id " +
            "JOIN category cate ON rd.cate_id = cate.id " +
            "JOIN cate_brand_price cbp ON cbp.cate_id = :cate_id \n" +
            "        AND cbp.brand_id = :brand_id \n" +
            "        AND cbp.is_color = :color\n" +
            "JOIN user u ON r.create_by = u.id " +
            "JOIN company_user cu ON u.id = cu.user_id " +
            "JOIN company c ON cu.company_id = c.id " +
            "JOIN solution s ON r.id = s.rfq_id " +
            "WHERE rd.id = :rfqDetailId", nativeQuery = true)
    QuotationInfoProjection findQuotationDetail(@Param("rfqDetailId") long rfqDetailId, Long brand_id, Long cate_id,@Param("color") Boolean color);

    @Query(value = "SELECT " +
            "q.id AS quotationId, " +
            "u.name AS userName, " +
            "q.is_accepted AS isAccepted, " +
            "p.name AS productName, " +
            "b.name AS brandName, " +
            "cate.name AS categoryName, " +
            "cbp.price AS price, " +
            "rd.note_color AS noteColor, " +
            "q.create_at AS createAt, " +
            "rd.quantity " +
            "FROM quotation q " +
            "JOIN rfq r ON q.rfq_id = r.id " +
            "JOIN user u ON r.create_by = u.id " +
            "JOIN rfq_detail rd ON r.id = rd.rfq_id " +
            "JOIN product p ON rd.product_id = p.id " +
            "JOIN brand b ON rd.brand_id = b.id " +
            "JOIN category cate ON rd.cate_id = cate.id " +
            "JOIN cate_brand_price cbp " +
            "ON cbp.cate_id = rd.cate_id " +
            "AND cbp.brand_id = rd.brand_id " +
            "WHERE " +
            "   (:search IS NULL OR :search = '' OR u.name LIKE %:search% OR p.name LIKE %:search% OR b.name LIKE %:search%) " +
            "   AND (:product IS NULL OR :product = '' OR p.name = :product) " +
            "   AND (:brand IS NULL OR :brand = '' OR b.name = :brand) " +
            "   AND (:category IS NULL OR :category = '' OR cate.name = :category) " +
            "   AND (:status IS NULL OR :status = '' OR q.is_accepted = :status)",
            countQuery = "SELECT COUNT(DISTINCT q.id) FROM quotation q " +
                    "JOIN rfq r ON q.rfq_id = r.id " +
                    "JOIN user u ON r.create_by = u.id " +
                    "JOIN rfq_detail rd ON r.id = rd.rfq_id " +
                    "JOIN product p ON rd.product_id = p.id " +
                    "JOIN brand b ON rd.brand_id = b.id " +
                    "JOIN category cate ON rd.cate_id = cate.id " +
                    "JOIN cate_brand_price cbp " +
                    "ON cbp.cate_id = rd.cate_id " +
                    "AND cbp.brand_id = rd.brand_id " +
                    "WHERE " +
                    "   (:search IS NULL OR :search = '' OR u.name LIKE %:search% OR p.name LIKE %:search% OR b.name LIKE %:search%) " +
                    "   AND (:product IS NULL OR :product = '' OR p.name = :product) " +
                    "   AND (:brand IS NULL OR :brand = '' OR b.name = :brand) " +
                    "   AND (:category IS NULL OR :category = '' OR cate.name = :category) " +
                    "   AND (:status IS NULL OR :status = '' OR q.is_accepted = :status)",
            nativeQuery = true)
    Page<Object[]> findAllWithFilters(
            @Param("search") String search,
            @Param("product") String product,
            @Param("brand") String brand,
            @Param("category") String category,
            @Param("status") String status,
            Pageable pageable
    );

    @Query(value = """
    select distinct q from Quotation q
    join q.rfq r
    join q.createdBy u
    join r.rfqDetails rd
    where (:search is null or lower(rd.product.name) like concat('%', lower(:search), '%')
    or lower(r.createBy.username) like concat('%', lower(:search), '%')
    or lower(q.createdBy.username) like concat('%', lower(:search), '%'))
    and (:status is null or q.isAccepted = :status)
   """,
            countQuery = """
    select count(distinct q) from Quotation q
    join q.rfq r
    join q.createdBy u
    join r.rfqDetails rd
    where (:search is null or lower(rd.product.name) like concat('%', lower(:search), '%')
    or lower(r.createBy.username) like concat('%', lower(:search), '%')
    or lower(q.createdBy.username) like concat('%', lower(:search), '%'))
    and (:status is null or q.isAccepted = :status)
   """)
    Page<Quotation> searchAndFilter(
            @Param("search") String search,
            @Param("status") BaseEnum status,
            Pageable pageable
    );

   /// for customer
    @Query(value = "SELECT \n" +
            "    q.id AS quotationId,\n" +
            "    r.id as rfqId,\n" +
            "q.is_canceled as isCanceled,"+
            "q.is_accepted as isAccepted,"+
            "    rd.quantity as quantity,\n" +
            "    u.name AS username, \n" +
            "    c.name AS companyname, \n" +
            "    c.tax_number, \n" +
            "    p.name AS productname, \n" +
            "    b.name AS brandname, \n" +
            "    cate.name AS categoryname, \n" +
            "    cbp.is_color AS isColor, \n" +
            "    cbp.price, \n" +
            "    rd.note_color, \n" +
            "    r.expect_delivery_date AS expectedDate, \n" +
            "    s.actual_delivery_date AS actualDate, \n" +
            "    s.reason \n" +
            "FROM quotation q\n" +
            "JOIN rfq r ON q.rfq_id = r.id\n" +
            "JOIN rfq_detail rd ON r.id = rd.rfq_id\n" +
            "JOIN product p ON rd.product_id = p.id\n" +
            "JOIN brand b ON rd.brand_id = b.id\n" +
            "JOIN category cate ON rd.cate_id = cate.id\n" +
            "JOIN cate_brand_price cbp ON cbp.cate_id = cate.id\n" +
            "JOIN user u ON r.create_by = u.id\n" +
            "JOIN company_user cu ON u.id = cu.user_id\n" +
            "JOIN company c ON cu.company_id = c.id\n" +
            "JOIN solution s ON r.id = s.rfq_id\n" +
            "WHERE r.id = :rfqId", nativeQuery = true)
    List<QuotationInforCustomerProjection> findQuotationCustomer(@Param("rfqId") long rfqId);


    Quotation findByRfqId(long rfqId);

    @Query(value = "SELECT q.id FROM quotation q WHERE q.rfq_id = :rfqId", nativeQuery = true)
    Long findQuotationIdByRfqId(long rfqId);



    @Query(value = "SELECT \n" +
            "    q.id AS quotationId,\n" +
            "    r.id as rfqId,\n" +
            "q.is_canceled as isCanceled,"+
            "q.is_accepted as isAccepted,"+
            "    rd.quantity as quantity,\n" +
            "    u.name AS username, \n" +
            "    c.name AS companyname, \n" +
            "    c.tax_number, \n" +
            "    p.name AS productname, \n" +
            "    b.name AS brandname, \n" +
            "    cate.name AS categoryname, \n" +
            "    cbp.is_color AS isColor, \n" +
            "    cbp.price, \n" +
            "    rd.note_color, \n" +
            "    r.expect_delivery_date AS expectedDate, \n" +
            "    s.actual_delivery_date AS actualDate, \n" +
            "    s.reason \n" +
            "FROM quotation q\n" +
            "JOIN rfq r ON q.rfq_id = r.id\n" +
            "JOIN rfq_detail rd ON r.id = rd.rfq_id\n" +
            "JOIN product p ON rd.product_id = p.id\n" +
            "JOIN brand b ON rd.brand_id = b.id\n" +
            "JOIN category cate ON rd.cate_id = cate.id\n" +
            "JOIN cate_brand_price cbp ON cbp.cate_id = :cate_id \n" +
            "        AND cbp.brand_id = :brand_id \n" +
            "        AND cbp.is_color = :color\n" +
            "JOIN user u ON r.create_by = u.id\n" +
            "JOIN company_user cu ON u.id = cu.user_id\n" +
            "JOIN company c ON cu.company_id = c.id\n" +
            "JOIN solution s ON r.id = s.rfq_id\n" +
            "WHERE rd.id = :rfqDetailId ", nativeQuery = true)
    QuotationInforCustomerProjection findQuotationCustomers(@Param("rfqDetailId") long rfqDetailId,Long brand_id, Long cate_id,@Param("color") Boolean color);
}
