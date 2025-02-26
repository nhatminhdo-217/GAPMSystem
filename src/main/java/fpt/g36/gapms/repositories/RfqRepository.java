package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Rfq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RfqRepository extends JpaRepository<Rfq, Long> {

    @Query("select r from Rfq r LEFT JOIN r.solution s ON r.id = s.rfq.id  where r.createBy.id = :userId order by r.createAt desc")
    List<Rfq> getRfqByUserId(Long userId);

}
