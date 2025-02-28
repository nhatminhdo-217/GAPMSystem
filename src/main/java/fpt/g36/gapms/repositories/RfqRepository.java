package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.Rfq;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RfqRepository extends JpaRepository<Rfq, Long> {

    @Query("select r from Rfq r LEFT JOIN r.solution s ON r.id = s.rfq.id  where r.createBy.id = :userId order by r.createAt desc")
    List<Rfq> getRfqByUserId(Long userId);

    @Query("SELECT r FROM Rfq r WHERE r.isSent = :isApproved")
    List<Rfq> getAllApprovedRfqs(@Param("isApproved") BaseEnum isApproved);

    @EntityGraph(attributePaths = {"rfqDetails", "rfqDetails.product", "rfqDetails.cate", "rfqDetails.brand", "createBy", "approvedBy", "solution"})
    Optional<Rfq> findById(Long id);
}
