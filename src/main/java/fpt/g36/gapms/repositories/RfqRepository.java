package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.Rfq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RfqRepository extends JpaRepository<Rfq, Long> {

    @Query("select r from Rfq r LEFT JOIN r.solution s ON r.id = s.rfq.id  where r.createBy.id = :userId order by CASE WHEN r.isSent = fpt.g36.gapms.enums.BaseEnum.NOT_APPROVED THEN 0 ELSE 1 END ,r.createAt desc")
    Page<Rfq> getRfqByUserId(Long userId, Pageable pageable);

    @Query("select r from Rfq r LEFT JOIN r.solution s ON r.id = s.rfq.id  where r.createBy.id = :userId order by r.createAt desc")
    List<Rfq> getRfqByUserId(Long userId);

    @Query("select r from Rfq r where r.id =:rfqId and r.createBy.id = :userId")
    Optional<Rfq> getRfqByRfqIdAndUserId(Long rfqId, Long userId);

    @Query("SELECT r FROM Rfq r WHERE r.isSent = :isApproved")
    List<Rfq> getAllApprovedRfqs(@Param("isApproved") BaseEnum isApproved);

    @EntityGraph(attributePaths = {"rfqDetails", "rfqDetails.product", "rfqDetails.cate", "rfqDetails.brand", "createBy", "approvedBy", "solution"})
    Optional<Rfq> findById(Long id);

    Rfq findBySolution_Id(Long solutionId);

    @Query("SELECT r FROM Rfq r LEFT JOIN r.solution s ON r.id = s.rfq.id " +
            "WHERE r.createBy.id = :userId AND r.isSent = :status " +
            "ORDER BY r.createAt DESC")
    Page<Rfq> getRfqsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BaseEnum status, Pageable pageable);

    //
    @Query("SELECT r FROM Rfq r LEFT JOIN r.solution s ON r.id = s.rfq.id " +
            "WHERE r.isSent = :status " +
            "ORDER BY r.createAt DESC")
    Page<Rfq> getRfqsByStatus(@Param("status") BaseEnum status, Pageable pageable);

    @Query("SELECT r FROM Rfq r LEFT JOIN r.solution s ON r.id = s.rfq.id " +
            "WHERE r.isSent = fpt.g36.gapms.enums.BaseEnum.APPROVED AND s IS NULL " +
            "ORDER BY r.createAt DESC")
    Page<Rfq> getApprovedRfqsWithoutSolution(Pageable pageable);

    @Query("SELECT r FROM Rfq r JOIN r.solution s ON r.id = s.rfq.id " +
            "WHERE r.isSent = fpt.g36.gapms.enums.BaseEnum.APPROVED " +
            "ORDER BY r.createAt DESC")
    Page<Rfq> getApprovedRfqsWithSolution(Pageable pageable);

    @Query("SELECT r FROM Rfq r LEFT JOIN r.solution s ON r.id = s.rfq.id " +
            "WHERE r.id = :rfqId AND r.isSent = :status")
    Optional<Rfq> getRfqByIdAndStatus(@Param("rfqId") Long rfqId, @Param("status") BaseEnum status);

    @Query("SELECT r FROM Rfq r LEFT JOIN r.solution s ON r.id = s.rfq.id " +
            "WHERE r.id = :rfqId AND r.isSent = fpt.g36.gapms.enums.BaseEnum.APPROVED AND s IS NULL")
    Optional<Rfq> getApprovedRfqWithoutSolutionById(@Param("rfqId") Long rfqId);

    @Query("SELECT r FROM Rfq r JOIN r.solution s ON r.id = s.rfq.id " +
            "WHERE r.id = :rfqId AND r.isSent = fpt.g36.gapms.enums.BaseEnum.APPROVED")
    Optional<Rfq> getApprovedRfqWithSolutionById(@Param("rfqId") Long rfqId);

}
