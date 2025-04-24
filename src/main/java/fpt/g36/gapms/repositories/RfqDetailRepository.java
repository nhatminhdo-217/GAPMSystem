package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.RfqDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RfqDetailRepository extends JpaRepository<RfqDetail, Long> {

    @Query("select rd from RfqDetail rd where rd.rfq.id = :rfqId")
    List<RfqDetail> getAllRfqDetailByRfqId(Long rfqId);


        @Query("SELECT d FROM RfqDetail d JOIN FETCH d.rfq JOIN FETCH d.product JOIN FETCH d.brand JOIN FETCH d.cate WHERE d.id = :id")
    Optional<RfqDetail> findByIdWithDetails(@Param("id") Long id);

}
