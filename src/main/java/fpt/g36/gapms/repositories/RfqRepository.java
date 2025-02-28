package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Rfq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RfqRepository extends JpaRepository<Rfq, Long> {

    @Query("select r from Rfq r LEFT JOIN r.solution s ON r.id = s.rfq.id  where r.createBy.id = :userId order by CASE WHEN r.isSent = fpt.g36.gapms.enums.BaseEnum.NOT_APPROVED THEN 0 ELSE 1 END ,r.createAt desc")
    Page<Rfq> getRfqByUserId(Long userId, Pageable pageable);

}
