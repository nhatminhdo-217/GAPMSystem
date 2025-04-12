package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.RfqDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RfqDetailRepository extends JpaRepository<RfqDetail, Long> {

    @Query("select rd from RfqDetail rd where rd.rfq.id = :rfqId")
    List<RfqDetail> getAllRfqDetailByRfqId(Long rfqId);
}
